package update

import model.Event.{Decision, Step}
import model.{Ball, Event, Position, SimulationState}

object Update:
  def update(simulationState: SimulationState, event: Event): SimulationState = event match
    case Step => 
      val newSimulationState: SimulationState = SimulationState(simulationState.playerList, Ball(Position(simulationState.ball.position.x + 1, simulationState.ball.position.y)))
      newSimulationState
    case Decision => simulationState

