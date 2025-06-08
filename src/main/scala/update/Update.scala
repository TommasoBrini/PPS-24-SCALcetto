package update

import model.Model.*
import Event.*
import update.Decide.*
import update.factory.SimulationFactory.*

import scala.annotation.tailrec
import scala.util.Random

object Update:
  // chain of responsibility
  @tailrec
  def update(simulationState: SimulationState, event: Event): SimulationState = event match
    case Step => update(simulationState, Decide)
    case Decide =>
      val newSimulationState: SimulationState = takeDecisions(simulationState)
      update(newSimulationState, Act)
    case Act     => ??? // ACT THE NEXT ACTION FOR EACH PLAYER, AND UPDATE THE STATE OF THE SIMULATION  -- EMI
    case Goal    => update(simulationState, Restart)
    case Restart => initialSimulationState()
    case _       => simulationState
