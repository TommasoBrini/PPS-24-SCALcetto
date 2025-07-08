package dsl.creation.build

import model.Match.*

import scala.collection.mutable.ListBuffer

final class MatchBuilder:
  private val teams: ListBuffer[TeamBuilder] = ListBuffer[TeamBuilder]()
  private val matchBall: BallBuilder         = BallBuilder()

  def team(side: Side): TeamBuilder =
    val newTeam: TeamBuilder = TeamBuilder(side)
    teams += newTeam
    newTeam

  def ball: BallBuilder = matchBall

  def build(): Match =
    require(teams.size == 2, "exactly two teams required")
    Match((teams.head.build(), teams.last.build()), ball.build())
