import model.Match.*
import SimulationLoop.*
import dsl.creation.GenSituation

/** new version -> v1.0.0
  */

@main def SCALcetto(): Unit =

  val initialState: Match = GenSituation.kickOff

  initialize(initialState, 50)
