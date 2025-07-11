package dsl.decisions

import dsl.decisions.DecisionValidator.*
import model.Match.Action.*
import model.Match.Decision.*
import model.Match.*
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class DecisionValidatorSpec extends AnyFlatSpec with Matchers:

  "A decision" should "always result in an action" in:
    val decision = Run(Direction.none, 1)
    decision.toAction shouldBe a[Action]

  "A pass decision" should "have his success rate" in:
    val decision = Pass(Player(1, Position(0, 0)), Player(2, Position(0, 0)))
    decision.getSuccessRate should be(DecisionSuccessRate.Pass)

  "A tackle decision" should "have his success rate" in:
    val decision = Tackle(Ball(Position(0, 0)))
    decision.getSuccessRate should be(DecisionSuccessRate.Tackle)

  "A short distance shot decision" should "have his success rate" in:
    val decision = Shoot(Player(1, Position(0, 0)), goal = Position(0, 50))
    decision.getSuccessRate should be(DecisionSuccessRate.ShortDistanceShot)

  "A long distance shot" should "have his success rate" in:
    val decision = Shoot(Player(1, Position(0, 0)), goal = Position(0, 200))
    decision.getSuccessRate should be(DecisionSuccessRate.LongDistanceShot)

  "Others decision" should "be always successful" in:
    val decision = Run(Direction.none, 1)
    decision.getSuccessRate should be(DecisionSuccessRate.SureDecision)

  "A pass decision" should "result in a hit from player to player when successful" in:
    val from           = Player(0, Position(0, 0))
    val to             = Player(0, Position(10, 10))
    val rightDirection = from.position.getDirection(to.position)
    Pass(from, to).getSuccessAction should matchPattern { case Hit(direction, _) => }

  it should "result in a moved hit from player to player when failed" in:
    val from           = Player(0, Position(0, 0))
    val to             = Player(0, Position(10, 10))
    val action         = Pass(from, to).getFailureAction(0.9)
    val rightDirection = from.position.getDirection(to.position)
    action match
      case Hit(actual, _) => actual should not equal rightDirection
      case _              => fail("Wrong action")

  "A shoot decision" should "result in a hit from player to goal when successful" in:
    val shooter        = Player(0, Position(0, 0))
    val goalPosition   = Position(10, 10)
    val rightDirection = shooter.position.getDirection(goalPosition)
    getSuccessAction(Shoot(shooter, goalPosition)) match
      case Hit(actual, _) => actual should be(rightDirection)
      case _              => fail("Wrong action")

  it should "result in a moved hit from player to goal when failed" in:
    val shooter        = Player(0, Position(0, 0))
    val goalPosition   = Position(10, 10)
    val action         = Shoot(shooter, goalPosition).getFailureAction(0.9)
    val rightDirection = shooter.position.getDirection(goalPosition)
    action match
      case Hit(actual, _) => actual should not equal rightDirection
      case _              => fail("Wrong action")

  "A tackle decision" should "result in a ball take when successful" in:
    val ball = Ball(Position(0, 0))
    getSuccessAction(Tackle(ball)) shouldBe Take(ball)

  it should "result in a stopped action when failed" in:
    val ball = Ball(Position(0, 0))
    Tackle(ball).getFailureAction(1.0) should matchPattern { case Stopped(_) => }

  "Other decisions" should "result in an initial action when failed" in:
    Run(Direction(1, 0), 1).getFailureAction(1.0) should be(Action.Initial)

  "A confusion decision" should "result in a stopped action" in:
    Confusion(2).getSuccessAction shouldBe Stopped(2)

  "A run decision" should "result in a move" in:
    val direction = Direction(1, 1)
    Run(direction, 1).getSuccessAction match
      case Move(actual, _) => actual should be(direction)
      case _               => fail("Wrong action")

  "A move to goal decision" should "result in a move to goal direction" in {
    val direction = Direction(0.5, 0.5)
    val decision  = MoveToGoal(direction)
    getSuccessAction(decision) match
      case Move(actual, _) => actual should be(direction)
      case _               => fail("Wrong action")
  }

  "A receive pass decision" should "result in a ball take" in {
    val ball = Ball(Position(0, 0))
    ReceivePass(ball).getSuccessAction shouldBe Take(ball)
  }

  "A intercept decision" should "result in a ball take" in {
    val ball = Ball(Position(0, 0))
    Intercept(ball).getSuccessAction shouldBe Take(ball)
  }

  "A move to ball decision" should "result in a move to ball direction" in {
    val direction = Direction(-0.5, 0.8)
    MoveToBall(direction).getSuccessAction match
      case Move(actual, _) => actual should be(direction)
      case _               => fail("Wrong action")
  }

  "A move random decision" should "result in a move to a random direction" in:
    val direction = Direction(0.3, -0.7)
    getSuccessAction(MoveRandom(direction, 1)) match
      case Move(actual, _) => actual should be(direction)
      case _               => fail("Wrong action")

  "A mark decision" should "result in a move to target if he has the ball" in:
    val defender       = Player(0, Position(0, 0))
    val target         = Player(0, Position(10, 10), ball = Some(Ball(Position(10, 10))))
    val rightDirection = defender.position.getDirection(target.position)
    Mark(defender, target, Side.West).getSuccessAction match
      case Move(actual, _) => actual should be(rightDirection)
      case _               => fail("Wrong action")

  it should "not result in a move to target if he doesn't have the ball" in:
    val defender = Player(0, Position(0, 0))
    val target   = Player(0, Position(10, 10), ball = None)
    val action   = getSuccessAction(Mark(defender, target, Side.East))
    val toTarget = defender.position.getDirection(target.position)
    action match
      case Move(actual, _) => actual should not be (toTarget)
      case _               => fail("Wrong action")
