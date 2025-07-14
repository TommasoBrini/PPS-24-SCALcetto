package dsl

import config.UIConfig
import space.PositionSyntax.isOutOfBound
import model.Match.{Match, Player}

object MatchSyntax:
  export game.PlayerSyntax.*
  export game.TeamsSyntax.*
  export game.ScoreSyntax.*

  extension (state: Match)
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
    def applyIf(condition: Match => Boolean)(f: Match => Match): Match =
      if condition.apply(state) then f(state) else state

    /** @return
      *   the players from both teams
      */
    def players: List[Player] = state.teams._1.players ++ state.teams._2.players

    /** @return
      *   true if the ball position is out of the field bounds, false otherwise
      */
    def isBallOut: Boolean =
      state.ball.position.isOutOfBound(UIConfig.fieldWidth, UIConfig.fieldHeight)
