package dsl.space

import model.Space.{Bounce, Direction}
import model.Space.Bounce.*

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
