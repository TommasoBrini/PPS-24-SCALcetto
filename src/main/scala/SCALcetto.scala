import SimulationLoop.loop
import model.Model.*
import update.factory.SimulationFactory

@main def SCALcetto =

  val initialState: SimulationState = SimulationFactory.initialSimulationState()
  loop(initialState, 1000, 50)
