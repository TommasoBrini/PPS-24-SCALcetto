package update

import model.Match.{Match, Score, Side}
import model.Match.Side.*
import monads.States.State
import decide.Decide.decideStep
import validate.Validate.validateStep
import act.Act.actStep
import config.UIConfig.*
import dsl.creation.SituationGenerator
import dsl.space.PositionSyntax.*
import dsl.space.MovementSyntax.*
import dsl.MatchSyntax.*

import scala.annotation.tailrec

object Update:
  enum Event:
    case BallOut, GoalEast, GoalWest

  def update(state: Match): Match =
    val updateFlow: State[Match, Option[Event]] =
      for
        _     <- decideStep
        _     <- validateStep
        event <- actStep
      yield event
    val (updated, event) = updateFlow.run(state)
    handleEvent(updated, event)

  import Event.*
  private def handleEvent(state: Match, event: Option[Event]): Match =
    event match
      case Some(BallOut) =>
        val bounceType = state.ball.position getBounce (fieldWidth, fieldHeight)
        state.copy(ball = state.ball.copy(movement = state.ball.movement getMovementFrom bounceType))
      case Some(GoalEast) =>
        println("East Goal!!!")
        SituationGenerator.kickOff(state.score.eastGoal, West)
      case Some(GoalWest) =>
        println("West Goal!!!")
        SituationGenerator.kickOff(state.score.westGoal, East)
      case _ => state
