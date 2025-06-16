package update

import model.Match.*
import Event.*
import init.GameInitializer.initialSimulationState
import decide.Decide.*
import act.Act.{executeAction, isAGoal}

import scala.annotation.tailrec

object Update:
  @tailrec
  def update(simulationState: MatchState, event: Event): MatchState = event match
    case StepEvent => update(simulationState, DecideEvent)
    case DecideEvent =>
      val newSimulationState: MatchState = takeDecisions(simulationState)
      update(newSimulationState, ActEvent)
    case ActEvent =>
      val state = executeAction(simulationState)
      if isAGoal(state) then update(state, GoalEvent)
      else state
    case GoalEvent    => update(simulationState, RestartEvent)
    case RestartEvent => initialSimulationState()
    case _            => simulationState
