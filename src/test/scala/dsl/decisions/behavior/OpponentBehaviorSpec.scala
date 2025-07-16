package dsl.decisions.behavior

import model.Match.*
import model.Match.Decision.*
import model.Space.*
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import dsl.decisions.behavior.OpponentBehavior.*
import dsl.decisions.PlayerRoleFactory.*
import dsl.decisions.PlayerTypes.*
import config.MatchConfig
import Side.*

class OpponentBehaviorSpec extends AnyFlatSpec with Matchers:

  "OpponentBehavior.calculateBestDecision" should "return Confusion when player is stopped" in:
    val opponentPlayer =
      Player(2, Position(10, 10), Movement.still, nextAction = Action.Stopped(3)).asOpponentPlayer
    val team1 = Team(List(), hasBall = true)
    val team2 = Team(List(opponentPlayer), hasBall = false)
    val state = MatchState((team1, team2), Ball(Position(0, 0), Movement.still))

    val decision = opponentPlayer.calculateBestDecision(state, None)

    decision shouldBe a[Confusion]
    decision.asInstanceOf[Confusion].remainingStep shouldBe 2

  it should "return Tackle when close to ball carrier" in:
    val ballCarrier    = Player(1, Position(5, 5), Movement.still, ball = Some(Ball(Position(5, 5), Movement.still)))
    val opponentPlayer = Player(2, Position(6, 6), Movement.still).asOpponentPlayer
    val team1          = Team(List(ballCarrier), West, hasBall = true)
    val team2          = Team(List(opponentPlayer), East, hasBall = false)
    val state          = MatchState((team1, team2), Ball(Position(5, 5), Movement.still))

    val decision = opponentPlayer.calculateBestDecision(state, None)

    decision shouldBe a[Tackle]

  it should "return Intercept when ball is in intercept range and no carrier" in:
    val opponentPlayer = Player(2, Position(6, 6), Movement.still).asOpponentPlayer
    val team1          = Team(List(), East, hasBall = true)
    val team2          = Team(List(opponentPlayer), West, hasBall = false)
    val state          = MatchState((team1, team2), Ball(Position(5, 5), Movement.still))

    val decision = opponentPlayer.calculateBestDecision(state, None)

    decision shouldBe a[Intercept]

  it should "return MoveToBall when ball is in proximity range but not in intercept range" in:
    val opponentPlayer = Player(2, Position(50, 50), Movement.still).asOpponentPlayer
    val team1          = Team(List(), East, hasBall = true)
    val team2          = Team(List(opponentPlayer), West, hasBall = false)
    val state          = MatchState((team1, team2), Ball(Position(20, 20), Movement.still))

    val decision = opponentPlayer.calculateBestDecision(state, None)

    decision shouldBe a[MoveToBall]

  it should "return Mark when target is provided and ball is far" in:
    val targetPlayer   = Player(1, Position(5, 5), Movement.still)
    val opponentPlayer = Player(2, Position(100, 100), Movement.still).asOpponentPlayer
    val team1          = Team(List(targetPlayer), side = East, hasBall = true)
    val team2          = Team(List(opponentPlayer), side = West, hasBall = false)
    val state          = MatchState((team1, team2), Ball(Position(0, 0), Movement.still))

    val decision = opponentPlayer.calculateBestDecision(state, Some(targetPlayer))

    decision shouldBe a[Mark]
    val markDecision = decision.asInstanceOf[Mark]
    markDecision.target shouldBe targetPlayer
    markDecision.teamSide shouldBe West

  it should "return MoveToBall when no target and ball is far" in:
    val opponentPlayer = Player(2, Position(100, 100), Movement.still).asOpponentPlayer
    val team1          = Team(List(), East, hasBall = true)
    val team2          = Team(List(opponentPlayer), West, hasBall = false)
    val state          = MatchState((team1, team2), Ball(Position(0, 0), Movement.still))

    val decision = opponentPlayer.calculateBestDecision(state, None)

    decision shouldBe a[MoveToBall]

  it should "prioritize Tackle over Intercept when both conditions are met" in:
    val ballCarrier    = Player(1, Position(5, 5), Movement.still, ball = Some(Ball(Position(5, 5), Movement.still)))
    val opponentPlayer = Player(2, Position(6, 6), Movement.still).asOpponentPlayer
    val team1          = Team(List(ballCarrier), East, hasBall = true)
    val team2          = Team(List(opponentPlayer), West, hasBall = false)
    val state          = MatchState((team1, team2), Ball(Position(5, 5), Movement.still))

    val decision = opponentPlayer.calculateBestDecision(state, None)

    decision shouldBe a[Tackle]

  it should "prioritize Tackle over Mark when close to ball carrier" in:
    val ballCarrier    = Player(1, Position(5, 5), Movement.still, ball = Some(Ball(Position(5, 5), Movement.still)))
    val targetPlayer   = Player(3, Position(10, 10), Movement.still)
    val opponentPlayer = Player(2, Position(6, 6), Movement.still).asOpponentPlayer
    val team1          = Team(List(ballCarrier, targetPlayer), East, hasBall = true)
    val team2          = Team(List(opponentPlayer), West, hasBall = false)
    val state          = MatchState((team1, team2), Ball(Position(5, 5), Movement.still))

    val decision = opponentPlayer.calculateBestDecision(state, Some(targetPlayer))

    decision shouldBe a[Tackle]

  it should "prioritize Intercept over Mark when ball is in intercept range" in:
    val targetPlayer   = Player(1, Position(10, 10), Movement.still)
    val opponentPlayer = Player(2, Position(6, 6), Movement.still).asOpponentPlayer
    val team1          = Team(List(targetPlayer), West, hasBall = true)
    val team2          = Team(List(opponentPlayer), East, hasBall = false)
    val state          = MatchState((team1, team2), Ball(Position(5, 5), Movement.still))

    val decision = opponentPlayer.calculateBestDecision(state, Some(targetPlayer))

    decision shouldBe a[Intercept]

  it should "handle edge case when player is at exact proximity range" in:
    val opponentPlayer = Player(2, Position(MatchConfig.proximityRange, 0), Movement.still).asOpponentPlayer
    val team1          = Team(List(), West, hasBall = true)
    val team2          = Team(List(opponentPlayer), East, hasBall = false)
    val state          = MatchState((team1, team2), Ball(Position(0, 0), Movement.still))

    val decision = opponentPlayer.calculateBestDecision(state, None)

    decision shouldBe a[MoveToBall]

  it should "handle stopped action with zero steps" in:
    val opponentPlayer =
      Player(2, Position(10, 10), Movement.still, nextAction = Action.Stopped(0)).asOpponentPlayer
    val team1 = Team(List(), West, hasBall = true)
    val team2 = Team(List(opponentPlayer), East, hasBall = false)
    val state = MatchState((team1, team2), Ball(Position(0, 0), Movement.still))

    val decision = opponentPlayer.calculateBestDecision(state, None)

    decision should not be a[Confusion]
    decision shouldBe a[Decision]

  it should "handle multiple opponents correctly" in:
    val opponent1 = Player(2, Position(6, 6), Movement.still).asOpponentPlayer
    val opponent2 = Player(3, Position(100, 100), Movement.still).asOpponentPlayer
    val team1     = Team(List(), East, hasBall = true)
    val team2     = Team(List(opponent1, opponent2), West, hasBall = false)
    val state     = MatchState((team1, team2), Ball(Position(5, 5), Movement.still))

    val decision1 = opponent1.calculateBestDecision(state, None)
    val decision2 = opponent2.calculateBestDecision(state, None)

    decision1 shouldBe a[Intercept]
    decision2 shouldBe a[MoveToBall]

  it should "handle different team configurations" in:
    val targetPlayer   = Player(1, Position(5, 5), Movement.still)
    val opponentPlayer = Player(12, Position(100, 100), Movement.still).asOpponentPlayer
    val team1          = Team(List(targetPlayer), East, hasBall = true)
    val team2          = Team(List(opponentPlayer), West, hasBall = false)
    val state          = MatchState((team1, team2), Ball(Position(0, 0), Movement.still))

    val decision = opponentPlayer.calculateBestDecision(state, Some(targetPlayer))

    decision shouldBe a[Mark]
    val markDecision = decision.asInstanceOf[Mark]
    markDecision.teamSide shouldBe West

  it should "handle ball carrier in different positions" in:
    val ballCarrier = Player(1, Position(50, 50), Movement.still, ball = Some(Ball(Position(50, 50), Movement.still)))
    val opponentPlayer = Player(2, Position(51, 51), Movement.still).asOpponentPlayer
    val team1          = Team(List(ballCarrier), West, hasBall = true)
    val team2          = Team(List(opponentPlayer), East, hasBall = false)
    val state          = MatchState((team1, team2), Ball(Position(50, 50), Movement.still))

    val decision = opponentPlayer.calculateBestDecision(state, None)

    decision shouldBe a[Tackle]

  it should "handle ball without carrier in different positions" in:
    val opponentPlayer = Player(2, Position(51, 51), Movement.still).asOpponentPlayer
    val team1          = Team(List(), East, hasBall = true)
    val team2          = Team(List(opponentPlayer), West, hasBall = false)
    val state          = MatchState((team1, team2), Ball(Position(50, 50), Movement.still))

    val decision = opponentPlayer.calculateBestDecision(state, None)

    decision shouldBe a[Intercept]
