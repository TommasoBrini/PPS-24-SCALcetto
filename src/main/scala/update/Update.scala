package update

import model.Match.Match
import monads.States.State
import decide.Decide.decideStep
import validate.Validate.validateStep
import act.Act.actStep
import config.UIConfig.*
import dsl.creation.GenSituation
import dsl.space.PositionSyntax.*
import dsl.space.MovementSyntax.*

import scala.annotation.tailrec

object Update:
  enum Event:
    case BallOut, Goal

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
      case Some(Goal) =>
        println("Goal!!!")
        GenSituation.kickOff
      case _ => state
