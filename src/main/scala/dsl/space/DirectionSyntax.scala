package dsl.space

import model.Space.{Bounce, Direction, Movement, Position}
import model.Space.Bounce.*
import dsl.space.PositionSyntax.{+, isOutOfField}

import scala.util.Random

object DirectionSyntax:
  private def bounceCalculator(bounce: Bounce, x: Double, y: Double): Direction = bounce match
    case ObliqueBounce    => Direction(-x, -y)
    case HorizontalBounce => Direction(-x, y)
    case VerticalBounce   => Direction(x, -y)

  extension (d: Direction)
    /** Mirrors this direction according to the collision [[Bounce]].
      */
    def getDirectionFrom(bounce: Bounce): Direction = bounceCalculator(bounce, d.x, d.y)

    /** Returns a slightly randomised direction to avoid deterministic movement patterns (±0.2 on each axis).
      */
    def jitter: Direction = Direction(d.x + Random.between(-0.2, 0.2), d.y + Random.between(-0.2, 0.2))

    /** Clamps the direction to not point out of field
      */
    def clampToField(position: Position, speed: Int): Direction =
      val dxOnly = Direction(d.x, 0)
      val dyOnly = Direction(0, d.y)

      val dxValid = !(position + Movement(dxOnly, speed)).isOutOfField
      val dyValid = !(position + Movement(dyOnly, speed)).isOutOfField

      (dxValid, dyValid) match
        case (true, true)   => d
        case (true, false)  => dxOnly
        case (false, true)  => dyOnly
        case (false, false) => Direction.none
