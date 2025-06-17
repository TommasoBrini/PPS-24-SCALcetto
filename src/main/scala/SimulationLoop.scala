import model.Match.*
import update.Update.*
import view.View.SwingView

import javax.swing.Timer as SwingTimer
import java.awt.event.{ActionEvent, ActionListener}

object SimulationLoop:

  private var timer: Option[SwingTimer] = None

  def initialize(initialState: MatchState, frameRate: Int = 30): Unit =
    val delayMs: Int      = 1000 / frameRate
    var state: MatchState = initialState
    val view: SwingView   = new SwingView(state)
    view.onStart(start())
    view.onPause(pause())
    view.onResume(resume())

    val newTimer: SwingTimer = new SwingTimer(
      delayMs,
      new ActionListener:
        override def actionPerformed(e: ActionEvent): Unit =
          state = update(state, Event.StepEvent)
          view.render(state)
    )
    timer = Some(newTimer)

  private def start(): Unit =
    timer.foreach(_.start())

  private def pause(): Unit =
    timer.foreach(_.stop())

  private def resume(): Unit =
    timer.foreach(_.start())
