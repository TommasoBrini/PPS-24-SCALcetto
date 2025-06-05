import model.Model.{Event, SimulationState}
import update.Update.*
import view.View.SwingView

import javax.swing.Timer as SwingTimer
import java.awt.event.{ActionEvent, ActionListener}

object SimulationLoop:
  def loop(model: SimulationState, nStep: Int): Unit =
    val view           = new SwingView(model)
    var current        = model
    var remainingSteps = nStep

    val timer = new SwingTimer(
      500,
      new ActionListener {
        override def actionPerformed(e: ActionEvent): Unit = {
          if remainingSteps > 0 then
            // current = update(current, Event.Step)
            view.render(current)
            remainingSteps -= 1
        }
      }
    )

    timer.start()
