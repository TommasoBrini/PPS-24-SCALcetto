package model

import model.Space.*
import model.Match.*
import Side.*
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import model.decisions.PlayerDecisionFactory.*
import model.decisions.CommonPlayerDecisions.*
import config.MatchConfig

class PlayerSpec extends AnyFlatSpec with Matchers:
  "A Player" should "store id, position, and movement correctly" in:
    val pos    = Position(1, 2)
    val mov    = Movement(Direction(1.0, 0.0), 3)
    val player = Player(42, pos, mov)

    player.id shouldBe 42
    player.position shouldBe pos
    player.movement shouldBe mov
    player.ball shouldBe None
    player.nextAction shouldBe Action.Initial

  it should "correctly report it when carrying a ball" in:
    val ball: Ball        = Ball(Position(5, 5), Movement(Direction.none, 0))
    val playerWithBall    = Player(1, Position(0, 0), Movement.still, Some(ball)).asControlDecisionPlayer
    val playerWithoutBall = Player(2, Position(0, 0), Movement.still)

    playerWithBall.ball.isDefined shouldBe true
    playerWithoutBall.ball.isDefined shouldBe false

  it should "can decide to run" in:
    val player    = Player(1, Position(0, 0))
    val direction = player.position.getDirection(Position(1, 0))
    val steps     = config.MatchConfig.runSteps
    val decision  = player.decideRun(direction, steps)
    decision shouldBe a[Decision.Run]
    val run = decision.asInstanceOf[Decision.Run]
    run.direction shouldBe direction
    run.steps shouldBe steps

  it should "can decide to stop" in:
    val player = Player(1, Position(0, 0))
    player.decideConfusion(10) shouldBe Decision.Confusion(10)

  it should "can decide to run to ball" in:
    val ball: Ball = Ball(Position(5, 5), Movement(Direction.none, 0))
    val player     = Player(1, Position(0, 0), Movement.still, Some(ball)).asControlDecisionPlayer
    player.decideMoveToBall(player.position.getDirection(ball.position)) shouldBe Decision.MoveToBall(
      player.position.getDirection(ball.position)
    )

  "A Player with a ball" should "be able to pass" in:
    val ball: Ball        = Ball(Position(5, 5), Movement(Direction.none, 0))
    val playerWithBall    = Player(1, Position(0, 0), Movement.still, Some(ball)).asControlDecisionPlayer
    val playerWithoutBall = Player(2, Position(0, 0), Movement.still)
    playerWithBall.decidePass(playerWithoutBall) shouldBe Decision.Pass(playerWithBall, playerWithoutBall)

  it should "be able to shoot" in:
    val ball: Ball     = Ball(Position(5, 5), Movement(Direction.none, 0))
    val playerWithBall = Player(1, Position(0, 0), Movement.still, Some(ball)).asControlDecisionPlayer
    playerWithBall.decideShoot(Position(10, 10)) shouldBe Decision.Shoot(playerWithBall, Position(10, 10))

  it should "be able to move to goal" in:
    val player = Player(1, Position(0, 0)).asControlDecisionPlayer
    player.decideMoveToGoal(player.position.getDirection(Position(10, 0))) shouldBe Decision.MoveToGoal(
      player.position.getDirection(Position(10, 0))
    )

  "A opponent" should "be able to mark an opponent" in:
    val defender = Player(1, Position(0, 0)).asOpponentDecisionPlayer
    val target   = Player(2, Position(1, 1))
    defender.decideMark(target, West) shouldBe Decision.Mark(defender, target, West)

  it should "be able to tackle a ball" in:
    val ball: Ball = Ball(Position(5, 5), Movement(Direction.none, 0))
    val player     = Player(1, Position(0, 0)).asOpponentDecisionPlayer
    player.decideTackle(ball) shouldBe Decision.Tackle(ball)

  it should "be able to intercept a ball" in:
    val ball: Ball = Ball(Position(5, 5), Movement(Direction.none, 0))
    val player     = Player(1, Position(0, 0)).asOpponentDecisionPlayer
    player.decideIntercept(ball) shouldBe Decision.Intercept(ball)

  "A teammate" should "be able to receive a pass" in:
    val ball: Ball = Ball(Position(5, 5), Movement(Direction.none, 0))
    val player     = Player(1, Position(0, 0), Movement.still, Some(ball)).asTeammateDecisionPlayer
    player.decideReceivePass(ball) shouldBe Decision.ReceivePass(ball)

  it should "be able to move randomly" in:
    val player = Player(1, Position(0, 0)).asTeammateDecisionPlayer
    player.decideMoveRandom(Direction(1.0, 0.0), 5) shouldBe Decision.MoveRandom(Direction(1.0, 0.0), 5)

  it should "be able to move to ball" in:
    val ball: Ball = Ball(Position(5, 5), Movement(Direction.none, 0))
    val player     = Player(1, Position(0, 0), Movement.still, Some(ball)).asTeammateDecisionPlayer
    player.decideMoveToBall(player.position.getDirection(ball.position)) shouldBe Decision.MoveToBall(
      player.position.getDirection(ball.position)
    )
