package update

import model.Match.*
import init.GameInitializer.initialSimulationState
import decide.Decide.*
import validate.Validate.*
import act.Act.{act, actStep, isBallOut, isGoal}
import config.UIConfig.*
import dsl.SpaceSyntax.*
import monads.States.*

import scala.annotation.tailrec

object Update:
  enum Event:
    case BallOut, Goal, Restart

  def update(state: Match): Match =
    val updateFlow: State[Match, Unit] =
      for
        _     <- decideStep
        _     <- validateStep
        event <- actStep
        end   <- handleEvent(event)
      yield end
    val (updated, _) = updateFlow.run(state)
    updated

  import Event.*
  private def handleEvent(event: Option[Event]): State[Match, Unit] =
    State(state => {
      (
        event match
          case Some(BallOut) =>
            val bounceType = state.ball.position getBounce (fieldWidth, fieldHeight)
            state.copy(ball = state.ball.copy(movement = state.ball.movement getMovementFrom bounceType))
          case Some(Goal) =>
            println("Goal!!!")
            initialSimulationState()
          case Some(Restart) => initialSimulationState()
          case _             => state
        ,
        ()
      )
    })
