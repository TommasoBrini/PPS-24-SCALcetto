import model.{Ball, Event, Player, Position, SimulationState}
import update.Update.*
import view.View.render

object SimulationLoop:
  def loop(model: SimulationState, nStep: Int): Unit =
    if nStep > 0 then
      val newState = update(model, Event.Step)
      render(newState)
      loop(newState, nStep - 1)
