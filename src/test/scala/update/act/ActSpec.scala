package update.act

import config.MatchConfig
import config.UIConfig
import model.Match.*
import model.Match.Action.*
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import Act.*
import init.GameInitializer

import dsl.game.TeamsSyntax.*
import dsl.space.PositionSyntax.*

class ActSpec extends AnyFlatSpec with Matchers:
  val defaultSpeed                = 1
  val defaultDirection: Direction = Direction(1, 1)

  "An act phase" should "update player movement and position if it is moving" in:
    val initialPosition = Position(0, 0)
    val player =
      Player(0, initialPosition, movement = Movement.still, nextAction = Move(defaultDirection, defaultSpeed))
    val team1               = Team(0, List(player))
    val team2               = Team(2, List())
    val state               = MatchState((team1, team2), Ball(Position(0, 0)))
    val updated: MatchState = act(state)
    updated.teams.players
      .forall(_.movement == Movement(defaultDirection, defaultSpeed)) should be(true)
    updated.teams.players
      .forall(_.position == initialPosition + Movement(defaultDirection, defaultSpeed)) should be(true)

  it should "update ball movement if someone hits it" in:
    val ball   = Ball(Position(0, 0), movement = Movement.still)
    val player = Player(0, Position(0, 0), ball = Some(ball), nextAction = Hit(defaultDirection, defaultSpeed))
    val team1  = Team(0, List(player))
    val team2  = Team(2, List())
    val state  = MatchState((team1, team2), ball)
    act(state).ball.movement should be(Movement(defaultDirection, defaultSpeed))

  "A team" should "move when he has to" in:
    val initial = Position(0, 0)
    val p1 = Player(
      0,
      initial,
      movement = Movement.still,
      nextAction = Move(defaultDirection, MatchConfig.playerWithBallSpeed)
    )
    val p2 = Player(
      1,
      initial,
      movement = Movement.still,
      nextAction = Move(defaultDirection, MatchConfig.playerWithBallSpeed)
    )
    val team = Team(0, List(p1, p2))
    updateMovement(team, None).players
      .forall(_.movement == Movement(defaultDirection, MatchConfig.playerWithBallSpeed)) should be(true)

  it should "move correctly" in:
    val initial  = Position(0, 0)
    val movement = Movement(defaultDirection, defaultSpeed)
    val p1       = Player(0, initial, movement, None, Action.Initial)
    val p2       = Player(1, initial, movement, None, Action.Initial)
    val team     = Team(0, List(p1, p2), false)
    move(team).players.forall(_.position == initial + movement) should be(true)

  "A player" should "keep last movement if he has no new action" in:
    val lastMovement = Movement(defaultDirection, defaultSpeed)
    val player       = Player(0, Position(0, 0), movement = lastMovement, nextAction = Initial)
    updateMovement(player, None).movement should be(lastMovement)

  it should "stand still if he is stopped" in:
    val player = Player(0, Position(0, 0), nextAction = Stopped(1))
    updateMovement(player, None).movement should be(Movement.still)

  it should "move when he has to" in:
    val player = Player(0, Position(0, 0), nextAction = Move(defaultDirection, MatchConfig.playerWithBallSpeed))
    updateMovement(player, None).movement should be(Movement(defaultDirection, MatchConfig.playerWithBallSpeed))

  it should "stand still when hitting the ball" in:
    val direction = Direction(2, 2)
    val speed     = 2
    val ball      = Ball(Position(0, 0))
    val notStill  = Movement(defaultDirection, defaultSpeed)
    val player = Player(0, Position(0, 0), movement = notStill, ball = Some(ball), nextAction = Hit(direction, speed))
    updateMovement(player, Some(player)).movement should be(Movement.still)

  it should "move correctly" in:
    val initialPosition = Position(0, 0)
    val initialMovement = Movement(defaultDirection, defaultSpeed)
    val player          = Player(0, initialPosition, movement = initialMovement)
    move(player).position should be(initialPosition + initialMovement)

  "A ball" should "keep the same movement if no one controls it" in:
    val initial = Movement(defaultDirection, defaultSpeed)
    val ball    = Ball(Position(0, 0), movement = initial)
    updateMovement(ball, None).movement should be(initial)

  it should "keep the same movement if the player that controls it" + "doesn't move or hit it" in:
    val initial = Movement.still
    val ball    = Ball(Position(0, 0), initial)
    val player  = Player(0, Position(0, 0), Movement.still, Some(ball))
    updateMovement(ball, Some(player)).movement should be(initial)

  it should "move with the player that controls it" in:
    val ball = Ball(Position(0, 0))
    val player =
      Player(0, Position(0, 0), Movement.still, Some(ball), Move(defaultDirection, MatchConfig.playerWithBallSpeed))
    val updated = updateMovement(player, Some(player))
    updateMovement(ball, Some(updated)).movement should be(Movement(defaultDirection, MatchConfig.playerWithBallSpeed))

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

  "Goal" should "be true when you hit the first door" in:
    val state: MatchState = GameInitializer.initialSimulationState()
    val goalState = state.copy(
      ball = state.ball.copy(
        position = Position(0, UIConfig.fieldHeight / 2)
      )
    )
    isAGoal(goalState) should be(true)

  "Goal " should " be true when you hit the second door" in:
    val state: MatchState = GameInitializer.initialSimulationState()
    val goalState = state.copy(
      ball = state.ball.copy(
        position = Position(UIConfig.fieldWidth, UIConfig.fieldHeight / 2)
      )
    )
    isAGoal(goalState) should be(true)
