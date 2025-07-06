package dsl.creation

import dsl.creation.build.{BallBuilder, MatchBuilder, PlayerBuilder, TeamBuilder}
import model.Match.{Match, Side}

object CreationSyntax:
  def newMatch(body: MatchBuilder ?=> Unit): Match =
    given scope: MatchBuilder = MatchBuilder() // create mutable builders
    body(using scope)
    scope.build()

  def team(side: Side)(using mb: MatchBuilder): TeamBuilder =
    mb.team(side)

  def ball(using mb: MatchBuilder): BallBuilder =
    mb.ball

  def player(id: Int)(using tb: TeamBuilder): PlayerBuilder =
    tb.player(id)
