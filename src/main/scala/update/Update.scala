package update

import model.Event.{Decision, Step}
import model.{Ball, Event, Position, SimulationState}

import scala.util.Random

object Update:
  def update(simulationState: SimulationState, event: Event): SimulationState = event match
    case Step =>
      val random: Random = new Random()
      random.nextInt(4) match
        case 0 => simulationState.copy(ball =
            Ball(Position(simulationState.ball.position.x + 1, simulationState.ball.position.y))
          )
        case 1 => simulationState.copy(ball =
            Ball(Position(simulationState.ball.position.x - 1, simulationState.ball.position.y))
          )
        case 2 => simulationState.copy(ball =
            Ball(Position(simulationState.ball.position.x, simulationState.ball.position.y + 1))
          )
        case 3 => simulationState.copy(ball =
            Ball(Position(simulationState.ball.position.x, simulationState.ball.position.y - 1))
          )

    case Decision => simulationState
