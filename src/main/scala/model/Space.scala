package model

object Space:
  opaque type Speed = Int

  opaque type Position = (Int, Int)
  object Position:
    def apply(x: Int, y: Int): Position = (x, y)
  extension (p: Position)
    def x: Int = p._1
    def y: Int = p._2
    def getDirection(to: Position): Direction =
      val dx = to.x - p.x
      val dy = to.y - p.y
      if dx == 0 && dy == 0 then Direction.none
      else (dx / Math.hypot(dx, dy), dy / Math.hypot(dx, dy))

  opaque type Direction = (Double, Double)
  object Direction:
    def apply(x: Int, y: Int): Direction                = (x, y)
    def apply(x: Double, y: Double): Direction          = (x, y)
    def unapply(d: Direction): Option[(Double, Double)] = Some(d)
    def none: Direction                                 = (0, 0)
  extension (d: Direction)
    def x: Double = d._1
    def y: Double = d._2

  opaque type Movement = (Direction, Speed)
  extension (m: Movement)
    def direction: Direction = m._1
    def speed: Int           = m._2

  object Movement:
    def apply(direction: Direction, speed: Int): Movement = (direction, speed)
    def still: Movement                                   = (Direction.none, 0)

  enum Bounce:
    case VerticalBounce, HorizontalBounce, ObliqueBounce
