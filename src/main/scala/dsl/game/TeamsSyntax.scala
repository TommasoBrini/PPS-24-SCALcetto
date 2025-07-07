package dsl.game

import model.Match.Side.{East, West}
import model.Match.{Player, Side, Team}

export TeamsSyntax.*

object TeamsSyntax:
  private def getOpponent(teams: (Team, Team), myTeam: Team): Team =
    if teams.teamA != myTeam then teams.teamA
    else teams.teamB

  private def getTeamOf(teams: (Team, Team), player: Player): Team =
    if teams.teamA.players.contains(player) then teams.teamA
    else teams.teamB

  private def getTeamWithBall(teams: (Team, Team)): Option[Team] =
    if teams.teamA.hasBall then Option(teams.teamA)
    else if teams.teamB.hasBall then Option(teams.teamB)
    else Option.empty

  extension (teams: (Team, Team))
    def teamA: Team                        = teams._1
    def teamB: Team                        = teams._2
    def opponentOf(team: Team): Team       = getOpponent(teams, team)
    def players: List[Player]              = teamA.players ::: teamB.players
    def teamOf(player: Player): Team       = getTeamOf(teams, player)
    def withBall: Option[Team]             = getTeamWithBall(teams)
    def map(f: Team => Team): (Team, Team) = (f(teams._1), f(teams._2))

  extension (side: Side)
    def seed: Int = side match
      case West => 1
      case East => 2
