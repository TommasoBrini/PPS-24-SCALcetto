import model.Match.*
import SimulationLoop.*
import dsl.creation.SituationGenerator

/** new version -> v3.0.0
  */

@main def SCALcetto(): Unit =

  val initialState: Match = SituationGenerator.kickOff(Score.init())

  initialize(initialState, 50)
