package update.act

import dsl.MatchSyntax.*
import dsl.action.ActionProcessor.*
import model.Match.MatchState
import monads.States.State
import update.Update.Event

object Act:
  /** Execute a single step of the act phase.
    *
    * @return
    *   a new match state and the optional event occurred during the step
    */
  def actStep: State[MatchState, Option[Event]] =
    State(state => {
      val updatedState = state
        .applyIf(existsSuccessfulTackle)(_.tackleBallCarrier())
        .applyIf(isPossessionChanging)(_.updateBallPossession())
        .updateMovements()
        .moveEntities()
      (updatedState, updatedState.detectEvent())
    })
