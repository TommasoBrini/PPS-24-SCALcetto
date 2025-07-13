package model

/** Basic **spatial algebra** used by the simulation and the DSL: positions, directions, speeds and movements with
  * helper constructors.
  */
object Space:
  /** Non-negative integer speed measured in grid units per tick. */
  opaque type Speed = Int

  /** Absolute **2-D grid position** `(x, y)` on the pitch. */
  opaque type Position = (Int, Int)
  object Position:
    def apply(x: Int, y: Int): Position = (x, y)
  extension (p: Position)
    def x: Int = p._1
    def y: Int = p._2

    /** Unit [[Direction]] that points from this position to the target.
      *
      * Returns [[Direction.none]] when both points coincide.
      */
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
