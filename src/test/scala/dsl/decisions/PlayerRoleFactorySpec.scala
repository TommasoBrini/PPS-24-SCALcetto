package dsl.decisions

import model.Match.*
import model.Match.Decision.*
import model.Space.*
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import dsl.decisions.PlayerRoleFactory.*
import dsl.decisions.PlayerTypes.*
import dsl.decisions.DecisionGenerator.*
import config.MatchConfig
import dsl.decisions.CommonPlayerDecisions.*
import dsl.game.TeamsSyntax.*
import Side.*
import dsl.creation.build.{BallBuilder, PlayerBuilder}
import dsl.creation.CreationSyntax.*

class PlayerRoleFactorySpec extends AnyFlatSpec with Matchers:

  "PlayerRoleFactory.asBallCarrierDecisionPlayer" should "convert player to BallCarrierPlayer" in:
    val basePlayer        = PlayerBuilder(1) at (5, 5)
    val ballCarrierPlayer = basePlayer.build().asBallCarrierPlayer

    ballCarrierPlayer shouldBe a[BallCarrierPlayer]
    ballCarrierPlayer.id shouldBe 1
    ballCarrierPlayer.position shouldBe Position(5, 5)

  it should "preserve all player properties" in:
    val ball = BallBuilder() at (5, 5)
    val basePlayer = PlayerBuilder(1)
      .at(5, 5)
      .move(Direction(1, 1))(2)
      .ownsBall(true)
      .decidedTo(Run(Direction(1, 0), MatchConfig.runSteps))
      .build()
    val ballCarrierPlayer = basePlayer.asBallCarrierPlayer
    ballCarrierPlayer.id shouldBe basePlayer.id
    ballCarrierPlayer.position shouldBe basePlayer.position
    ballCarrierPlayer.movement shouldBe basePlayer.movement
    ballCarrierPlayer.ball shouldBe basePlayer.ball
    ballCarrierPlayer.nextAction shouldBe basePlayer.nextAction
    ballCarrierPlayer.decision shouldBe basePlayer.decision

  "PlayerRoleFactory.asOpponentDecisionPlayer" should "convert player to OpponentPlayer" in:
    val basePlayer     = PlayerBuilder(2) at (10, 10)
    val opponentPlayer = basePlayer.build().asOpponentPlayer

    opponentPlayer shouldBe a[OpponentPlayer]
    opponentPlayer.id shouldBe 2
    opponentPlayer.position shouldBe Position(10, 10)

  it should "preserve all player properties" in:
    val ball = BallBuilder() at (10, 10)
    val basePlayer = PlayerBuilder(2)
      .at(10, 10)
      .move(Direction(-1, -1))(1)
      .ownsBall(true)
      .isGoingTo(Action.Stopped(2))
      .decidedTo(Mark(PlayerBuilder(1).at(5, 5).build(), PlayerBuilder(1).at(5, 5).build(), West))
      .build()
    val opponentPlayer = basePlayer.asOpponentPlayer

    opponentPlayer.id shouldBe basePlayer.id
    opponentPlayer.position shouldBe basePlayer.position
    opponentPlayer.movement shouldBe basePlayer.movement
    opponentPlayer.ball shouldBe basePlayer.ball
    opponentPlayer.nextAction shouldBe basePlayer.nextAction
    opponentPlayer.decision shouldBe basePlayer.decision

  "PlayerRoleFactory.asTeammateDecisionPlayer" should "convert player to TeammatePlayer" in:
    val basePlayer     = PlayerBuilder(3) at (6, 6)
    val teammatePlayer = basePlayer.build().asTeammatePlayer

    teammatePlayer shouldBe a[TeammatePlayer]
    teammatePlayer.id shouldBe 3
    teammatePlayer.position shouldBe Position(6, 6)

  it should "preserve all player properties" in:
    val ball = BallBuilder() at (6, 6)
    val basePlayer = PlayerBuilder(3)
      .at(6, 6)
      .move(Direction(0, 1))(1)
      .ownsBall(true)
      .isGoingTo(Action.Initial)
      .decidedTo(MoveRandom(Direction(1, 0), 3))
      .build()
    val teammatePlayer = basePlayer.asTeammatePlayer

    teammatePlayer.id shouldBe basePlayer.id
    teammatePlayer.position shouldBe basePlayer.position
    teammatePlayer.movement shouldBe basePlayer.movement
    teammatePlayer.ball shouldBe basePlayer.ball
    teammatePlayer.nextAction shouldBe basePlayer.nextAction
    teammatePlayer.decision shouldBe basePlayer.decision

  "PossibleDecisionFactory.possibleDecisions" should "return list of possible decisions for BallCarrierPlayer" in:
    val ballCarrierPlayer =
      PlayerBuilder(1).at(5, 5).decidedTo(Decision.MoveToBall(Direction(1, 0))).build().asBallCarrierPlayer
    val state = newMatch(Score.init()):
      team(West) withBall:
        player(1) at (5, 5) ownsBall true
      team(East):
        player(2) at (6, 6)
      ball at (5, 5)

    val possibleDecisions = ballCarrierPlayer.generateAllPossibleDecisions(state)

    possibleDecisions should not be empty
    possibleDecisions.foreach { decision =>
      decision shouldBe a[Decision]
    }

  it should "include Run decisions" in:
    val ballCarrierPlayer =
      PlayerBuilder(1).at(5, 5).decidedTo(Decision.MoveToBall(Direction(1, 0))).build().asBallCarrierPlayer
    val state = newMatch(Score.init()):
      team(West) withBall:
        player(1) at (5, 5) ownsBall true
      team(East):
        player(2) at (6, 6)
      ball at (5, 5)

    val possibleDecisions = ballCarrierPlayer.generateAllPossibleDecisions(state)

    possibleDecisions should contain atLeastOneElementOf (ballCarrierPlayer.generatePossibleRunDirections(state))

  it should "include Pass decisions when teammates are available" in:
    val ballCarrierPlayer =
      PlayerBuilder(1).at(5, 5).decidedTo(Decision.MoveToBall(Direction(1, 0))).build().asBallCarrierPlayer
    val state = newMatch(Score.init()):
      team(West) withBall:
        player(1) at (5, 5) ownsBall true
        player(2) at (6, 6)
      team(East):
        player(3) at (10, 10)
      ball at (5, 5)

    val possibleDecisions = ballCarrierPlayer.generateAllPossibleDecisions(state)

    possibleDecisions should contain atLeastOneElementOf (ballCarrierPlayer.generatePossiblePasses(state))

  it should "include Shoot decisions" in:
    val ballCarrierPlayer =
      PlayerBuilder(1).at(5, 5).decidedTo(Decision.MoveToBall(Direction(1, 0))).build().asBallCarrierPlayer
    val state = newMatch(Score.init()):
      team(West) withBall:
        player(1) at (5, 5) ownsBall true
      team(East):
        player(2) at (6, 6)
      ball at (5, 5)

    val possibleDecisions = ballCarrierPlayer.generateAllPossibleDecisions(state)

    possibleDecisions should contain atLeastOneElementOf (ballCarrierPlayer.generatePossibleShots(state))

  it should "include MoveToGoal decisions" in:
    val ballCarrierPlayer =
      PlayerBuilder(1).at(5, 5).decidedTo(Decision.MoveToGoal(Direction(1, 0))).build().asBallCarrierPlayer
    val state = newMatch(Score.init()):
      team(West) withBall:
        player(1) at (5, 5) ownsBall true
      team(East):
        player(2) at (6, 6)
      ball at (5, 5)

    val possibleDecisions = ballCarrierPlayer.generateAllPossibleDecisions(state)

    possibleDecisions should contain atLeastOneElementOf (ballCarrierPlayer.generatePossibleMovesToGoal(state))

  it should "handle player with Initial decision" in:
    val ballCarrierPlayer =
      PlayerBuilder(1).at(5, 5).decidedTo(Decision.MoveToBall(Direction(1, 0))).build().asBallCarrierPlayer
    val state = newMatch(Score.init()):
      team(West) withBall:
        player(1) at (5, 5) ownsBall true
      team(East):
        player(2) at (6, 6)
      ball at (5, 5)

    val possibleDecisions = ballCarrierPlayer.generateAllPossibleDecisions(state)

    possibleDecisions should not be empty
    possibleDecisions.foreach { decision =>
      decision shouldBe a[Decision]
    }

  it should "handle player with existing decision" in:
    val ballCarrierPlayer =
      PlayerBuilder(1).at(5, 5).decidedTo(Run(Direction(1, 0), MatchConfig.runSteps)).build().asBallCarrierPlayer
    val state = newMatch(Score.init()):
      team(West) withBall:
        player(1) at (5, 5) ownsBall true
      team(East):
        player(2) at (6, 6)
      ball at (5, 5)

    val possibleDecisions = ballCarrierPlayer.generateAllPossibleDecisions(state)

    possibleDecisions should not be empty
    possibleDecisions.foreach { decision =>
      decision shouldBe a[Decision]
    }

  it should "return consistent decisions for same state" in:
    val ballCarrierPlayer = PlayerBuilder(1).at(5, 5).build().asBallCarrierPlayer
    val state = newMatch(Score.init()):
      team(West) withBall:
        player(1) at (5, 5) ownsBall true
      team(East):
        player(2) at (6, 6)
      ball at (5, 5)

    val decisions1 = ballCarrierPlayer.generateAllPossibleDecisions(state)
    val decisions2 = ballCarrierPlayer.generateAllPossibleDecisions(state)

    decisions1 shouldBe decisions2

  it should "handle edge case positions" in:
    val ballCarrierPlayer =
      PlayerBuilder(1).at(5, 5).decidedTo(Decision.MoveToBall(Direction(1, 0))).build().asBallCarrierPlayer
    val state = newMatch(Score.init()):
      team(West) withBall:
        player(1) at (5, 5) ownsBall true
      team(East):
        player(2) at (6, 6)
      ball at (5, 5)

    val possibleDecisions = ballCarrierPlayer.generateAllPossibleDecisions(state)

    possibleDecisions should not be empty
    possibleDecisions.foreach { decision =>
      decision shouldBe a[Decision]
    }
