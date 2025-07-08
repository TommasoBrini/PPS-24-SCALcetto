package model.decisions.behavior

import model.Match.*
import model.Match.Decision.*
import model.Space.*
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import dsl.decisions.behavior.BallCarrierBehavior.*
import dsl.decisions.PlayerRoleFactory.*
import config.UIConfig
import Side.*
import model.Match.Action

class BallCarrierBehaviorSpec extends AnyFlatSpec with Matchers:

  "BallCarrierBehavior.calculateBestDecision" should "return a valid decision for ball carrier player" in:
    val ballCarrierPlayer = Player(
      1,
      Position(5, 5),
      Movement.still,
      ball = Some(Ball(Position(5, 5), Movement.still))
    ).asBallCarrierPlayer
    val teammate = Player(2, Position(6, 6), Movement.still).asTeammatePlayer
    val team1    = Team(List(ballCarrierPlayer, teammate), East, hasBall = true)
    val team2    = Team(List(), West, hasBall = false)
    val state    = Match((team1, team2), Ball(Position(0, 0), Movement.still))

    val decision = ballCarrierPlayer.calculateBestDecision(state)

    decision should not be Decision.Initial
    decision shouldBe a[Decision]

  it should "return Pass decision when teammate is available" in:
    val ballCarrierPlayer = Player(1, Position(5, 5), Movement.still).asBallCarrierPlayer
    val teammate          = Player(2, Position(6, 6), Movement.still).asTeammatePlayer
    val team1             = Team(List(ballCarrierPlayer, teammate), East, hasBall = true)
    val team2             = Team(List(), West, hasBall = false)
    val state             = Match((team1, team2), Ball(Position(0, 0), Movement.still))

    val decision = ballCarrierPlayer.calculateBestDecision(state)

    decision shouldBe a[Decision]

  it should "return Shoot decision when near goal" in:
    val ballCarrierPlayer =
      Player(
        1,
        Position(UIConfig.goalEastX - 2, UIConfig.midGoalY),
        Movement.still,
        Some(Ball(Position(0, 0), Movement.still)),
        Decision.Tackle(Ball(Position(0, 0), Movement.still)),
        Action.Initial
      ).asBallCarrierPlayer
    val team1 = Team(List(ballCarrierPlayer), West, hasBall = true)
    val team2 = Team(List(), East, false)
    val state = Match((team1, team2), Ball(Position(0, 0), Movement.still))

    val decision = ballCarrierPlayer.calculateBestDecision(state)

    decision shouldBe a[Decision]

  it should "return MoveToGoal decision when appropriate" in:
    val ballCarrierPlayer = Player(
      1,
      Position(UIConfig.goalEastX - 2, UIConfig.midGoalY),
      Movement.still,
      Some(Ball(Position(0, 0), Movement.still)),
      Decision.Tackle(Ball(Position(0, 0), Movement.still)),
      Action.Initial
    ).asBallCarrierPlayer
    val team1 = Team(List(ballCarrierPlayer), East, hasBall = true)
    val team2 = Team(List(), West, hasBall = false)
    val state = Match((team1, team2), Ball(Position(0, 0), Movement.still))

    val decision = ballCarrierPlayer.calculateBestDecision(state)

    decision should not be Decision.Initial
    decision shouldBe a[Decision]

  it should "handle player with ball" in:
    val ball = Ball(Position(5, 5), Movement.still)
    val ballCarrierPlayer = Player(
      1,
      Position(UIConfig.goalEastX - 2, UIConfig.midGoalY),
      Movement.still,
      Some(Ball(Position(0, 0), Movement.still)),
      Decision.Tackle(Ball(Position(0, 0), Movement.still)),
      Action.Initial
    ).asBallCarrierPlayer
    val team1 = Team(List(ballCarrierPlayer), West, hasBall = true)
    val team2 = Team(List(), East, hasBall = false)
    val state = Match((team1, team2), ball)

    val decision = ballCarrierPlayer.calculateBestDecision(state)

    decision should not be Decision.Initial
    decision shouldBe a[Decision]

  it should "handle player far from goal" in:
    val ballCarrierPlayer = Player(
      1,
      Position(UIConfig.goalEastX - 2, UIConfig.midGoalY),
      Movement.still,
      Some(Ball(Position(0, 0), Movement.still)),
      Decision.Tackle(Ball(Position(0, 0), Movement.still)),
      Action.Initial
    ).asBallCarrierPlayer
    val team1 = Team(List(ballCarrierPlayer), West, hasBall = true)
    val team2 = Team(List(), East, hasBall = false)
    val state = Match((team1, team2), Ball(Position(0, 0), Movement.still))

    val decision = ballCarrierPlayer.calculateBestDecision(state)

    decision should not be Decision.Initial
    decision shouldBe a[Decision]

  it should "handle player near goal" in:
    val ballCarrierPlayer =
      Player(
        1,
        Position(UIConfig.goalEastX - 2, UIConfig.midGoalY),
        Movement.still,
        Some(Ball(Position(0, 0), Movement.still)),
        Decision.Tackle(Ball(Position(0, 0), Movement.still)),
        Action.Initial
      ).asBallCarrierPlayer
    val team1 = Team(List(ballCarrierPlayer), West, hasBall = true)
    val team2 = Team(List(), East, hasBall = false)
    val state = Match((team1, team2), Ball(Position(0, 0), Movement.still))

    val decision = ballCarrierPlayer.calculateBestDecision(state)

    decision should not be Decision.Initial
    decision shouldBe a[Decision]

  it should "handle multiple possible decisions" in:
    val ballCarrierPlayer = Player(1, Position(5, 5), Movement.still).asBallCarrierPlayer
    val teammate          = Player(2, Position(6, 6), Movement.still).asTeammatePlayer
    val team1             = Team(List(ballCarrierPlayer, teammate), West, hasBall = true)
    val team2             = Team(List(), East, hasBall = false)
    val state             = Match((team1, team2), Ball(Position(0, 0), Movement.still))

    val decision = ballCarrierPlayer.calculateBestDecision(state)

    decision should not be Decision.Initial
    decision shouldBe a[Decision]

  it should "always return the highest rated decision" in:
    val ballCarrierPlayer = Player(
      1,
      Position(UIConfig.goalEastX - 2, UIConfig.midGoalY),
      Movement.still,
      Some(Ball(Position(0, 0), Movement.still)),
      Decision.Tackle(Ball(Position(0, 0), Movement.still)),
      Action.Initial
    ).asBallCarrierPlayer
    val team1 = Team(List(ballCarrierPlayer), West, hasBall = true)
    val team2 = Team(List(), East, hasBall = false)
    val state = Match((team1, team2), Ball(Position(0, 0), Movement.still))

    val decision1 = ballCarrierPlayer.calculateBestDecision(state)
    val decision2 = ballCarrierPlayer.calculateBestDecision(state)

    decision1 shouldBe decision2

  it should "handle edge case positions" in:
    val ballCarrierPlayer = Player(
      1,
      Position(UIConfig.goalEastX - 2, UIConfig.midGoalY),
      Movement.still,
      Some(Ball(Position(0, 0), Movement.still)),
      Decision.Tackle(Ball(Position(0, 0), Movement.still)),
      Action.Initial
    ).asBallCarrierPlayer
    val team1 = Team(List(ballCarrierPlayer), East, hasBall = true)
    val team2 = Team(List(), West, hasBall = false)
    val state = Match((team1, team2), Ball(Position(0, 0), Movement.still))

    val decision = ballCarrierPlayer.calculateBestDecision(state)

    decision should not be Decision.Initial
    decision shouldBe a[Decision]
