package dsl.game

import model.Match.Side.{East, West}
import model.Match.{Player, Side, Team}

export TeamsSyntax.*

object TeamsSyntax:
  private def getOpponent(teams: (Team, Team), myTeam: Team): Team =
    if teams.teamWest != myTeam then teams.teamWest
    else teams.teamEast

  private def getTeamOf(teams: (Team, Team), player: Player): Team =
    if teams.teamWest.players.contains(player) then teams.teamWest
    else teams.teamEast

  private def getTeamWithBall(teams: (Team, Team)): Option[Team] =
    if teams.teamWest.hasBall then Option(teams.teamWest)
    else if teams.teamEast.hasBall then Option(teams.teamEast)
    else Option.empty

  extension (teams: (Team, Team))
    def teamWest: Team                     = teams._1
    def teamEast: Team                     = teams._2
    def opponentOf(team: Team): Team       = getOpponent(teams, team)
    def players: List[Player]              = teamWest.players ::: teamEast.players
    def teamOf(player: Player): Team       = getTeamOf(teams, player)
    def withBall: Option[Team]             = getTeamWithBall(teams)
    def map(f: Team => Team): (Team, Team) = (f(teams._1), f(teams._2))

  extension (side: Side)
    def seed: Int = side match
      case West => 1
      case East => 2
