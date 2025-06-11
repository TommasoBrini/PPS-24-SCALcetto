package update

import model.Model.*
import Event.*
import update.Decide.*
import update.Act.{act, isAGoal}
import update.factory.SimulationFactory.*

import scala.annotation.tailrec

object Update:
  @tailrec
  def update(simulationState: SimulationState, event: Event): SimulationState = event match
    case Step => update(simulationState, Decide)
    case Decide =>
      val newSimulationState: SimulationState = takeDecisions(simulationState)
      update(newSimulationState, Act)
    case Act =>
      val state = act(simulationState)
      if isAGoal(state) then update(state, Goal)
      else state
    case Goal    => update(simulationState, Restart)
    case Restart => initialSimulationState()
    case _       => simulationState
