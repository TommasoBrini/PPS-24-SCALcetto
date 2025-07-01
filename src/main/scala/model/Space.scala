package model

import config.UIConfig

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
    case VerticalBounce, HorizontalBounce, ObliqueBounce

  extension (d: Direction)
    def jitter: Direction = (d.x + Random.between(-0.2, 0.2), d.y + Random.between(-0.2, 0.2))

  extension (p: Position)
    def clampToField: Position =
      val clampedX = Math.max(0, Math.min(p.x, UIConfig.fieldWidth))
      val clampedY = Math.max(0, Math.min(p.y, UIConfig.fieldHeight))
      Position(clampedX, clampedY)
