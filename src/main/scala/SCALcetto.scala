import SimulationLoop.loop
import model.*

@main def SCALcetto =
  val playerList: List[Player]         = List(Player(0, Position(10, 10), false), Player(1, Position(5, 30), false))
  val simulationState: SimulationState = SimulationState(playerList, Ball(Position(30, 30)))
  loop(simulationState, 1000)
