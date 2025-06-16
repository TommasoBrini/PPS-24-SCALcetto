package model

import Model.*
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class ModelSpec extends AnyFlatSpec with Matchers:

  private val initial_x_position: Int = 3
  private val initial_y_position: Int = 4

  "Direction" should "be create from Position" in:
    val pos: Position  = Position(initial_x_position, initial_y_position)
    val dir: Direction = Direction(pos)
    dir.x shouldEqual initial_x_position.toDouble
    dir.y shouldEqual initial_y_position.toDouble
  it should "be create from coordinates" in:
    val dir: Direction = Direction(initial_x_position, initial_y_position)
    dir.x shouldEqual initial_x_position.toDouble
    dir.y shouldEqual initial_y_position.toDouble
  it should "be none" in:
    val dir: Direction = Direction.none
    dir.x shouldEqual 0.0
    dir.y shouldEqual 0.0

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
