package dsl

import config.UIConfig
import space.PositionSyntax.isOutOfBound
import model.Match.{Match, Player}

object MatchSyntax:
  export game.PlayerSyntax.*
  export game.TeamsSyntax.*
  export game.ScoreSyntax.*

  extension (state: Match)
    def mapIf(condition: Match => Boolean)(f: Match => Match): Match =
      if condition.apply(state) then f(state) else state
    def players: List[Player] = state.teams._1.players ++ state.teams._2.players
    def isBallOut: Boolean =
      state.ball.position.isOutOfBound(UIConfig.fieldWidth, UIConfig.fieldHeight)
