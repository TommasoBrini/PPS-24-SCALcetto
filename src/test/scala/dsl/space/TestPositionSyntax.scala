package dsl.space

import model.Space.{Direction, Movement, Position}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.matchers.should.Matchers.shouldBe
import model.Match.{Ball, Player, Team}
import model.Space.Bounce.*

import PositionSyntax.*

import scala.util.Random

class TestPositionSyntax extends AnyFlatSpec with Matchers:

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
