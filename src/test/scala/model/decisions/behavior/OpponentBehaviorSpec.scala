package model.decisions.behavior

import model.Match.*
import model.Match.Decision.*
import model.Space.*
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import model.decisions.behavior.OpponentBehavior.*
import model.decisions.PlayerDecisionFactory.*
import model.decisions.DecisorPlayer.*
import config.MatchConfig

class OpponentBehaviorSpec extends AnyFlatSpec with Matchers:

  "OpponentBehavior.calculateBestDecision" should "return a valid decision for opponent player" in:
    val opponentPlayer = Player(2, Position(10, 10), Movement.still).asOpponentDecisionPlayer
    val team1          = Team(1, List(), hasBall = true)
    val team2          = Team(2, List(opponentPlayer), hasBall = false)
    val state          = MatchState(List(team1, team2), Ball(Position(0, 0), Movement.still))

    val decision = opponentPlayer.calculateBestDecision(state, None)

    decision should not be Decision.Initial
    decision shouldBe a[Decision]

  it should "return Tackle decision when close to ball carrier" in:
    val ballCarrier    = Player(1, Position(5, 5), Movement.still, ball = Some(Ball(Position(5, 5), Movement.still)))
    val opponentPlayer = Player(2, Position(6, 6), Movement.still).asOpponentDecisionPlayer
    val team1          = Team(1, List(ballCarrier), hasBall = true)
    val team2          = Team(2, List(opponentPlayer), hasBall = false)
    val state          = MatchState(List(team1, team2), Ball(Position(5, 5), Movement.still))

    val decision = opponentPlayer.calculateBestDecision(state, None)

    decision shouldBe a[Tackle]

  it should "return Intercept decision when ball is nearby but no carrier" in:
    val opponentPlayer = Player(2, Position(6, 6), Movement.still).asOpponentDecisionPlayer
    val team1          = Team(1, List(), hasBall = true)
    val team2          = Team(2, List(opponentPlayer), hasBall = false)
    val state          = MatchState(List(team1, team2), Ball(Position(5, 5), Movement.still))

    val decision = opponentPlayer.calculateBestDecision(state, None)

    decision shouldBe a[Intercept]

  it should "return Mark decision when target is provided" in:
    val targetPlayer   = Player(1, Position(5, 5), Movement.still)
    val opponentPlayer = Player(2, Position(10, 10), Movement.still).asOpponentDecisionPlayer
    val team1          = Team(1, List(targetPlayer), hasBall = true)
    val team2          = Team(2, List(opponentPlayer), hasBall = false)
    val state          = MatchState(List(team1, team2), Ball(Position(0, 0), Movement.still))

    val decision = opponentPlayer.calculateBestDecision(state, Some(targetPlayer))

    decision shouldBe a[Mark]

  it should "return MoveToBall decision when no specific target and ball is far" in:
    val opponentPlayer = Player(2, Position(20, 20), Movement.still).asOpponentDecisionPlayer
    val team1          = Team(1, List(), hasBall = true)
    val team2          = Team(2, List(opponentPlayer), hasBall = false)
    val state          = MatchState(List(team1, team2), Ball(Position(0, 0), Movement.still))

    val decision = opponentPlayer.calculateBestDecision(state, None)

    decision shouldBe a[MoveToBall]

  it should "return Confusion decision when player is stopped" in:
    val opponentPlayer =
      Player(2, Position(10, 10), Movement.still, nextAction = Action.Stopped(2)).asOpponentDecisionPlayer
    val team1 = Team(1, List(), hasBall = true)
    val team2 = Team(2, List(opponentPlayer), hasBall = false)
    val state = MatchState(List(team1, team2), Ball(Position(0, 0), Movement.still))

    val decision = opponentPlayer.calculateBestDecision(state, None)

    decision shouldBe a[Confusion]
    decision.asInstanceOf[Confusion].remainingStep shouldBe 1

  it should "handle player with ball" in:
    val ball           = Ball(Position(10, 10), Movement.still)
    val opponentPlayer = Player(2, Position(10, 10), Movement.still, ball = Some(ball)).asOpponentDecisionPlayer
    val team1          = Team(1, List(), hasBall = true)
    val team2          = Team(2, List(opponentPlayer), hasBall = false)
    val state          = MatchState(List(team1, team2), ball)

    val decision = opponentPlayer.calculateBestDecision(state, None)

    decision should not be Decision.Initial
    decision shouldBe a[Decision]

  it should "handle edge case distances" in:
    val opponentPlayer =
      Player(2, Position(MatchConfig.tackleRange, MatchConfig.tackleRange), Movement.still).asOpponentDecisionPlayer
    val ballCarrier = Player(1, Position(0, 0), Movement.still, ball = Some(Ball(Position(0, 0), Movement.still)))
    val team1       = Team(1, List(ballCarrier), hasBall = true)
    val team2       = Team(2, List(opponentPlayer), hasBall = false)
    val state       = MatchState(List(team1, team2), Ball(Position(0, 0), Movement.still))

    val decision = opponentPlayer.calculateBestDecision(state, None)

    decision should not be Decision.Initial
    decision shouldBe a[Decision]

  it should "handle multiple opponents" in:
    val opponent1 = Player(2, Position(6, 6), Movement.still).asOpponentDecisionPlayer
    val opponent2 = Player(3, Position(7, 7), Movement.still).asOpponentDecisionPlayer
    val team1     = Team(1, List(), hasBall = true)
    val team2     = Team(2, List(opponent1, opponent2), hasBall = false)
    val state     = MatchState(List(team1, team2), Ball(Position(5, 5), Movement.still))

    val decision1 = opponent1.calculateBestDecision(state, None)
    val decision2 = opponent2.calculateBestDecision(state, None)

    decision1 should not be Decision.Initial
    decision2 should not be Decision.Initial
    decision1 shouldBe a[Decision]
    decision2 shouldBe a[Decision]

  it should "handle different marking targets" in:
    val target1        = Player(1, Position(5, 5), Movement.still)
    val target2        = Player(4, Position(6, 6), Movement.still)
    val opponentPlayer = Player(2, Position(10, 10), Movement.still).asOpponentDecisionPlayer
    val team1          = Team(1, List(target1, target2), hasBall = true)
    val team2          = Team(2, List(opponentPlayer), hasBall = false)
    val state          = MatchState(List(team1, team2), Ball(Position(0, 0), Movement.still))

    val decision1 = opponentPlayer.calculateBestDecision(state, Some(target1))
    val decision2 = opponentPlayer.calculateBestDecision(state, Some(target2))

    decision1 shouldBe a[Mark]
    decision2 shouldBe a[Mark]
    decision1.asInstanceOf[Mark].target shouldBe target1
    decision2.asInstanceOf[Mark].target shouldBe target2

  it should "handle stopped action with zero steps" in:
    val opponentPlayer =
      Player(2, Position(10, 10), Movement.still, nextAction = Action.Stopped(0)).asOpponentDecisionPlayer
    val team1 = Team(1, List(), hasBall = true)
    val team2 = Team(2, List(opponentPlayer), hasBall = false)
    val state = MatchState(List(team1, team2), Ball(Position(0, 0), Movement.still))

    val decision = opponentPlayer.calculateBestDecision(state, None)

    // Non dovrebbe essere Confusion(0) ma una decisione normale
    decision should not be a[Confusion]
    decision shouldBe a[Decision]
