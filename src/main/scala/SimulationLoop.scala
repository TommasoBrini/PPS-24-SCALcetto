import model.Match.*
import update.Update.*
import view.View.SwingView
import init.GameInitializer
import config.UIConfig.*

import javax.swing.Timer as SwingTimer
import java.awt.event.{ActionEvent, ActionListener}

object SimulationLoop:

  private var timer: Option[SwingTimer] = None
  private var currentState: MatchState  = _
  private var view: SwingView           = _
  private var frameRate: Int            = defaultFrameRate
  private var isRunning: Boolean        = false

  def initialize(initialState: MatchState, initialFrameRate: Int = defaultFrameRate): Unit =
    frameRate = initialFrameRate
    currentState = initialState
    view = new SwingView(currentState)

    // Set up event handlers
    view.onStart(start())
    view.onPause(pause())
    view.onResume(resume())
    view.onReset(reset())

    // Initial render
    view.render(currentState)

  private def start(): Unit =
    if !isRunning then
      createTimer()
      timer.foreach(_.start())
      isRunning = true

  private def pause(): Unit =
    timer.foreach(_.stop())
    isRunning = false

  private def resume(): Unit =
    if !isRunning then
      timer.foreach(_.start())
      isRunning = true

  private def reset(): Unit =
    pause()
    currentState = GameInitializer.initialSimulationState()
    view.render(currentState)

  private def createTimer(): Unit =
    val delayMs: Int = 1000 / frameRate
    val newTimer: SwingTimer = new SwingTimer(
      delayMs,
      new ActionListener:
        override def actionPerformed(e: ActionEvent): Unit =
          currentState = update(currentState, Event.StepEvent)
          view.render(currentState)
    )
    timer = Some(newTimer)
