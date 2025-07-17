package dsl

import config.UIConfig
import space.PositionSyntax.isOutOfField
import model.Match.{MatchState, Player}

object MatchSyntax:
  export `match`.PlayerSyntax.*
  export `match`.TeamsSyntax.*
  export `match`.ScoreSyntax.*

  extension (state: MatchState)
    /** Applies the given function to the match state if the condition is true. Used to chain conditional updates in a
      * DSL style.
      *
      * @param condition
      *   predicate to test against the match state
      * @param f
      *   transformation to apply if the condition is true
      * @return
      *   the updated or original match state
      */
    def applyIf(condition: MatchState => Boolean)(f: MatchState => MatchState): MatchState =
      if condition.apply(state) then f(state) else state

    /** @return
      *   the players from both teams
      */
    def players: List[Player] = state.teams._1.players ++ state.teams._2.players

    /** @return
      *   true if the ball position is out of the field bounds, false otherwise
      */
    def isBallOut: Boolean =
      state.ball.position.isOutOfField
