package update

import model.Match.*
import Event.*
import init.GameInitializer.initialSimulationState
import decide.Decide.*
import validate.Validate.*
import act.Act.{act, isBallOut, isGoal}
import config.UIConfig.*
import dsl.SpaceSyntax.*

import scala.annotation.tailrec

object Update:
  @tailrec
  def update(state: MatchState, event: Event): MatchState = event match
    case Step =>
      update(state, Decide)
    case Decide =>
      val newState: MatchState = decide(state)
      update(newState, Validate)
    case Validate =>
      val newState: MatchState = state.validate()
      update(newState, Act)
    case Act =>
      val newState: MatchState = state.act()
      if newState.isGoal then update(newState, Goal)
      else if newState.isBallOut then update(newState, BallOut)
      else newState
    case BallOut =>
      val bounceType = state.ball.position getBounce (fieldWidth, fieldHeight)
      MatchState(state.teams, state.ball.copy(movement = state.ball.movement getMovementFrom bounceType))
    case Goal    => update(state, Restart)
    case Restart => initialSimulationState()
