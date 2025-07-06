package dsl.creation.build

import model.Match.*

case class MatchBuilder(
    teams: (Team, Team),
    ball: Ball,
    matchSituation: MatchSituation
):
  def withTeams(teamA: Team, teamB: Team): MatchBuilder =
    copy(teams = (teamA, teamB))

  def withBall(givenBall: Ball): MatchBuilder =
    copy(ball = givenBall)

  def withSituation(situation: MatchSituation): MatchBuilder =
    copy(matchSituation = situation)

  def build(): Match = ???
