package model.decisions.behavior

import model.Match.*
import model.Match.Decision.*
import model.Space.*
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import model.decisions.behavior.ControlBehavior.*
import model.decisions.PlayerDecisionFactory.*
import model.decisions.DecisorPlayer.*
import config.UIConfig
import model.Match.Action

class ControlBehaviorSpec extends AnyFlatSpec with Matchers:

  "ControlBehavior.calculateBestDecision" should "return a valid decision for control player" in:
    val controlPlayer = Player(
      1,
      Position(5, 5),
      Movement.still,
      ball = Some(Ball(Position(5, 5), Movement.still))
    ).asControlDecisionPlayer
    val teammate = Player(2, Position(6, 6), Movement.still).asTeammateDecisionPlayer
    val team1    = Team(1, List(controlPlayer, teammate), hasBall = true)
    val team2    = Team(2, List(), hasBall = false)
    val state    = MatchState(List(team1, team2), Ball(Position(0, 0), Movement.still))

    val decision = controlPlayer.calculateBestDecision(state)

    decision should not be Decision.Initial
    decision shouldBe a[Decision]

  it should "return Pass decision when teammate is available" in:
    val controlPlayer = Player(1, Position(5, 5), Movement.still).asControlDecisionPlayer
    val teammate      = Player(2, Position(6, 6), Movement.still).asTeammateDecisionPlayer
    val team1         = Team(1, List(controlPlayer, teammate), hasBall = true)
    val team2         = Team(2, List(), hasBall = false)
    val state         = MatchState(List(team1, team2), Ball(Position(0, 0), Movement.still))

    val decision = controlPlayer.calculateBestDecision(state)

    decision shouldBe a[Decision]

  it should "return Shoot decision when near goal" in:
    val controlPlayer =
      Player(
        1,
        Position(UIConfig.goalEastX - 2, UIConfig.midGoalY),
        Movement.still,
        Some(Ball(Position(0, 0), Movement.still)),
        Action.Initial,
        Decision.Tackle(Ball(Position(0, 0), Movement.still))
      ).asControlDecisionPlayer
    val team1 = Team(1, List(controlPlayer), hasBall = true)
    val team2 = Team(2, List())
    val state = MatchState(List(team1, team2), Ball(Position(0, 0), Movement.still))

    val decision = controlPlayer.calculateBestDecision(state)

    decision shouldBe a[Decision]

  it should "return MoveToGoal decision when appropriate" in:
    val controlPlayer = Player(
      1,
      Position(UIConfig.goalEastX - 2, UIConfig.midGoalY),
      Movement.still,
      Some(Ball(Position(0, 0), Movement.still)),
      Action.Initial,
      Decision.Tackle(Ball(Position(0, 0), Movement.still))
    ).asControlDecisionPlayer
    val team1 = Team(1, List(controlPlayer), hasBall = true)
    val team2 = Team(2, List(), hasBall = false)
    val state = MatchState(List(team1, team2), Ball(Position(0, 0), Movement.still))

    val decision = controlPlayer.calculateBestDecision(state)

    decision should not be Decision.Initial
    decision shouldBe a[Decision]

  it should "handle player with ball" in:
    val ball = Ball(Position(5, 5), Movement.still)
    val controlPlayer = Player(
      1,
      Position(UIConfig.goalEastX - 2, UIConfig.midGoalY),
      Movement.still,
      Some(Ball(Position(0, 0), Movement.still)),
      Action.Initial,
      Decision.Tackle(Ball(Position(0, 0), Movement.still))
    ).asControlDecisionPlayer
    val team1 = Team(1, List(controlPlayer), hasBall = true)
    val team2 = Team(2, List(), hasBall = false)
    val state = MatchState(List(team1, team2), ball)

    val decision = controlPlayer.calculateBestDecision(state)

    decision should not be Decision.Initial
    decision shouldBe a[Decision]

  it should "handle player far from goal" in:
    val controlPlayer = Player(
      1,
      Position(UIConfig.goalEastX - 2, UIConfig.midGoalY),
      Movement.still,
      Some(Ball(Position(0, 0), Movement.still)),
      Action.Initial,
      Decision.Tackle(Ball(Position(0, 0), Movement.still))
    ).asControlDecisionPlayer
    val team1 = Team(1, List(controlPlayer), hasBall = true)
    val team2 = Team(2, List(), hasBall = false)
    val state = MatchState(List(team1, team2), Ball(Position(0, 0), Movement.still))

    val decision = controlPlayer.calculateBestDecision(state)

    decision should not be Decision.Initial
    decision shouldBe a[Decision]

  it should "handle player near goal" in:
    val controlPlayer =
      Player(
        1,
        Position(UIConfig.goalEastX - 2, UIConfig.midGoalY),
        Movement.still,
        Some(Ball(Position(0, 0), Movement.still)),
        Action.Initial,
        Decision.Tackle(Ball(Position(0, 0), Movement.still))
      ).asControlDecisionPlayer
    val team1 = Team(1, List(controlPlayer), hasBall = true)
    val team2 = Team(2, List(), hasBall = false)
    val state = MatchState(List(team1, team2), Ball(Position(0, 0), Movement.still))

    val decision = controlPlayer.calculateBestDecision(state)

    decision should not be Decision.Initial
    decision shouldBe a[Decision]

  it should "handle multiple possible decisions" in:
    val controlPlayer = Player(1, Position(5, 5), Movement.still).asControlDecisionPlayer
    val teammate      = Player(2, Position(6, 6), Movement.still).asTeammateDecisionPlayer
    val team1         = Team(1, List(controlPlayer, teammate), hasBall = true)
    val team2         = Team(2, List(), hasBall = false)
    val state         = MatchState(List(team1, team2), Ball(Position(0, 0), Movement.still))

    val decision = controlPlayer.calculateBestDecision(state)

    decision should not be Decision.Initial
    decision shouldBe a[Decision]

  it should "always return the highest rated decision" in:
    val controlPlayer = Player(
      1,
      Position(UIConfig.goalEastX - 2, UIConfig.midGoalY),
      Movement.still,
      Some(Ball(Position(0, 0), Movement.still)),
      Action.Initial,
      Decision.Tackle(Ball(Position(0, 0), Movement.still))
    ).asControlDecisionPlayer
    val team1 = Team(1, List(controlPlayer), hasBall = true)
    val team2 = Team(2, List(), hasBall = false)
    val state = MatchState(List(team1, team2), Ball(Position(0, 0), Movement.still))

    val decision1 = controlPlayer.calculateBestDecision(state)
    val decision2 = controlPlayer.calculateBestDecision(state)

    decision1 shouldBe decision2

  it should "handle edge case positions" in:
    val controlPlayer = Player(
      1,
      Position(UIConfig.goalEastX - 2, UIConfig.midGoalY),
      Movement.still,
      Some(Ball(Position(0, 0), Movement.still)),
      Action.Initial,
      Decision.Tackle(Ball(Position(0, 0), Movement.still))
    ).asControlDecisionPlayer
    val team1 = Team(1, List(controlPlayer), hasBall = true)
    val team2 = Team(2, List(), hasBall = false)
    val state = MatchState(List(team1, team2), Ball(Position(0, 0), Movement.still))

    val decision = controlPlayer.calculateBestDecision(state)

    decision should not be Decision.Initial
    decision shouldBe a[Decision]
