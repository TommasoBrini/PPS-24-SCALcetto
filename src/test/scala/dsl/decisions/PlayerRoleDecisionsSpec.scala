package dsl.decisions

import model.Match.*
import model.Match.Decision.*
import model.Space.*
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import dsl.decisions.CommonPlayerDecisions.*
import config.UIConfig
import dsl.decisions.PlayerRoleFactory.*
import config.MatchConfig
import dsl.decisions.DecisionGenerator.*
import Side.*
import dsl.creation.build.PlayerBuilder
import dsl.creation.CreationSyntax.*
import dsl.creation.build.BallBuilder

class PlayerRoleDecisionsSpec extends AnyFlatSpec with Matchers:

  "CommonPlayerDecisions.decideRun" should "create Run decision with correct direction" in:
    val player    = PlayerBuilder(1).at(5, 5).build()
    val direction = Direction(1, 0)
    val decision  = player.createRunDecision(direction, MatchConfig.runSteps)

    decision shouldBe a[Run]
    decision.asInstanceOf[Run].direction shouldBe direction

  it should "work with different directions" in:
    val player = PlayerBuilder(1).at(5, 5).build()
    val directions = List(
      Direction(1, 1),
      Direction(-1, -1),
      Direction(0, 1),
      Direction(1, 0),
      Direction(-1, 0),
      Direction(0, -1)
    )

    directions.foreach { direction =>
      val decision = player.createRunDecision(direction, MatchConfig.runSteps)
      decision shouldBe a[Run]
      decision.asInstanceOf[Run].direction shouldBe direction
    }

  "CommonPlayerDecisions.possibleRuns" should "return list of Run decisions" in:
    val commonPlayer = PlayerBuilder(1).at(5, 5).build()
    val state = newMatch(Score.init()):
      team(West) withBall:
        player(1) at (5, 5) ownsBall true
      team(East):
        player(2) at (6, 6)
      ball at (0, 0)

    val possibleRuns = commonPlayer.generatePossibleRunDirections(state)

    possibleRuns should not be empty
    possibleRuns.foreach { decision =>
      decision shouldBe a[Run]
    }

  it should "exclude stationary movement (0,0)" in:
    val commonPlayer = PlayerBuilder(1).at(5, 5).build()
    val state = newMatch(Score.init()):
      team(West) withBall:
        player(1) at (5, 5) ownsBall true
      team(East):
        player(2) at (6, 6)
      ball at (0, 0)

    val possibleRuns = commonPlayer.generatePossibleRunDirections(state)

    possibleRuns should not contain (Run(Direction(0, 0), MatchConfig.runSteps))

  it should "include all valid movement directions" in:
    val commonPlayer = PlayerBuilder(1).at(5, 5).build()
    val state = newMatch(Score.init()):
      team(West) withBall:
        player(1) at (5, 5) ownsBall true
      team(East):
        player(2) at (6, 6)
      ball at (0, 0)

    val possibleRuns = commonPlayer.generatePossibleRunDirections(state)

    possibleRuns should have size 4

  "CommonPlayerDecisions.decideConfusion" should "create Confusion decision with correct steps" in:
    val commonPlayer = PlayerBuilder(1).at(5, 5).build()
    val steps        = 3
    val decision     = commonPlayer.createConfusionDecision(steps)

    decision shouldBe a[Confusion]
    decision.asInstanceOf[Confusion].remainingStep shouldBe steps

  it should "work with zero steps" in:
    val commonPlayer = PlayerBuilder(1).at(5, 5).build()
    val decision     = commonPlayer.createConfusionDecision(0)

    decision shouldBe a[Confusion]
    decision.asInstanceOf[Confusion].remainingStep shouldBe 0

  "CommonPlayerDecisions.decideMoveToBall" should "create MoveToBall decision with correct direction" in:
    val commonPlayer = PlayerBuilder(1).at(5, 5).build()
    val direction    = Direction(1, 1)
    val decision     = commonPlayer.createMoveToBallDecision(direction)

    decision shouldBe a[MoveToBall]
    decision.asInstanceOf[MoveToBall].directionToBall shouldBe direction

  "CanDecideToPass.decidePass" should "create Pass decision with correct players" in:
    val passer   = PlayerBuilder(1).at(5, 5).build().asBallCarrierPlayer
    val receiver = PlayerBuilder(2).at(6, 6).build()
    val decision = passer.createPassDecision(receiver)

    decision shouldBe a[Pass]
    decision.asInstanceOf[Pass].from shouldBe passer
    decision.asInstanceOf[Pass].to shouldBe receiver

  "CanDecideToPass.possiblePasses" should "return list of Pass decisions for teammates" in:
    val passer    = PlayerBuilder(1).at(5, 5).build().asBallCarrierPlayer
    val teammate1 = PlayerBuilder(2).at(6, 6).build()
    val teammate2 = PlayerBuilder(3).at(7, 7).build()
    val opponent  = PlayerBuilder(4).at(10, 10).build()
    val team1     = Team(List(passer, teammate1, teammate2), West, hasBall = true)
    val team2     = Team(List(opponent), East, hasBall = false)
    val state     = MatchState((team1, team2), Ball(Position(0, 0), Movement.still))

    val possiblePasses = passer.generatePossiblePasses(state)

    possiblePasses should have size 2
    possiblePasses.foreach { decision =>
      decision shouldBe a[Pass]
      decision.asInstanceOf[Pass].from shouldBe passer
      decision.asInstanceOf[Pass].to should (be(teammate1) or be(teammate2))
    }

  it should "not include passes to opponents" in:
    val passer   = PlayerBuilder(1).at(5, 5).build().asBallCarrierPlayer
    val teammate = PlayerBuilder(2).at(6, 6).build()
    val opponent = PlayerBuilder(3).at(10, 10).build()
    val team1    = Team(List(passer, teammate), West, hasBall = true)
    val team2    = Team(List(opponent), East, hasBall = false)
    val state    = MatchState((team1, team2), Ball(Position(0, 0), Movement.still))

    val possiblePasses = passer.generatePossiblePasses(state)

    possiblePasses should have size 1
    possiblePasses.head.asInstanceOf[Pass].to shouldBe teammate

  it should "not include passes to self" in:
    val passer = PlayerBuilder(1).at(5, 5).build().asBallCarrierPlayer
    val state = newMatch(Score.init()):
      team(West) withBall:
        player(1) at (5, 5) ownsBall true
      team(East):
        player(2) at (6, 6)
      ball at (0, 0)

    val possiblePasses = passer.generatePossiblePasses(state)

    possiblePasses should be(empty)

  "CanDecideToShoot.decideShoot" should "create Shoot decision with correct parameters" in:
    val striker  = PlayerBuilder(1).at(5, 5).build().asBallCarrierPlayer
    val goal     = Position(10, 5)
    val decision = striker.createShootDecision(goal)

    decision shouldBe a[Shoot]
    decision.asInstanceOf[Shoot].striker shouldBe striker
    decision.asInstanceOf[Shoot].goal shouldBe goal

  "CanDecideToShoot.possibleShots" should "return list of Shoot decisions for all goal positions" in:
    val striker = Player(1, Position(5, 5), Movement.still).asBallCarrierPlayer
    val state = newMatch(Score.init()):
      team(West) withBall:
        player(1) at (5, 5) ownsBall true
      team(East):
        player(2) at (6, 6)
      ball at (0, 0)

    val possibleShots = striker.generatePossibleShots(state)

    possibleShots should have size 3
    possibleShots.foreach { decision =>
      decision shouldBe a[Shoot]
      decision.asInstanceOf[Shoot].striker shouldBe striker
    }

  it should "include correct goal positions for team 1" in:
    val striker = PlayerBuilder(1).at(5, 5).build().asBallCarrierPlayer
    val state = newMatch(Score.init()):
      team(West) withBall:
        player(1) at (5, 5) ownsBall true
      team(East):
        player(2) at (6, 6)
      ball at (0, 0)

    val possibleShots = striker.generatePossibleShots(state)

    val expectedGoals = List(
      Position(UIConfig.goalEastX, UIConfig.firstPoleY),
      Position(UIConfig.goalEastX, UIConfig.midGoalY),
      Position(UIConfig.goalEastX, UIConfig.secondPoleY)
    )

    possibleShots.map(_.asInstanceOf[Shoot].goal) should contain theSameElementsAs expectedGoals

  "CanDecideToMoveToGoal.decideMoveToGoal" should "create MoveToGoal decision with correct direction" in:
    val player    = PlayerBuilder(1).at(5, 5).build().asBallCarrierPlayer
    val direction = Direction(1, 0)
    val decision  = player.createMoveToGoalDecision(direction)

    decision shouldBe a[MoveToGoal]
    decision.asInstanceOf[MoveToGoal].goalDirection shouldBe direction

  it should "return MoveToGoal decision for non-Initial decision" in:
    val carrier = PlayerBuilder(1).at(5, 5).build().asBallCarrierPlayer
    val state = newMatch(Score.init()):
      team(West) withBall:
        player(1) at (5, 5) ownsBall true
      team(East):
        player(2) at (6, 6)
      ball at (0, 0)

    val possibleMoves = carrier.generatePossibleMovesToGoal(state)

    possibleMoves should have size 1
    possibleMoves.head shouldBe a[MoveToGoal]

  it should "calculate correct direction to goal for team 1" in:
    val carrier = PlayerBuilder(1).at(5, 5).build().asBallCarrierPlayer
    val state = newMatch(Score.init()):
      team(West) withBall:
        player(1) at (5, 5) ownsBall true
      team(East):
        player(2) at (6, 6)
      ball at (0, 0)

    val possibleMoves = carrier.generatePossibleMovesToGoal(state)

    val moveToGoal        = possibleMoves.head.asInstanceOf[MoveToGoal]
    val expectedGoal      = Position(UIConfig.fieldWidth, UIConfig.fieldHeight / 2)
    val expectedDirection = carrier.position.getDirection(expectedGoal)

    moveToGoal.goalDirection shouldBe expectedDirection

  "CanDecideToMark.decideMark" should "create Mark decision with correct players" in:
    val marker   = PlayerBuilder(1).at(5, 5).build().asOpponentPlayer
    val target   = PlayerBuilder(2).at(6, 6).build()
    val decision = marker.createMarkDecision(target, West)

    decision shouldBe a[Mark]
    decision.asInstanceOf[Mark].defender shouldBe marker
    decision.asInstanceOf[Mark].target shouldBe target

  "CanDecideToTackle.decideTackle" should "create Tackle decision with correct ball" in:
    val tackler  = PlayerBuilder(1).at(5, 5).build().asOpponentPlayer
    val ball     = BallBuilder().at(6, 6).build()
    val decision = tackler.createTackleDecision(ball)

    decision shouldBe a[Tackle]
    decision.asInstanceOf[Tackle].ball shouldBe ball

  "CanDecideToIntercept.decideIntercept" should "create Intercept decision with correct ball" in:
    val interceptor = PlayerBuilder(1).at(5, 5).build().asOpponentPlayer
    val ball        = BallBuilder().at(6, 6).build()
    val decision    = interceptor.createInterceptDecision(ball)

    decision shouldBe a[Intercept]
    decision.asInstanceOf[Intercept].ball shouldBe ball

  "CanDecideToMoveRandom.decideMoveRandom" should "create MoveRandom decision with correct parameters" in:
    val player    = PlayerBuilder(1).at(5, 5).build().asTeammatePlayer
    val direction = Direction(1, 1)
    val steps     = 3
    val decision  = player.createRandomMovementDecision(direction, steps)

    decision shouldBe a[MoveRandom]
    decision.asInstanceOf[MoveRandom].direction shouldBe direction
    decision.asInstanceOf[MoveRandom].steps shouldBe steps

  "CanDecideToReceivePass.decideReceivePass" should "create ReceivePass decision with correct ball" in:
    val receiver = PlayerBuilder(1).at(5, 5).build().asTeammatePlayer
    val ball     = BallBuilder().at(6, 6).build()
    val decision = receiver.createReceivePassDecision(ball)

    decision shouldBe a[ReceivePass]
    decision.asInstanceOf[ReceivePass].ball shouldBe ball
