package model

import model.Match.*

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import model.Space.*
import model.Space.Bounce.*

class SpaceSpec extends AnyFlatSpec with Matchers:

  "A Position" should "store x and y correctly" in:
    val p = Position(3, 5)
    p.x shouldBe 3
    p.y shouldBe 5

  "A Direction" should "store x and y correctly" in:
    val d = Direction(2.0, 4.0)
    d.x shouldBe 2.0
    d.y shouldBe 4.0

  it can "be created with Integers" in:
    val d = Direction(2, 4)
    d.x shouldBe 2
    d.y shouldBe 4

  it can "be none, with both components 0" in:
    val d = Direction.none
    d.x shouldBe 0
    d.y shouldBe 0

  it should "be created as a normalized vector between two positions" in:
    val from           = Position(0, 0)
    val to             = Position(3, 4)
    val dir: Direction = from.getDirection(to)
    dir.x shouldBe (0.6 +- 0.0001)
    dir.y shouldBe (0.8 +- 0.0001)

  "A Movement" should "store direction and speed correctly" in:
    val direction = Direction(1.0, -1.0)
    val speed     = 4
    val movement  = Movement(direction, speed)
    movement.direction shouldBe direction
    movement.speed shouldBe speed

  it can "be still, with no direction and speed 0" in:
    val m = Movement.still
    m.direction shouldBe Direction.none
    m.speed shouldBe 0
