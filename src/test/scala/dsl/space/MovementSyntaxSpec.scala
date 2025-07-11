package dsl.space

import model.Space.{Direction, Movement, Position}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.matchers.should.Matchers.shouldBe
import model.Space.Bounce.*

import MovementSyntax.*
import PositionSyntax.*

class MovementSyntaxSpec extends AnyFlatSpec with Matchers:
  "A Movement" should "bounce its direction correctly and keep speed" in:
    val m       = Movement(Direction(1.0, -1.0), 5)
    val bounced = m getMovementFrom ObliqueBounce
    bounced.direction shouldBe Direction(-1.0, 1.0)
    bounced.speed shouldBe 5

  "A Movement" should "be applied correctly to a position, returning the new position" in:
    val p      = Position(0, 0)
    val d      = Direction(1.0, 0.0)
    val m      = Movement(d, 3)
    val newPos = p + m
    newPos shouldBe Position(3, 0)
