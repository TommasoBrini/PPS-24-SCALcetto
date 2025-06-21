package update

import model.Match.*
import Event.*
import init.GameInitializer.initialSimulationState
import decide.Decide.*
import validate.Validate.*
import act.Act.{executeAction, isAGoal, isBallOut}
import config.FieldConfig
import config.FieldConfig.{heightBound, widthBound}

import scala.annotation.tailrec

object Update:
  @tailrec
  def update(state: MatchState, event: Event): MatchState = event match
    case StepEvent =>
      update(state, DecideEvent)
    case DecideEvent =>
      val newState: MatchState = decide(state)
      update(newState, ValidateEvent)
    case ValidateEvent =>
      val newState: MatchState = validate(state)
      update(newState, ActEvent)
    case ActEvent =>
      val newState: MatchState = executeAction(state)
      if isAGoal(newState) then update(newState, GoalEvent)
      else if isBallOut(newState) then update(newState, BallOutEvent)
      else newState
    case BallOutEvent =>
      val bounceType = state.ball.position.getBounce(widthBound, heightBound)
      MatchState(state.teams, state.ball.copy(movement = state.ball.movement.bounce(bounceType)))
    case GoalEvent    => update(state, RestartEvent)
    case RestartEvent => initialSimulationState()
