package model

import config.FieldConfig

import scala.annotation.targetName
import scala.util.Random

object Space:

  opaque type Position = (Int, Int)
  object Position:
    def apply(x: Int, y: Int): Position = (x, y)
  extension (p: Position)
    def x: Int                            = p._1
    def y: Int                            = p._2
    def getDistance(p2: Position): Double = Math.hypot(p2.x - p.x, p2.y - p.y)

  opaque type Direction = (Double, Double)
  object Direction:
    def apply(x: Int, y: Int): Direction       = (x, y)
    def apply(x: Double, y: Double): Direction = (x, y)
    def none: Direction                        = (0, 0)
  extension (d: Direction)
    def x: Double = d._1
    def y: Double = d._2

  extension (p: Position)
    def getDirection(to: Position): Direction =
      val dx = to.x - p.x
      val dy = to.y - p.y
      // Hypot with both argument zero returns zero, implying a division by zero returning Double.NaN
      if dx == 0 && dy == 0 then Direction.none
      else (dx / Math.hypot(dx, dy), dy / Math.hypot(dx, dy))

  case class Movement(direction: Direction, speed: Int)
  object Movement:
    def still: Movement = Movement(Direction.none, 0)

    extension (m: Movement)
      @targetName("applyScale")
      def *(factor: Int): Movement =
        Movement(m.direction, m.speed * factor)

  extension (p: Position)
    @targetName("applyMovement")
    def +(m: Movement): Position =
      val dx = m.direction.x * m.speed
      val dy = m.direction.y * m.speed
      val x  = (p.x + dx).round.toInt
      val y  = (p.y + dy).round.toInt
      Position(x, y)

  enum Bounce:
    case Vertical, Horizontal, Both

  extension (i: Int)
    def isOutOfBound(bound: Int): Boolean = i < 0 || i > bound

  import Bounce.*
  extension (p: Position)
    def isOutOfBound(widthBound: Int, heightBound: Int): Boolean =
      p.x.isOutOfBound(widthBound) || p.y.isOutOfBound(heightBound)
    def getBounce(widthBound: Int, heightBound: Int): Bounce =
      if p.x.isOutOfBound(widthBound) && p.y.isOutOfBound(heightBound) then Both
      else if p.x.isOutOfBound(widthBound) then Horizontal
      else Vertical
    def isGoal: Boolean =
      val firstGoalPost: Int  = (FieldConfig.heightBound - (FieldConfig.goalHeight * FieldConfig.scale)) / 2
      val secondGoalPost: Int = firstGoalPost + (FieldConfig.goalHeight * FieldConfig.scale)
      (p.x <= 0 || p.x >= FieldConfig.widthBound) && (p.y >= firstGoalPost && p.y <= secondGoalPost)

  extension (d: Direction)
    def bounce(bounce: Bounce): Direction = bounce match
      case Both       => Direction(-d.x, -d.y)
      case Horizontal => Direction(-d.x, d.y)
      case Vertical   => Direction(d.x, -d.y)

  extension (m: Movement)
    def bounce(bounce: Bounce): Movement = m.copy(direction = m.direction.bounce(bounce))

  extension (d: Direction)
    def jitter: Direction = (d.x + Random.between(-0.2, 0.2), d.y + Random.between(-0.2, 0.2))
