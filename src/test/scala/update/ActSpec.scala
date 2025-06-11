package update

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import model.Model.*
import update.Act.*
import model.Model.PlayerStatus.*
import model.Model.Action.*
import config.FieldConfig.*

class ActSpec extends AnyFlatSpec with Matchers:
  val defaultSpeed                = 1
  val defaultDirection: Direction = Direction(1, 1)

  "A ball" should "move correctly" in:
    val initial  = Position(0, 0)
    val movement = Movement(defaultDirection, defaultSpeed)
    val ball     = Ball(initial, movement)

    move(ball).position should be(initial + movement)

  "A player" should "move correctly" in:
    val initial  = Position(0, 0)
    val movement = Movement(defaultDirection, defaultSpeed)
    val player   = Player(0, initial, noControl, None, movement)

    move(player).position should be(initial + movement)

  it should "move when he has to" in:
    val player = Player(0, Position(0, 0), noControl, Some(Move(defaultDirection)), Movement.still)

    updateMovement(player).movement should be(Movement(defaultDirection, playerSpeed))

  it should "move the ball with him if he controls it" in:
    val ball   = Ball(Position(0, 0), Movement.still)
    val player = Player(0, Position(0, 0), ballControl, Some(Move(defaultDirection)), Movement.still)

    val updated = updateMovement(player)
    updateMovement(ball, Some(updated)).movement should be(Movement(defaultDirection, playerSpeed))

  it should "hit ball correctly" in:
    val ball      = Ball(Position(0, 0), Movement.still)
    val direction = Direction(2, 2)
    val speed     = 2
    val player    = Player(0, Position(0, 0), ballControl, Some(Hit(direction, speed)), Movement.still)

    updateMovement(ball, Some(player)).movement should be(Movement(direction, speed))

  it should "stand still when hitting the ball" in:
    val direction = Direction(2, 2)
    val speed     = 2
    val player    = Player(0, Position(0, 0), ballControl, Some(Hit(direction, speed)), Movement(direction, speed))

    updateMovement(player).movement should be(Movement.still)

  "A team" should "move correctly" in:
    val initial  = Position(0, 0)
    val movement = Movement(Direction(1, 1), defaultSpeed)
    val p1       = Player(0, initial, noControl, None, movement)
    val p2       = Player(1, initial, noControl, None, movement)
    val team     = Team(0, List(p1, p2))

    move(team).players.forall(_.position == initial + movement) should be(true)

  it should "move when he has to" in:
    val initial = Position(0, 0)
    val p1      = Player(0, initial, noControl, Some(Move(defaultDirection)), Movement.still)
    val p2      = Player(1, initial, noControl, Some(Move(defaultDirection)), Movement.still)
    val team    = Team(0, List(p1, p2))

    updateMovement(team).players
      .forall(_.movement == Movement(defaultDirection, playerSpeed)) should be(true)
