import SimulationLoop.loop
import model.{Ball, Event, Player, Position, SimulationState}


@main def SCALcetto =
  val playerList: List[Player] = List(Player(0, Position(0, 0), false))
  val simulationState: SimulationState = SimulationState(playerList, Ball(Position(0,0)))
  loop(simulationState, 5)
  