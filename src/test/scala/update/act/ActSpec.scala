package update.act

import config.FieldConfig
import model.Match.*
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import Act.*

class ActSpec extends AnyFlatSpec with Matchers:
  val defaultSpeed                = 1
  val defaultDirection: Direction = Direction(1, 1)

  "An act phase" should "update player movement if it is moving" in:
    val initial = Movement.still
    val player =
      Player(0, Position(0, 0), Movement.still, None, Action.Move(defaultDirection, FieldConfig.playerSpeed))
    val team  = Team(0, List(player))
    val state = MatchState(List(team), Ball(Position(0, 0), Movement.still))
    executeAction(state).teams.flatMap(_.players)
      .forall(_.movement == Movement(defaultDirection, defaultSpeed)) should be(true)

  it should "update ball movement if someone hits it" in:
    val position = Position(0, 0)
    val ball     = Ball(position, Movement.still)
    val player   = Player(0, position, Movement.still, Some(ball), Action.Hit(defaultDirection, defaultSpeed))
    val team     = Team(0, List(player))
    val state    = MatchState(List(team), Ball(Position(0, 0), Movement.still))
    executeAction(state).ball.movement should be(Movement(defaultDirection, defaultSpeed))

  "A team" should "move when he has to" in:
    val initial = Position(0, 0)
    val p1      = Player(0, initial, Movement.still, None, Action.Move(defaultDirection, FieldConfig.playerSpeed))
    val p2      = Player(1, initial, Movement.still, None, Action.Move(defaultDirection, FieldConfig.playerSpeed))
    val team    = Team(0, List(p1, p2))
    updateMovement(team).players
      .forall(_.movement == Movement(defaultDirection, FieldConfig.playerSpeed)) should be(true)

  it should "move correctly" in:
    val initial  = Position(0, 0)
    val movement = Movement(defaultDirection, defaultSpeed)
    val p1       = Player(0, initial, movement, None, Action.Initial)
    val p2       = Player(1, initial, movement, None, Action.Initial)
    val team     = Team(0, List(p1, p2))
    move(team).players.forall(_.position == initial + movement) should be(true)

  "A player" should "keep last movement if he has no new action" in:
    val action       = Action.Initial
    val lastMovement = Movement(defaultDirection, defaultSpeed)
    val player       = Player(0, Position(0, 0), lastMovement, None, action)
    updateMovement(player).movement should be(lastMovement)

  it should "stand still if he is stopped" in:
    val action = Action.Stopped(2)
    val player = Player(0, Position(0, 0), Movement.still, None, action)
    updateMovement(player).position should be(Position(0, 0))
    updateMovement(player).nextAction should be(Action.Stopped(1))

  it should "move when he has to" in:
    val action = Action.Move(defaultDirection, FieldConfig.playerSpeed)
    val player = Player(0, Position(0, 0), Movement.still, None, action)
    updateMovement(player).movement should be(Movement(defaultDirection, FieldConfig.playerSpeed))

  it should "stand still when hitting the ball" in:
    val direction = Direction(2, 2)
    val speed     = 2
    val ball      = Ball(Position(0, 0), Movement.still)
    val notStill  = Movement(defaultDirection, defaultSpeed)
    val player    = Player(0, Position(0, 0), notStill, Some(ball), Action.Hit(direction, speed))
    updateMovement(player).movement should be(Movement.still)

  it should "move correctly" in:
    val initial  = Position(0, 0)
    val movement = Movement(defaultDirection, defaultSpeed)
    val player   = Player(0, initial, movement)
    move(player).position should be(initial + movement)

  "A ball" should "keep the same movement if no one controls it" in:
    val initial = Movement(defaultDirection, defaultSpeed)
    val ball    = Ball(Position(0, 0), initial)
    updateMovement(ball, None).movement should be(initial)

  it should "keep the same movement if the player that controls it" +
    "doesn't move or hit it" in:
      val initial = Movement.still
      val ball    = Ball(Position(0, 0), initial)
      val player  = Player(0, Position(0, 0), Movement.still, Some(ball))
      updateMovement(ball, Some(player)).movement should be(initial)

  it should "move with the player that controls it" in:
    val ball = Ball(Position(0, 0), Movement.still)
    val player =
      Player(0, Position(0, 0), Movement.still, Some(ball), Action.Move(defaultDirection, FieldConfig.playerSpeed))
    val updated = updateMovement(player)
    updateMovement(ball, Some(updated)).movement should be(Movement(defaultDirection, FieldConfig.playerSpeed))

  it should "be hit by player correctly" in:
    val ball      = Ball(Position(0, 0), Movement.still)
    val direction = Direction(2, 2)
    val speed     = 2
    val player    = Player(0, Position(0, 0), Movement.still, Some(ball), Action.Hit(direction, speed))
    updateMovement(ball, Some(player)).movement should be(Movement(direction, speed))

  it should "move correctly" in:
    val initial  = Position(0, 0)
    val movement = Movement(defaultDirection, defaultSpeed)
    val ball     = Ball(initial, movement)
    move(ball).position should be(initial + movement)
