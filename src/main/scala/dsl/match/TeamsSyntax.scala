package dsl.`match`

import model.Match.Side.{East, West}
import model.Match.{Player, Side, Team}

export TeamsSyntax.*

/** Collection of **high-level queries** on the tuple `(westTeam, eastTeam)` returned by `Match.teams`.
  *
  * Private helpers are kept hidden; only the DSL-facing extension methods are documented below.
  */
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
    /** Team defending the **West** goal. */
    def teamWest: Team = getWestTeam(teams)

    /** Team defending the **East** goal. */
    def teamEast: Team = getEastTeam(teams)

    /** Retrieves the opponent of the supplied `team`.
      *
      * @return
      *   the other element of the tuple
      */
    def opponentOf(team: Team): Team = getOpponent(teams, team)

    /** Flattened list of the 22 players on the pitch.
      */
    def players: List[Player] = teamWest.players ::: teamEast.players

    /** Team that owns the given `player`.
      */
    def teamOf(player: Player): Team = getTeamOf(teams, player)

    /** `Some(team)` if any side has the ball, `None` otherwise.
      */
    def withBall: Option[Team] = getTeamWithBall(teams)

    /** Maps both teams with the supplied function and returns the updated pair.
      */
    def map(f: Team => Team): (Team, Team) = (f(teams._1), f(teams._2))

  extension (side: Side)
    /** Fixed **seed value** used to generate correctly the players of a specific Team in his side initially (West = 1,
      * East = 2).
      */
    def seed: Int = side match
      case West => 1
      case East => 2
