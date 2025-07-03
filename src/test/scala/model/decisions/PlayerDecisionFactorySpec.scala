package model.decisions

import model.Match.*
import model.Match.Decision.*
import model.Space.*
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import model.decisions.PlayerDecisionFactory.*
import model.decisions.DecisorPlayer.*
import model.decisions.PossibleDecisionFactory.*
import model.decisions.CommonPlayerDecisions.*
import config.MatchConfig

class PlayerDecisionFactorySpec extends AnyFlatSpec with Matchers:

  "PlayerDecisionFactory.asControlDecisionPlayer" should "convert player to ControlPlayer" in:
    val basePlayer    = Player(1, Position(5, 5), Movement.still)
    val controlPlayer = basePlayer.asControlDecisionPlayer

    controlPlayer shouldBe a[ControlPlayer]
    controlPlayer.id shouldBe 1
    controlPlayer.position shouldBe Position(5, 5)

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
    val controlPlayer = basePlayer.asControlDecisionPlayer

    controlPlayer.id shouldBe basePlayer.id
    controlPlayer.position shouldBe basePlayer.position
    controlPlayer.movement shouldBe basePlayer.movement
    controlPlayer.ball shouldBe basePlayer.ball
    controlPlayer.nextAction shouldBe basePlayer.nextAction
    controlPlayer.decision shouldBe basePlayer.decision

  "PlayerDecisionFactory.asOpponentDecisionPlayer" should "convert player to OpponentPlayer" in:
    val basePlayer     = Player(2, Position(10, 10), Movement.still)
    val opponentPlayer = basePlayer.asOpponentDecisionPlayer

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
      decision = Mark(Player(1, Position(5, 5), Movement.still), Player(1, Position(5, 5), Movement.still))
    )
    val opponentPlayer = basePlayer.asOpponentDecisionPlayer

    opponentPlayer.id shouldBe basePlayer.id
    opponentPlayer.position shouldBe basePlayer.position
    opponentPlayer.movement shouldBe basePlayer.movement
    opponentPlayer.ball shouldBe basePlayer.ball
    opponentPlayer.nextAction shouldBe basePlayer.nextAction
    opponentPlayer.decision shouldBe basePlayer.decision

  "PlayerDecisionFactory.asTeammateDecisionPlayer" should "convert player to TeammatePlayer" in:
    val basePlayer     = Player(3, Position(6, 6), Movement.still)
    val teammatePlayer = basePlayer.asTeammateDecisionPlayer

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
    val teammatePlayer = basePlayer.asTeammateDecisionPlayer

    teammatePlayer.id shouldBe basePlayer.id
    teammatePlayer.position shouldBe basePlayer.position
    teammatePlayer.movement shouldBe basePlayer.movement
    teammatePlayer.ball shouldBe basePlayer.ball
    teammatePlayer.nextAction shouldBe basePlayer.nextAction
    teammatePlayer.decision shouldBe basePlayer.decision

  "PossibleDecisionFactory.possibleDecisions" should "return list of possible decisions for ControlPlayer" in:
    val controlPlayer = Player(1, Position(5, 5), Movement.still).asControlDecisionPlayer
    val teammate      = Player(2, Position(6, 6), Movement.still).asTeammateDecisionPlayer
    val team1         = Team(1, List(controlPlayer, teammate), hasBall = true)
    val team2         = Team(2, List(), hasBall = false)
    val state         = MatchState(List(team1, team2), Ball(Position(0, 0), Movement.still))

    val possibleDecisions = controlPlayer.possibleDecisions(state)

    possibleDecisions should not be empty
    possibleDecisions.foreach { decision =>
      decision shouldBe a[Decision]
    }

  it should "include Run decisions" in:
    val controlPlayer = Player(1, Position(5, 5), Movement.still).asControlDecisionPlayer
    val team1         = Team(1, List(controlPlayer), hasBall = true)
    val team2         = Team(2, List(), hasBall = false)
    val state         = MatchState(List(team1, team2), Ball(Position(0, 0), Movement.still))

    val possibleDecisions = controlPlayer.possibleDecisions(state)

    possibleDecisions should contain atLeastOneElementOf (controlPlayer.possibleRunDirections(state))

  it should "include Pass decisions when teammates are available" in:
    val controlPlayer = Player(1, Position(5, 5), Movement.still).asControlDecisionPlayer
    val teammate      = Player(2, Position(6, 6), Movement.still).asTeammateDecisionPlayer
    val team1         = Team(1, List(controlPlayer, teammate), hasBall = true)
    val team2         = Team(2, List(), hasBall = false)
    val state         = MatchState(List(team1, team2), Ball(Position(0, 0), Movement.still))

    val possibleDecisions = controlPlayer.possibleDecisions(state)

    possibleDecisions should contain atLeastOneElementOf (controlPlayer.possiblePasses(state))

  it should "include Shoot decisions" in:
    val controlPlayer = Player(1, Position(5, 5), Movement.still).asControlDecisionPlayer
    val team1         = Team(1, List(controlPlayer), hasBall = true)
    val team2         = Team(2, List(), hasBall = false)
    val state         = MatchState(List(team1, team2), Ball(Position(0, 0), Movement.still))

    val possibleDecisions = controlPlayer.possibleDecisions(state)

    possibleDecisions should contain atLeastOneElementOf (controlPlayer.possibleShots(state))

  it should "include MoveToGoal decisions" in:
    val controlPlayer =
      Player(1, Position(5, 5), Movement.still, decision = Decision.MoveToGoal(Direction(1, 0))).asControlDecisionPlayer
    val team1 = Team(1, List(controlPlayer), hasBall = true)
    val team2 = Team(2, List(), hasBall = false)
    val state = MatchState(List(team1, team2), Ball(Position(0, 0), Movement.still))

    val possibleDecisions = controlPlayer.possibleDecisions(state)

    possibleDecisions should contain atLeastOneElementOf (controlPlayer.possibleMovesToGoal(state))

  it should "handle player with Initial decision" in:
    val controlPlayer = Player(1, Position(5, 5), Movement.still, decision = Decision.Initial).asControlDecisionPlayer
    val team1         = Team(1, List(controlPlayer), hasBall = true)
    val team2         = Team(2, List(), hasBall = false)
    val state         = MatchState(List(team1, team2), Ball(Position(0, 0), Movement.still))

    val possibleDecisions = controlPlayer.possibleDecisions(state)

    possibleDecisions should not be empty
    possibleDecisions.foreach { decision =>
      decision shouldBe a[Decision]
    }

  it should "handle player with existing decision" in:
    val controlPlayer =
      Player(
        1,
        Position(5, 5),
        Movement.still,
        decision = Run(Direction(1, 0), MatchConfig.runSteps)
      ).asControlDecisionPlayer
    val team1 = Team(1, List(controlPlayer), hasBall = true)
    val team2 = Team(2, List(), hasBall = false)
    val state = MatchState(List(team1, team2), Ball(Position(0, 0), Movement.still))

    val possibleDecisions = controlPlayer.possibleDecisions(state)

    possibleDecisions should not be empty
    possibleDecisions.foreach { decision =>
      decision shouldBe a[Decision]
    }

  it should "return consistent decisions for same state" in:
    val controlPlayer = Player(1, Position(5, 5), Movement.still).asControlDecisionPlayer
    val team1         = Team(1, List(controlPlayer), hasBall = true)
    val team2         = Team(2, List(), hasBall = false)
    val state         = MatchState(List(team1, team2), Ball(Position(0, 0), Movement.still))

    val decisions1 = controlPlayer.possibleDecisions(state)
    val decisions2 = controlPlayer.possibleDecisions(state)

    decisions1 shouldBe decisions2

  it should "handle edge case positions" in:
    val controlPlayer = Player(1, Position(0, 0), Movement.still).asControlDecisionPlayer
    val team1         = Team(1, List(controlPlayer), hasBall = true)
    val team2         = Team(2, List(), hasBall = false)
    val state         = MatchState(List(team1, team2), Ball(Position(0, 0), Movement.still))

    val possibleDecisions = controlPlayer.possibleDecisions(state)

    possibleDecisions should not be empty
    possibleDecisions.foreach { decision =>
      decision shouldBe a[Decision]
    }
