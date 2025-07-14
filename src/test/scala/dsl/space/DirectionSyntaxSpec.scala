package dsl.space

import model.Space.{Direction, Position}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.matchers.should.Matchers.shouldBe
import model.Space.Bounce.*
import DirectionSyntax.*
import config.UIConfig

import scala.util.Random

class DirectionSyntaxSpec extends AnyFlatSpec with Matchers:

  "A Direction" should "reflect correctly when bounced" in:
    val d = Direction(1.0, -1.0)
    (d getDirectionFrom ObliqueBounce, d getDirectionFrom HorizontalBounce, d getDirectionFrom VerticalBounce) shouldBe
      (Direction(-1.0, 1.0), Direction(-1.0, -1.0), Direction(1.0, 1.0))

  "Direction jitter" should " add the same noise when Random is seeded" in:
    Random.setSeed(42L)
    val direction = Direction(1.0, -3.0)
    val first     = direction.jitter

    Random.setSeed(42L)
    val second = direction.jitter

    assert(first == second)
    assert(math.abs(first.x - direction.x) <= 0.2)
    assert(math.abs(first.y - direction.y) <= 0.2)

  val fieldWidth: Int  = UIConfig.fieldWidth
  val fieldHeight: Int = UIConfig.fieldHeight

  "Direction clampToField" should "return original direction when both components are valid" in:
    val direction = Direction(1, 1)
    val position  = Position(fieldWidth / 2, fieldHeight / 2)

    val newDirection = direction.clampToField(position, speed = 1)
    newDirection should be(direction)

  it should "zero out Y component if moving in Y would go out of field" in:
    val direction = Direction(1, 1)
    val position  = Position(fieldWidth / 2, fieldHeight)

    val newDirection = direction.clampToField(position, speed = 1)
    newDirection should be(Direction(direction.x, 0))

  it should "zero out X component if moving in X would go out of field" in:
    val direction = Direction(1, 1)
    val position  = Position(fieldWidth, fieldHeight / 2)

    val newDirection = direction.clampToField(position, speed = 1)
    newDirection should be(Direction(0, direction.y))

  it should "return Direction.none if both components lead out of field" in:
    val direction = Direction(1, 1)
    val position  = Position(fieldWidth, fieldHeight)

    val newDirection = direction.clampToField(position, speed = 1)
    newDirection should be(Direction.none)
