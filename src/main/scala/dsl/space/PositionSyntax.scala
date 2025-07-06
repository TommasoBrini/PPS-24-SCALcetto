package dsl.space

import config.UIConfig
import model.Space.{Bounce, Movement, Position}
import model.Space.Bounce.*

import scala.annotation.targetName

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
    def distanceFrom(p2: Position): Double = Math.hypot(p2.x - p.x, p2.y - p.y)
