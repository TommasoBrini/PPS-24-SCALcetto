import model.Model.*
import update.factory.SimulationFactory
import SimulationLoop.*

@main def SCALcetto =

  val initialState: SimulationState = SimulationFactory.initialSimulationState()

  initialize(initialState, 50)
