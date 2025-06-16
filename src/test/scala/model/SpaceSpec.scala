package model

import Match.*
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

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

  "Movement" should "be still" in:
    val movement: Movement = Movement.still
    movement.direction shouldEqual Direction.none
    movement.speed shouldEqual 0

  "Position" should "get direction to another position" in:
    val pos1: Position = Position(1, 1)
    val pos2: Position = Position(4, 5)
    val dir: Direction = pos1.getDirection(pos2)
    dir.x shouldEqual 0.6
    dir.y shouldEqual 0.8
  it should "apply movement" in:
    val pos: Position      = Position(initial_x_position, initial_y_position)
    val movement: Movement = Movement(Direction(1, 1), 3)
    val newPos: Position   = pos + movement
    newPos.x shouldEqual (initial_x_position + 3)
    newPos.y shouldEqual (initial_y_position + 3)

  "Player" should "be create without ball" in:
    val player: Player = Player(1, Position(0, 0), Movement.still)
    player.id shouldEqual 1
    player.position shouldEqual Position(0, 0)
    player.movement shouldEqual Movement.still
    player.ball shouldEqual None
    player.nextAction shouldEqual None
  it should "be create with ball" in:
    val ball: Ball     = Ball(Position(1, 1), Movement.still)
    val player: Player = Player(1, Position(0, 0), Movement.still, Some(ball), Some(Action.Take(ball)))
    player.hasBall shouldEqual true
