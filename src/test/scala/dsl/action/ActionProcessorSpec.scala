package dsl.action

import config.{MatchConfig, UIConfig}
import dsl.MatchSyntax.*
import dsl.SpaceSyntax.*
import dsl.action.ActionProcessor.*
import dsl.creation.CreationSyntax.*
import dsl.creation.build.MatchBuilder
import model.Match.*
import model.Match.Action.*
import model.Match.Decision.{Intercept, Tackle}
import model.Match.Side.*
import model.Space.*
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import update.Update.Event.*

class ActionProcessorSpec extends AnyFlatSpec with Matchers:
  val defaultSpeed                = 1
  val defaultDirection: Direction = Direction(1, 1)

  "A match" should "recognize if there is a successful tackle in it" in:
    val ball = Ball(Position(0, 0))
    val state = newMatch(Score.init()):
      team(West):
        player(0) decidedTo Tackle(ball) isGoingTo Take(ball)
      team(East):
        player(1)
    state.existsSuccessfulTackle should be(true)

  it should "recognize if there isn't a successful tackle in it" in:
    val ball = Ball(Position(0, 0))
    val state = newMatch(Score.init()):
      team(West):
        player(0) decidedTo Tackle(ball) isGoingTo Stopped(1)
      team(East):
        player(1)
    state.existsSuccessfulTackle should be(false)

  it should "recognize if ball possession is changing" in:
    val ball = Ball(Position(0, 0))
    val state = newMatch(Score.init()):
      team(West):
        player(0) decidedTo Intercept(ball) isGoingTo Take(ball)
      team(East):
        player(1)
    state.isPossessionChanging should be(true)

  it should "recognize if ball possession isn't changing" in:
    val ball = Ball(Position(0, 0))
    val state = newMatch(Score.init()):
      team(West):
        player(0)
      team(East):
        player(1)
    state.isPossessionChanging should be(false)

  it should "be able to tackle the ball carrier, taking away the ball and stopping him" in:
    val state = newMatch(Score.init()):
      team(West):
        player(0) ownsBall true
      team(East):
        player(1)
    val carrier: Player = state.tackleBallCarrier().players.head
    carrier.nextAction should matchPattern { case Stopped(_) => }
    carrier.ball should be(None)

  it should "update ball possession if someone it taking the ball" in:
    val ball = Ball(Position(0, 0))
    val state = newMatch(Score.init()):
      team(West):
        player(0) ownsBall false decidedTo Intercept(ball) isGoingTo Take(ball)
      team(East):
        player(1)
    state.updateBallPossession().players.head.ball should be(Some(ball))

  it should "update all movements" in:
    val state = newMatch(Score.init()):
      team(Side.West) withBall:
        player(0) at (5, 5) ownsBall true isGoingTo Move(defaultDirection, defaultSpeed)
      team(East):
        player(1)
      ball at (5, 5)
    val updated = state.updateMovements()
    updated.players.head.movement should be(Movement(defaultDirection, defaultSpeed))
    updated.ball.movement should be(Movement(defaultDirection, defaultSpeed))

  it should "update positions of moving entities" in:
    val initial = Position(0, 0)
    val state = newMatch(Score.init()):
      team(Side.West) withBall:
        player(0).at(initial.x, initial.y).move(defaultDirection)(defaultSpeed)
      team(East):
        player(1)
      ball at (initial.x, initial.y) move (defaultDirection, defaultSpeed)
    val updated = state.moveEntities()
    updated.players.head.position should be(initial + Movement(defaultDirection, defaultSpeed))
    updated.ball.position should be(initial + Movement(defaultDirection, defaultSpeed))

  it should "detect a goal from west team" in:
    val x = UIConfig.fieldWidth
    val y = UIConfig.fieldHeight / 2
    val state = newMatch(Score.init()):
      team(West)
      team(East)
      ball at (x, y)
    state.detectEvent() should be(Some(GoalWest))

  it should "detect a goal from east team" in:
    val x = 0
    val y = UIConfig.fieldHeight / 2
    val state = newMatch(Score.init()):
      team(West)
      team(East)
      ball at (x, y)
    state.detectEvent() should be(Some(GoalEast))

  it should "detect if the ball went out" in:
    val state = newMatch(Score.init()):
      team(West)
      team(East)
      ball at (-1, -1)
    state.detectEvent() should be(Some(BallOut))

  it should "detect nothing if nothing of the previous happened" in:
    val state = newMatch(Score.init()):
      team(West):
        player(0) at (0, 0)
      team(East)
      ball at (0, 0)
    state.detectEvent() should be(None)

  "A team" should "gain possession if someone in it is taking the ball" in:
    val ball    = Ball(Position(0, 0))
    val player  = Player(0, Position(0, 0), ball = None, nextAction = Take(ball))
    val team    = Team(List(player), West)
    val updated = team.updateBallPossession()
    updated.hasBall should be(true)
    updated.players.head.ball should be(Some(ball))

  it should "move when he has to" in:
    val initial = Position(0, 0)
    val p1 = Player(0, initial, movement = Movement.still, nextAction = Move(defaultDirection, MatchConfig.playerSpeed))
    val p2 = Player(1, initial, movement = Movement.still, nextAction = Move(defaultDirection, MatchConfig.playerSpeed))
    val team = Team(List(p1, p2))
    team.processActions().players
      .forall(_.movement == Movement(defaultDirection, MatchConfig.playerSpeed)) should be(true)

  it should "move correctly" in:
    val initial  = Position(0, 0)
    val movement = Movement(defaultDirection, defaultSpeed)
    val p1       = Player(0, initial, movement)
    val p2       = Player(1, initial, movement)
    val team     = Team(List(p1, p2))
    team.move().players.forall(_.position == initial + movement) should be(true)

  "A player" should "gain possession of the ball if he's taking it" in:
    val ball   = Ball(Position(0, 0))
    val player = Player(0, Position(0, 0), ball = Some(ball), nextAction = Take(ball))
    player.updateBallPossession().ball should be(Some(ball))

  it should "not gain possession if he's not taking it" in:
    val player = Player(0, Position(0, 0), ball = None)
    player.updateBallPossession().ball should be(None)

  it should "stand still when hitting the ball" in:
    val ball = Ball(Position(0, 0))
    val player = Player(
      0,
      Position(0, 0),
      Movement(defaultDirection, defaultSpeed),
      ball = Some(ball),
      nextAction = Hit(defaultDirection, defaultSpeed)
    )
    player.processAction().movement should be(Movement.still)

  it should "not have the ball after hitting it" in:
    val ball = Ball(Position(0, 0))
    val player = Player(
      0,
      Position(0, 0),
      Movement(defaultDirection, defaultSpeed),
      ball = Some(ball),
      nextAction = Hit(defaultDirection, defaultSpeed)
    )
    player.processAction().ball should be(None)

  it should "stop a while after hitting the ball" in:
    val ball = Ball(Position(0, 0))
    val player = Player(
      0,
      Position(0, 0),
      Movement(defaultDirection, defaultSpeed),
      ball = Some(ball),
      nextAction = Hit(defaultDirection, defaultSpeed)
    )
    player.processAction().nextAction should matchPattern { case Stopped(_) => }

  it should "move when he has to" in:
    val player = Player(0, Position(0, 0), nextAction = Move(defaultDirection, defaultSpeed))
    player.processAction().movement should be(Movement(defaultDirection, defaultSpeed))

  it should "stand still if he is stopped" in:
    val player = Player(0, Position(0, 0), nextAction = Stopped(1))
    player.processAction().movement should be(Movement.still)

  it should "stand still if he is taking the ball" in:
    val ball   = Ball(Position(0, 0))
    val player = Player(0, Position(0, 0), nextAction = Take(ball))
    player.processAction().movement should be(Movement.still)

  it should "keep last movement if he has no new action" in:
    val player = Player(0, Position(0, 0), Movement(defaultDirection, defaultSpeed), nextAction = Initial)
    player.processAction().movement should be(Movement(defaultDirection, defaultSpeed))

  it should "move correctly" in:
    val initialPosition = Position(0, 0)
    val initialMovement = Movement(defaultDirection, defaultSpeed)
    val player          = Player(0, initialPosition, movement = initialMovement)
    player.move().position should be(initialPosition + initialMovement)

  "A ball" should "be hit by player correctly" in:
    val ball      = Ball(Position(0, 0), Movement.still)
    val direction = Direction(2, 2)
    val speed     = 2
    val player    = Player(0, Position(0, 0), Movement.still, ball = Some(ball), nextAction = Hit(direction, speed))
    ball.updateMovement(Some(player)).movement should be(Movement(direction, speed))

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

  it should "keep the same movement if no one controls it" in:
    val initial = Movement(defaultDirection, defaultSpeed)
    val ball    = Ball(Position(0, 0), movement = initial)
    ball.updateMovement(None).movement should be(initial)

  it should "move correctly" in:
    val initial  = Position(0, 0)
    val movement = Movement(defaultDirection, defaultSpeed)
    val ball     = Ball(initial, movement)
    ball.move().position should be(initial + movement)
