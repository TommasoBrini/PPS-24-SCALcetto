package model.decisions

import model.Match.*
import model.Match.Decision.*
import model.Space.*
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import model.decisions.PlayerRoleFactory.*
import model.decisions.PlayerTypes.*
import model.decisions.DecisionGenerator.*
import config.MatchConfig
import model.decisions.CommonPlayerDecisions.*

class PlayerRoleFactorySpec extends AnyFlatSpec with Matchers:

  "PlayerRoleFactory.asBallCarrierDecisionPlayer" should "convert player to BallCarrierPlayer" in:
    val basePlayer        = Player(1, Position(5, 5), Movement.still)
    val ballCarrierPlayer = basePlayer.asBallCarrierPlayer

    ballCarrierPlayer shouldBe a[BallCarrierPlayer]
    ballCarrierPlayer.id shouldBe 1
    ballCarrierPlayer.position shouldBe Position(5, 5)

  it should "preserve all player properties" in:
    val ball = Ball(Position(5, 5), Movement.still)
    val basePlayer = Player(
      1,
      Position(5, 5),
      Movement(Direction(1, 1), 2),
      ball = Some(ball),
      nextAction = Action.Move(Direction(1, 0), 1),
      decision = Run(Direction(1, 0), MatchConfig.runSteps)
    )
    val ballCarrierPlayer = basePlayer.asBallCarrierPlayer

    ballCarrierPlayer.id shouldBe basePlayer.id
    ballCarrierPlayer.position shouldBe basePlayer.position
    ballCarrierPlayer.movement shouldBe basePlayer.movement
    ballCarrierPlayer.ball shouldBe basePlayer.ball
    ballCarrierPlayer.nextAction shouldBe basePlayer.nextAction
    ballCarrierPlayer.decision shouldBe basePlayer.decision

  "PlayerRoleFactory.asOpponentDecisionPlayer" should "convert player to OpponentPlayer" in:
    val basePlayer     = Player(2, Position(10, 10), Movement.still)
    val opponentPlayer = basePlayer.asOpponentPlayer

    opponentPlayer shouldBe a[OpponentPlayer]
    opponentPlayer.id shouldBe 2
    opponentPlayer.position shouldBe Position(10, 10)

  it should "preserve all player properties" in:
    val ball = Ball(Position(10, 10), Movement.still)
    val basePlayer = Player(
      2,
      Position(10, 10),
      Movement(Direction(-1, -1), 1),
      ball = Some(ball),
      nextAction = Action.Stopped(2),
      decision = Mark(Player(1, Position(5, 5), Movement.still), Player(1, Position(5, 5), Movement.still), 1)
    )
    val opponentPlayer = basePlayer.asOpponentPlayer

    opponentPlayer.id shouldBe basePlayer.id
    opponentPlayer.position shouldBe basePlayer.position
    opponentPlayer.movement shouldBe basePlayer.movement
    opponentPlayer.ball shouldBe basePlayer.ball
    opponentPlayer.nextAction shouldBe basePlayer.nextAction
    opponentPlayer.decision shouldBe basePlayer.decision

  "PlayerRoleFactory.asTeammateDecisionPlayer" should "convert player to TeammatePlayer" in:
    val basePlayer     = Player(3, Position(6, 6), Movement.still)
    val teammatePlayer = basePlayer.asTeammatePlayer

    teammatePlayer shouldBe a[TeammatePlayer]
    teammatePlayer.id shouldBe 3
    teammatePlayer.position shouldBe Position(6, 6)

  it should "preserve all player properties" in:
    val ball = Ball(Position(6, 6), Movement.still)
    val basePlayer = Player(
      3,
      Position(6, 6),
      Movement(Direction(0, 1), 1),
      ball = Some(ball),
      nextAction = Action.Initial,
      decision = MoveRandom(Direction(1, 0), 3)
    )
    val teammatePlayer = basePlayer.asTeammatePlayer

    teammatePlayer.id shouldBe basePlayer.id
    teammatePlayer.position shouldBe basePlayer.position
    teammatePlayer.movement shouldBe basePlayer.movement
    teammatePlayer.ball shouldBe basePlayer.ball
    teammatePlayer.nextAction shouldBe basePlayer.nextAction
    teammatePlayer.decision shouldBe basePlayer.decision

  "PossibleDecisionFactory.possibleDecisions" should "return list of possible decisions for BallCarrierPlayer" in:
    val ballCarrierPlayer = Player(1, Position(5, 5), Movement.still).asBallCarrierPlayer
    val teammate          = Player(2, Position(6, 6), Movement.still).asTeammatePlayer
    val team1             = Team(1, List(ballCarrierPlayer, teammate), hasBall = true)
    val team2             = Team(2, List(), hasBall = false)
    val state             = MatchState((team1, team2), Ball(Position(0, 0), Movement.still))

    val possibleDecisions = ballCarrierPlayer.generateAllPossibleDecisions(state)

    possibleDecisions should not be empty
    possibleDecisions.foreach { decision =>
      decision shouldBe a[Decision]
    }

  it should "include Run decisions" in:
    val ballCarrierPlayer =
      Player(1, Position(5, 5), Movement.still, decision = Decision.MoveToBall(Direction(1, 0))).asBallCarrierPlayer
    val team1 = Team(1, List(ballCarrierPlayer), hasBall = true)
    val team2 = Team(2, List(), hasBall = false)
    val state = MatchState((team1, team2), Ball(Position(0, 0), Movement.still))

    val possibleDecisions = ballCarrierPlayer.generateAllPossibleDecisions(state)

    possibleDecisions should contain atLeastOneElementOf (ballCarrierPlayer.generatePossibleRunDirections(state))

  it should "include Pass decisions when teammates are available" in:
    val ballCarrierPlayer = Player(1, Position(5, 5), Movement.still).asBallCarrierPlayer
    val teammate          = Player(2, Position(6, 6), Movement.still).asTeammatePlayer
    val team1             = Team(1, List(ballCarrierPlayer, teammate), hasBall = true)
    val team2             = Team(2, List(), hasBall = false)
    val state             = MatchState((team1, team2), Ball(Position(0, 0), Movement.still))

    val possibleDecisions = ballCarrierPlayer.generateAllPossibleDecisions(state)

    possibleDecisions should contain atLeastOneElementOf (ballCarrierPlayer.generatePossiblePasses(state))

  it should "include Shoot decisions" in:
    val ballCarrierPlayer =
      Player(1, Position(5, 5), Movement.still, decision = Decision.MoveToBall(Direction(1, 0))).asBallCarrierPlayer
    val team1 = Team(1, List(ballCarrierPlayer), hasBall = true)
    val team2 = Team(2, List(), hasBall = false)
    val state = MatchState((team1, team2), Ball(Position(0, 0), Movement.still))

    val possibleDecisions = ballCarrierPlayer.generateAllPossibleDecisions(state)

    possibleDecisions should contain atLeastOneElementOf (ballCarrierPlayer.generatePossibleShots(state))

  it should "include MoveToGoal decisions" in:
    val ballCarrierPlayer =
      Player(1, Position(5, 5), Movement.still, decision = Decision.MoveToGoal(Direction(1, 0))).asBallCarrierPlayer
    val team1 = Team(1, List(ballCarrierPlayer), hasBall = true)
    val team2 = Team(2, List(), hasBall = false)
    val state = MatchState((team1, team2), Ball(Position(0, 0), Movement.still))

    val possibleDecisions = ballCarrierPlayer.generateAllPossibleDecisions(state)

    possibleDecisions should contain atLeastOneElementOf (ballCarrierPlayer.generatePossibleMovesToGoal(state))

  it should "handle player with Initial decision" in:
    val ballCarrierPlayer =
      Player(1, Position(5, 5), Movement.still, decision = Decision.MoveToBall(Direction(1, 0))).asBallCarrierPlayer
    val team1 = Team(1, List(ballCarrierPlayer), hasBall = true)
    val team2 = Team(2, List(), hasBall = false)
    val state = MatchState((team1, team2), Ball(Position(0, 0), Movement.still))

    val possibleDecisions = ballCarrierPlayer.generateAllPossibleDecisions(state)

    possibleDecisions should not be empty
    possibleDecisions.foreach { decision =>
      decision shouldBe a[Decision]
    }

  it should "handle player with existing decision" in:
    val ballCarrierPlayer =
      Player(
        1,
        Position(5, 5),
        Movement.still,
        decision = Run(Direction(1, 0), MatchConfig.runSteps)
      ).asBallCarrierPlayer
    val team1 = Team(1, List(ballCarrierPlayer), hasBall = true)
    val team2 = Team(2, List(), hasBall = false)
    val state = MatchState((team1, team2), Ball(Position(0, 0), Movement.still))

    val possibleDecisions = ballCarrierPlayer.generateAllPossibleDecisions(state)

    possibleDecisions should not be empty
    possibleDecisions.foreach { decision =>
      decision shouldBe a[Decision]
    }

  it should "return consistent decisions for same state" in:
    val ballCarrierPlayer = Player(1, Position(5, 5), Movement.still).asBallCarrierPlayer
    val team1             = Team(1, List(ballCarrierPlayer), hasBall = true)
    val team2             = Team(2, List(), hasBall = false)
    val state             = MatchState((team1, team2), Ball(Position(0, 0), Movement.still))

    val decisions1 = ballCarrierPlayer.generateAllPossibleDecisions(state)
    val decisions2 = ballCarrierPlayer.generateAllPossibleDecisions(state)

    decisions1 shouldBe decisions2

  it should "handle edge case positions" in:
    val ballCarrierPlayer =
      Player(1, Position(0, 0), Movement.still, decision = Decision.MoveToBall(Direction(1, 0))).asBallCarrierPlayer
    val team1 = Team(1, List(ballCarrierPlayer), hasBall = true)
    val team2 = Team(2, List(), hasBall = false)
    val state = MatchState((team1, team2), Ball(Position(0, 0), Movement.still))

    val possibleDecisions = ballCarrierPlayer.generateAllPossibleDecisions(state)

    possibleDecisions should not be empty
    possibleDecisions.foreach { decision =>
      decision shouldBe a[Decision]
    }
