package update

import config.{MatchConfig, UIConfig}
import dsl.SpaceSyntax.*
import model.Match.*
import model.Match.Action.*
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import update.act.Act.*

class ActSpec extends AnyFlatSpec with Matchers:
  val defaultSpeed                = 1
  val defaultDirection: Direction = Direction(1, 1)

  "An act phase" should "update player movement and position if it is moving" in:
    val initialPosition = Position(0, 0)
    val player =
      Player(0, initialPosition, movement = Movement.still, nextAction = Move(defaultDirection, defaultSpeed))
    val team           = Team(List(player))
    val state          = Match((team, team), Ball(Position(0, 0)))
    val updated: Match = state.act()
    updated.teams.players
      .forall(_.movement == Movement(defaultDirection, defaultSpeed)) should be(true)
    updated.teams.players
      .forall(_.position == initialPosition + Movement(defaultDirection, defaultSpeed)) should be(true)

  it should "update ball movement if someone hits it" in:
    val ball   = Ball(Position(0, 0), movement = Movement.still)
    val player = Player(0, Position(0, 0), ball = Some(ball), nextAction = Hit(defaultDirection, defaultSpeed))
    val team   = Team(List(player))
    val state  = Match((team, team), ball)
    state.act().ball.movement should be(Movement(defaultDirection, defaultSpeed))

  "A team" should "move when he has to" in:
    val initial = Position(0, 0)
    val p1 = Player(0, initial, movement = Movement.still, nextAction = Move(defaultDirection, MatchConfig.playerSpeed))
    val p2 = Player(1, initial, movement = Movement.still, nextAction = Move(defaultDirection, MatchConfig.playerSpeed))
    val team = Team(List(p1, p2))
    team.updateMovements().players
      .forall(_.movement == Movement(defaultDirection, MatchConfig.playerSpeed)) should be(true)

  it should "move correctly" in:
    val initial  = Position(0, 0)
    val movement = Movement(defaultDirection, defaultSpeed)
    val p1       = Player(0, initial, movement)
    val p2       = Player(1, initial, movement)
    val team     = Team(List(p1, p2))
    team.move().players.forall(_.position == initial + movement) should be(true)

  "A player" should "keep last movement if he has no new nextAction" in:
    val lastMovement = Movement(defaultDirection, defaultSpeed)
    val player       = Player(0, Position(0, 0), movement = lastMovement, nextAction = Initial)
    player.updateMovement().movement should be(lastMovement)

  it should "stand still if he is stopped" in:
    val player = Player(0, Position(0, 0), nextAction = Stopped(1))
    player.updateMovement().movement should be(Movement.still)

  it should "move when he has to" in:
    val player = Player(0, Position(0, 0), nextAction = Move(defaultDirection, MatchConfig.playerSpeed))
    player.updateMovement().movement should be(Movement(defaultDirection, MatchConfig.playerSpeed))

  it should "stand still when hitting the ball" in:
    val direction = Direction(2, 2)
    val speed     = 2
    val ball      = Ball(Position(0, 0))
    val notStill  = Movement(defaultDirection, defaultSpeed)
    val player = Player(
      0,
      Position(0, 0),
      movement = notStill,
      ball = Some(ball),
      nextAction = Hit(defaultDirection, defaultSpeed)
    )
    player.updateMovement().movement should be(Movement.still)

  it should "move correctly" in:
    val initialPosition = Position(0, 0)
    val initialMovement = Movement(defaultDirection, defaultSpeed)
    val player          = Player(0, initialPosition, movement = initialMovement)
    player.move().position should be(initialPosition + initialMovement)

  "A ball" should "keep the same movement if no one controls it" in:
    val initial = Movement(defaultDirection, defaultSpeed)
    val ball    = Ball(Position(0, 0), movement = initial)
    ball.updateMovement(None).movement should be(initial)

  it should "keep the same movement if the player that controls it" + "doesn't move or hit it" in:
    val initial = Movement.still
    val ball    = Ball(Position(0, 0), initial)
    val carrier = Player(0, Position(0, 0), Movement.still, Some(ball))
    ball.updateMovement(Some(carrier)).movement should be(initial)

  it should "move with the player that controls it" in:
    val ball = Ball(Position(0, 0))
    val carrier =
      Player(0, Position(0, 0), ball = Some(ball), nextAction = Move(defaultDirection, MatchConfig.playerSpeed))
    ball.updateMovement(Some(carrier)).movement should be(Movement(defaultDirection, MatchConfig.playerSpeed))

  it should "move with the player that took it" in:
    val ball           = Ball(Position(0, 0))
    val playerMovement = Movement(defaultDirection, defaultSpeed)
    val carrier        = Player(0, Position(0, 0), playerMovement, nextAction = Take(ball))
    ball.updateMovement(Some(carrier)).movement should be(playerMovement)

  it should "be hit by player correctly" in:
    val ball      = Ball(Position(0, 0), Movement.still)
    val direction = Direction(2, 2)
    val speed     = 2
    val player    = Player(0, Position(0, 0), Movement.still, ball = Some(ball), nextAction = Hit(direction, speed))
    ball.updateMovement(Some(player)).movement should be(Movement(direction, speed))

  it should "move correctly" in:
    val initial  = Position(0, 0)
    val movement = Movement(defaultDirection, defaultSpeed)
    val ball     = Ball(initial, movement)
    ball.move().position should be(initial + movement)

  "isGoal" should "be true when you hit the first door" in:
    val state = Match((Team(Nil), Team(Nil)), Ball(Position(0, UIConfig.fieldHeight / 2)))
    state.isGoal should be(true)

  it should "be true when you hit the second door" in:
    val state = Match((Team(Nil), Team(Nil)), Ball(Position(UIConfig.fieldWidth, UIConfig.fieldHeight / 2)))
    state.isGoal should be(true)

  "isBallOut" should "be true if the ball is out" in:
    val state = Match((Team(Nil), Team(Nil)), Ball(Position(UIConfig.fieldWidth + 1, UIConfig.fieldHeight + 1)))
    state.isBallOut should be(true)

  it should "be false when ball is in game" in:
    val state = Match((Team(Nil), Team(Nil)), Ball(Position(UIConfig.fieldWidth / 2, UIConfig.fieldHeight / 2)))
    state.isBallOut should be(false)
