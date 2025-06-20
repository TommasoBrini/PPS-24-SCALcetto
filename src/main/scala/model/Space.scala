package model

import scala.annotation.targetName

object Space:

  opaque type Position = (Int, Int)
  object Position:
    def apply(x: Int, y: Int): Position = (x, y)
  extension (p: Position)
    def x: Int = p._1
    def y: Int = p._2

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
      val dx = to.x.toDouble - p.x
      val dy = to.y.toDouble - p.y
      (dx / Math.hypot(dx, dy), dy / Math.hypot(dx, dy))

  case class Movement(direction: Direction, speed: Int)
  object Movement:
    def still: Movement = Movement(Direction.none, 0)

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

  extension (d: Direction)
    def bounce(bounce: Bounce): Direction = bounce match
      case Both       => Direction(-d.x, -d.y)
      case Horizontal => Direction(-d.x, d.y)
      case Vertical   => Direction(d.x, -d.y)

  extension (m: Movement)
    def bounce(bounce: Bounce): Movement = m.copy(direction = m.direction.bounce(bounce))
