package model

import Match.*

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

  it should "be applied correctly to a position, returning the new position" in:
    val p      = Position(0, 0)
    val d      = Direction(1.0, 0.0)
    val m      = Movement(d, 3)
    val newPos = p + m
    newPos shouldBe Position(3, 0)

  "isOutOfBound" should "return false for positions within bounds" in:
    val inside = Position(2, 2)
    inside.isOutOfBound(10, 10) shouldBe false

  it should "return true for positions out of bounds" in:
    val outside = Position(-1, 11)
    outside.isOutOfBound(10, 10) shouldBe true

  "getBounce" should "return Both if x and y are out of bounds" in:
    val p = Position(-1, -1)
    p.getBounce(10, 10) shouldBe Both

  it should "return Horizontal if only x is out of bounds" in:
    val p = Position(-1, 5)
    p.getBounce(10, 10) shouldBe Horizontal

  it should "return Vertical if only y is out of bounds" in:
    val p = Position(5, -1)
    p.getBounce(10, 10) shouldBe Vertical

  "A Direction" should "reflect correctly when bounced" in:
    val d = Direction(1.0, -1.0)
    d.bounce(Both) shouldBe Direction(-1.0, 1.0)
    d.bounce(Horizontal) shouldBe Direction(-1.0, -1.0)
    d.bounce(Vertical) shouldBe Direction(1.0, 1.0)

  "A Movement" should "bounce its direction correctly and keep speed" in:
    val m       = Movement(Direction(1.0, -1.0), 5)
    val bounced = m.bounce(Both)
    bounced.direction shouldBe Direction(-1.0, 1.0)
    bounced.speed shouldBe 5
