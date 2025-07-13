import model.Match.*
import SimulationLoop.*
import dsl.creation.SituationGenerator

@main def SCALcetto(): Unit =

  val initialState: Match = SituationGenerator.kickOff(Score.init())

  initialize(initialState, 50)
