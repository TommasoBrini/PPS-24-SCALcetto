import SimulationLoop.loop
import model.Model.*
import model.SimulationFactory

@main def SCALcetto =
  
  val initialState: SimulationState = SimulationFactory.initialSimulationState()
  loop(initialState, 1000)
