package dsl.space

import config.UIConfig
import model.Space.{Bounce, Movement, Position}
import model.Space.Bounce.*

import scala.annotation.targetName

/** Enriches [[model.Space.Position]] with extra geometry such as movement application, bounce detection and goal-line
  * checks.
  */
object PositionSyntax:
  private def checkAxisOutOfBound(coordinate: Int, axisBound: Int): Boolean =
    coordinate < 0 || coordinate > axisBound

  private def calculateMovedPosition(p: Position, m: Movement): Position =
    val dx = m.direction.x * m.speed
    val dy = m.direction.y * m.speed
    val x  = (p.x + dx).round.toInt
    val y  = (p.y + dy).round.toInt
    Position(x, y)

  private def isInsideGoalWest(p: Position): Boolean =
    val firstGoalPost: Int  = (UIConfig.fieldHeight - UIConfig.goalHeight) / 2
    val secondGoalPost: Int = firstGoalPost + UIConfig.goalHeight
    (p.x <= 0) && (p.y >= firstGoalPost && p.y <= secondGoalPost)

  private def isInsideGoalEast(p: Position): Boolean =
    val firstGoalPost: Int  = (UIConfig.fieldHeight - UIConfig.goalHeight) / 2
    val secondGoalPost: Int = firstGoalPost + UIConfig.goalHeight
    (p.x >= UIConfig.fieldWidth) && (p.y >= firstGoalPost && p.y <= secondGoalPost)

  extension (p: Position)
    /** Checks whether the position is **outside** the rectangular field bounds.
      *
      * @param widthBound
      *   inclusive maximum `x`
      * @param heightBound
      *   inclusive maximum `y`
      */
    def isOutOfBound(widthBound: Int, heightBound: Int): Boolean =
      checkAxisOutOfBound(p.x, widthBound) || checkAxisOutOfBound(p.y, heightBound)

    /** Applies the supplied movement vector and returns the **new position**.
      *
      * Alias `+` keeps arithmetic intuition: val next = current + movement
      */
    @targetName("applyMovement")
    def +(m: Movement): Position = calculateMovedPosition(p, m)

    /** Classifies the rebound when the position exceeds the pitch limits.
      */
    def getBounce(widthBound: Int, heightBound: Int): Bounce =
      if checkAxisOutOfBound(p.x, widthBound) && checkAxisOutOfBound(p.y, heightBound) then ObliqueBounce
      else if checkAxisOutOfBound(p.x, widthBound) then HorizontalBounce
      else VerticalBounce

    /** `true` when the point lies inside the **East** goal mouth.
      */
    def goalWest: Boolean = isInsideGoalEast(p)

    /** `true` when the point lies inside the **West** goal mouth.
      */
    def goalEast: Boolean = isInsideGoalWest(p)

    /** Clamps the coordinates so that they remain inside the playable field.
      */
    def clampToField: Position =
      val clampedX = Math.max(0, Math.min(p.x, UIConfig.fieldWidth))
      val clampedY = Math.max(0, Math.min(p.y, UIConfig.fieldHeight))
      Position(clampedX, clampedY)

    /** Euclidean distance to another position.
      */
    def distanceFrom(p2: Position): Double = Math.hypot(p2.x - p.x, p2.y - p.y)
