package dsl.game

import model.Match.Side.{East, West}
import model.Match.{Player, Side, Team}

export TeamsSyntax.*

object TeamsSyntax:
  private def getOpponent(teams: (Team, Team), myTeam: Team): Team =
    if teams.teamWest != myTeam then teams.teamWest
    else teams.teamEast

  private def getTeamOf(teams: (Team, Team), player: Player): Team =
    if teams.teamWest.players.exists(_.id == player.id) then teams.teamWest
    else teams.teamEast

  private def getTeamWithBall(teams: (Team, Team)): Option[Team] =
    if teams.teamWest.hasBall then Option(teams.teamWest)
    else if teams.teamEast.hasBall then Option(teams.teamEast)
    else Option.empty

  private def getWestTeam(teams: (Team, Team)): Team = (teams._1.side, teams._2.side) match
    case (West, East) => teams._1
    case (East, West) => teams._2
    case _ =>
      throw new IllegalArgumentException(
        s"Unexpected sides: ${teams._1.side} & ${teams._2.side}"
      )

  private def getEastTeam(teams: (Team, Team)): Team = (teams._1.side, teams._2.side) match
    case (West, East) => teams._2
    case (East, West) => teams._1
    case _ =>
      throw new IllegalArgumentException(
        s"Unexpected sides: ${teams._1.side} & ${teams._2.side}"
      )

  extension (teams: (Team, Team))
    def teamWest: Team                     = getWestTeam(teams)
    def teamEast: Team                     = getEastTeam(teams)
    def opponentOf(team: Team): Team       = getOpponent(teams, team)
    def players: List[Player]              = teamWest.players ::: teamEast.players
    def teamOf(player: Player): Team       = getTeamOf(teams, player)
    def withBall: Option[Team]             = getTeamWithBall(teams)
    def map(f: Team => Team): (Team, Team) = (f(teams._1), f(teams._2))

  extension (side: Side)
    def seed: Int = side match
      case West => 1
      case East => 2
