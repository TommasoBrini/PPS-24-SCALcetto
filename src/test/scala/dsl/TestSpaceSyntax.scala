package dsl

import model.Space.{Direction, Movement, Position}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.matchers.should.Matchers.shouldBe
import SpaceSyntax.*
import config.UIConfig
import model.Space.Bounce.*

class TestSpaceSyntax extends AnyFlatSpec with Matchers:

  "isOutOfBound" should "return false for positions within bounds" in:
    val inside = Position(2, 2)
    inside isOutOfBound (10, 10) shouldBe false

  "isOutOfBound" should "return true for positions out of bounds" in:
    val outside = Position(-1, 11)
    outside.isOutOfBound(10, 10) shouldBe true

  "getBounce" should "return Both if x and y are out of bounds" in:
    val p = Position(-1, -1)
    p.getBounce(10, 10) shouldBe ObliqueBounce

  "getBounce" should "return Horizontal if only x is out of bounds" in:
    val p = Position(-1, 5)
    p.getBounce(10, 10) shouldBe HorizontalBounce

  "getBounce" should "return Vertical if only y is out of bounds" in:
    val p = Position(5, -1)
    p.getBounce(10, 10) shouldBe VerticalBounce

  "A Direction" should "reflect correctly when bounced" in:
    val d = Direction(1.0, -1.0)
    d getDirectionFrom ObliqueBounce shouldBe Direction(-1.0, 1.0)
    d getDirectionFrom HorizontalBounce shouldBe Direction(-1.0, -1.0)
    d getDirectionFrom VerticalBounce shouldBe Direction(1.0, 1.0)

  "A Movement" should "bounce its direction correctly and keep speed" in:
    val m       = Movement(Direction(1.0, -1.0), 5)
    val bounced = m getMovementFrom ObliqueBounce
    bounced.direction shouldBe Direction(-1.0, 1.0)
    bounced.speed shouldBe 5
