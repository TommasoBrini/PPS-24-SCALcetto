import model.Match.*
import SimulationLoop.*
import init.GameInitializer

/** new version -> v1.0.0
  */

@main def SCALcetto =

  val initialState: MatchState = GameInitializer.initialSimulationState()

  initialize(initialState, 50)
