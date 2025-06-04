import SimulationLoop.loop
import model.*

@main def SCALcetto =
  val teamsList: List[Team] = List(
    Team(1, List(Player(0, Position(10, 10), false, None, Movement(Position(0, 0), 0)))),
    Team(2, List(Player(0, Position(15, 15), false, None, Movement(Position(0, 0), 0))))
  )
  val simulationState: SimulationState = SimulationState(teamsList, Ball(Position(30, 30), Movement(Position(0, 0), 0)))
  loop(simulationState, 1000)
