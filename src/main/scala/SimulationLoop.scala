import model.Match.*
import update.Update.*
import view.View.SwingView
import config.UIConfig.*
import dsl.creation.GenSituation

import javax.swing.Timer as SwingTimer
import java.awt.event.{ActionEvent, ActionListener}

object SimulationLoop:

  private var timer: Option[SwingTimer] = None
  private var view: SwingView           = _
  private var state: Match              = _

  def initialize(initialState: Match, initialFrameRate: Int = 30): Unit =
    val delayMs: Int = 1000 / initialFrameRate
    state = initialState
    view = new SwingView(state)
    view.onStart(start(delayMs))
    view.onPause(pause())
    view.onResume(resume())
    view.onReset(reset())
    view.render(state)

  private def start(delayMs: Int): Unit =
    createTimer(delayMs)
    timer.foreach(_.start())

  private def pause(): Unit =
    timer.foreach(_.stop())

  private def resume(): Unit =
    timer.foreach(_.start())

  private def reset(): Unit =
    pause()
    state = GenSituation.kickOff
    view.render(state)

  private def createTimer(delayMs: Int): Unit =
    val newTimer: SwingTimer = new SwingTimer(
      delayMs,
      (e: ActionEvent) =>
        state = update(state)
        view.render(state)
    )
    timer = Some(newTimer)
