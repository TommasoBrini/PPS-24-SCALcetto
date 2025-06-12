package update

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import model.Model.*
import Act.*
import model.Model.PlayerStatus.*
import model.Model.Action.*
import config.FieldConfig

class ActSpec extends AnyFlatSpec with Matchers:
  val defaultSpeed                = 1
  val defaultDirection: Direction = Direction(1, 1)

  "An act phase" should "update player movement if it is moving" in:
    val initial = Movement.still
    val player  = Player(0, Position(0, 0), noControl, Some(Move(defaultDirection)), Movement.still)
    val team    = Team(0, List(player))
    val state   = SimulationState(List(team), Ball(Position(0, 0), Movement.still))

    act(state).teams.flatMap(_.players)
      .forall(_.movement == Movement(defaultDirection, defaultSpeed)) should be(true)

  it should "update ball movement if someone hits it" in:
    val position = Position(0, 0)
    val ball     = Ball(position, Movement.still)
    val player   = Player(0, position, ballControl, Some(Hit(defaultDirection, defaultSpeed)), Movement.still)
    val team     = Team(0, List(player))
    val state    = SimulationState(List(team), Ball(Position(0, 0), Movement.still))

    act(state).ball.movement should be(Movement(defaultDirection, defaultSpeed))

  "A team" should "move when he has to" in:
    val initial = Position(0, 0)
    val p1      = Player(0, initial, noControl, Some(Move(defaultDirection)), Movement.still)
    val p2      = Player(1, initial, noControl, Some(Move(defaultDirection)), Movement.still)
    val team    = Team(0, List(p1, p2))

    updateMovement(team).players
      .forall(_.movement == Movement(defaultDirection, FieldConfig.playerSpeed)) should be(true)

  it should "move correctly" in:
    val initial  = Position(0, 0)
    val movement = Movement(Direction(1, 1), defaultSpeed)
    val p1       = Player(0, initial, noControl, None, movement)
    val p2       = Player(1, initial, noControl, None, movement)
    val team     = Team(0, List(p1, p2))

    move(team).players.forall(_.position == initial + movement) should be(true)

  "A player" should "keep last movement if he has no new action" in:
    val action       = None
    val lastMovement = Movement(defaultDirection, defaultSpeed)
    val player       = Player(0, Position(0, 0), noControl, action, lastMovement)

    updateMovement(player).movement should be(lastMovement)

  it should "move when he has to" in:
    val action = Move(defaultDirection)
    val player = Player(0, Position(0, 0), noControl, Some(action), Movement.still)

    updateMovement(player).movement should be(Movement(defaultDirection, FieldConfig.playerSpeed))

  it should "stand still when hitting the ball" in:
    val direction = Direction(2, 2)
    val speed     = 2
    val player    = Player(0, Position(0, 0), ballControl, Some(Hit(direction, speed)), Movement(direction, speed))

    updateMovement(player).movement should be(Movement.still)

  it should "move correctly" in:
    val initial  = Position(0, 0)
    val movement = Movement(defaultDirection, defaultSpeed)
    val player   = Player(0, initial, noControl, None, movement)

    move(player).position should be(initial + movement)

  "A ball" should "keep the same movement if no one controls it" in:
    val initial = Movement(defaultDirection, defaultSpeed)
    val ball    = Ball(Position(0, 0), initial)

    updateMovement(ball, None).movement should be(initial)

  it should "keep the same movement if the player that controls it" +
    "doesn't move or hit it" in:
      val initial = Movement.still
      val ball    = Ball(Position(0, 0), initial)
      val player  = Player(0, Position(0, 0), ballControl, None, Movement.still)

      updateMovement(ball, Some(player)).movement should be(initial)

  it should "move with the player that controls it" in:
    val ball   = Ball(Position(0, 0), Movement.still)
    val player = Player(0, Position(0, 0), ballControl, Some(Move(defaultDirection)), Movement.still)

    val updated = updateMovement(player)
    updateMovement(ball, Some(updated)).movement should be(Movement(defaultDirection, FieldConfig.playerSpeed))

  it should "be hit by player correctly" in:
    val ball      = Ball(Position(0, 0), Movement.still)
    val direction = Direction(2, 2)
    val speed     = 2
    val player    = Player(0, Position(0, 0), ballControl, Some(Hit(direction, speed)), Movement.still)

    updateMovement(ball, Some(player)).movement should be(Movement(direction, speed))

  it should "move correctly" in:
    val initial  = Position(0, 0)
    val movement = Movement(defaultDirection, defaultSpeed)
    val ball     = Ball(initial, movement)

    move(ball).position should be(initial + movement)
