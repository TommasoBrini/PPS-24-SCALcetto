package dsl.creation.build

import model.Match.*

final class MatchBuilder:
  private val teams = scala.collection.mutable.ListBuffer[TeamBuilder]()
  private val b     = BallBuilder()

  def team(side: Side): TeamBuilder =
    val t = TeamBuilder(side)
    teams += t
    t

  def ball: BallBuilder = b

  def build(): Match =
    require(teams.size == 2, "exactly two teams required")
    Match((teams.head.build(), teams.last.build()), ball.build())
