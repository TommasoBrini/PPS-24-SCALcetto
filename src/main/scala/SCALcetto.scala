import model.Model.*
import SimulationLoop.*
import init.GameInitializer

@main def SCALcetto =

  val initialState: MatchState = GameInitializer.initialSimulationState()

  initialize(initialState, 50)
