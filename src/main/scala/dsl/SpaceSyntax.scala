package dsl

import config.UIConfig
import model.Match.{Player, Team}
import model.Space.*
import model.Space.Bounce.*

import scala.annotation.targetName
import scala.util.Random

object SpaceSyntax {
  export DirectionSyntax.*
  export MovementSyntax.*
  export PositionSyntax.*
  export TeamsSyntax.*

  object PositionSyntax:
    private def checkAxisOutOfBound(coordinate: Int, axisBound: Int): Boolean =
      coordinate < 0 || coordinate > axisBound

    private def calculateMovedPosition(p: Position, m: Movement): Position =
      val dx = m.direction.x * m.speed
      val dy = m.direction.y * m.speed
      val x  = (p.x + dx).round.toInt
      val y  = (p.y + dy).round.toInt
      Position(x, y)

    extension (p: Position)
      def isOutOfBound(widthBound: Int, heightBound: Int): Boolean =
        checkAxisOutOfBound(p.x, widthBound) || checkAxisOutOfBound(p.y, heightBound)
      @targetName("applyMovement")
      def +(m: Movement): Position = calculateMovedPosition(p, m)

      def getBounce(widthBound: Int, heightBound: Int): Bounce =
        if checkAxisOutOfBound(p.x, widthBound) && checkAxisOutOfBound(p.y, heightBound) then ObliqueBounce
        else if checkAxisOutOfBound(p.x, widthBound) then HorizontalBounce
        else VerticalBounce

      def isInsideGoal: Boolean =
        val firstGoalPost: Int  = (UIConfig.fieldHeight - UIConfig.goalHeight) / 2
        val secondGoalPost: Int = firstGoalPost + UIConfig.goalHeight
        (p.x <= 0 || p.x >= UIConfig.fieldWidth) && (p.y >= firstGoalPost && p.y <= secondGoalPost)

      def clampToField: Position =
        val clampedX = Math.max(0, Math.min(p.x, UIConfig.fieldWidth))
        val clampedY = Math.max(0, Math.min(p.y, UIConfig.fieldHeight))
        Position(clampedX, clampedY)

  object DirectionSyntax:
    private def bounceCalculator(bounce: Bounce, x: Double, y: Double): Direction = bounce match
      case ObliqueBounce    => Direction(-x, -y)
      case HorizontalBounce => Direction(-x, y)
      case VerticalBounce   => Direction(x, -y)
    extension (d: Direction)
      def getDirectionFrom(bounce: Bounce): Direction = bounceCalculator(bounce, d.x, d.y)
      def jitter: Direction = Direction(d.x + Random.between(-0.2, 0.2), d.y + Random.between(-0.2, 0.2))

  object MovementSyntax:
    extension (m: Movement)
      def bool: Int                                 = 4
      def getMovementFrom(bounce: Bounce): Movement = Movement(m.direction getDirectionFrom bounce, m.speed)
      @targetName("applyScale")
      def *(factor: Int): Movement = Movement(m.direction, m.speed * factor)

  // TODO fix logix of teams from list to tuple

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
}
