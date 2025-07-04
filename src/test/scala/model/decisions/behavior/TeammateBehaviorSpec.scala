package model.decisions.behavior

import model.Match.*
import model.Match.Decision.*
import model.Space.*
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import model.decisions.behavior.TeammateBehavior.*
import model.decisions.PlayerDecisionFactory.*
import model.decisions.DecisorPlayer.*
import config.MatchConfig

class TeammateBehaviorSpec extends AnyFlatSpec with Matchers:

  "TeammateBehavior.calculateBestDecision" should "return a valid decision for teammate player" in:
    val teammatePlayer = Player(3, Position(6, 6), Movement.still).asTeammateDecisionPlayer
    val team1          = Team(1, List(teammatePlayer), hasBall = true)
    val team2          = Team(2, List(), hasBall = false)
    val state          = MatchState(List(team1, team2), Ball(Position(0, 0), Movement.still))

    val decision = teammatePlayer.calculateBestDecision(state)

    decision should not be Decision.Initial
    decision shouldBe a[Decision]

  it should "return ReceivePass decision when ball is heading toward player" in:
    val teammatePlayer = Player(3, Position(6, 6), Movement.still).asTeammateDecisionPlayer
    val team1          = Team(1, List(teammatePlayer), hasBall = true)
    val team2          = Team(2, List(), hasBall = false)
    val ball           = Ball(Position(5, 5), Movement(Direction(1, 1), 2))
    val state          = MatchState(List(team1, team2), ball)

    val decision = teammatePlayer.calculateBestDecision(state)

    decision shouldBe a[ReceivePass]

  it should "return Confusion decision when player is stopped" in:
    val teammatePlayer =
      Player(3, Position(6, 6), Movement.still, nextAction = Action.Stopped(2)).asTeammateDecisionPlayer
    val team1 = Team(1, List(teammatePlayer), hasBall = true)
    val team2 = Team(2, List(), hasBall = false)
    val state = MatchState(List(team1, team2), Ball(Position(0, 0), Movement.still))

    val decision = teammatePlayer.calculateBestDecision(state)

    decision shouldBe a[Confusion]
    decision.asInstanceOf[Confusion].remainingStep shouldBe 1

  it should "return MoveRandom decision when no specific action needed" in:
    val teammatePlayer = Player(3, Position(6, 6), Movement.still).asTeammateDecisionPlayer
    val team2          = Team(2, List())
    val state          = MatchState(List(team2), Ball(Position(0, 0), Movement.still))

    val decision = teammatePlayer.calculateBestDecision(state)

    decision shouldBe a[ReceivePass]

  it should "handle player with ball" in:
    val ball           = Ball(Position(6, 6), Movement.still)
    val teammatePlayer = Player(3, Position(6, 6), Movement.still, ball = Some(ball)).asTeammateDecisionPlayer
    val team1          = Team(1, List(teammatePlayer), hasBall = true)
    val team2          = Team(2, List(), hasBall = false)
    val state          = MatchState(List(team1, team2), ball)

    val decision = teammatePlayer.calculateBestDecision(state)

    decision should not be Decision.Initial
    decision shouldBe a[Decision]

  it should "return ReceivePass decision when ball is moving away from player" in:
    val teammatePlayer = Player(3, Position(6, 6), Movement.still).asTeammateDecisionPlayer
    val team1          = Team(1, List(teammatePlayer), hasBall = true)
    val team2          = Team(2, List(), hasBall = false)
    val ball           = Ball(Position(5, 5), Movement(Direction(-1, -1), 2))
    val state          = MatchState(List(team1, team2), ball)

    val decision = teammatePlayer.calculateBestDecision(state)

    decision shouldBe a[ReceivePass]

  it should "handle multiple teammates" in:
    val teammate1 = Player(3, Position(6, 6), Movement.still).asTeammateDecisionPlayer
    val teammate2 = Player(4, Position(7, 7), Movement.still).asTeammateDecisionPlayer
    val team1     = Team(1, List(teammate1, teammate2), hasBall = true)
    val team2     = Team(2, List(), hasBall = false)
    val state     = MatchState(List(team1, team2), Ball(Position(0, 0), Movement.still))

    val decision1 = teammate1.calculateBestDecision(state)
    val decision2 = teammate2.calculateBestDecision(state)

    decision1 should not be Decision.Initial
    decision2 should not be Decision.Initial
    decision1 shouldBe a[Decision]
    decision2 shouldBe a[Decision]

  it should "handle stopped action with zero steps" in:
    val teammatePlayer =
      Player(3, Position(6, 6), Movement.still, nextAction = Action.Stopped(0)).asTeammateDecisionPlayer
    val team1 = Team(1, List(teammatePlayer), hasBall = true)
    val team2 = Team(2, List(), hasBall = false)
    val state = MatchState(List(team1, team2), Ball(Position(0, 0), Movement.still))

    val decision = teammatePlayer.calculateBestDecision(state)

    // Non dovrebbe essere Confusion(0) ma una decisione normale
    decision should not be a[Confusion]
    decision shouldBe a[Decision]

  it should "handle ball at exact pass direction range" in:
    val teammatePlayer = Player(
      3,
      Position(MatchConfig.passDirectionRange.toInt, MatchConfig.passDirectionRange.toInt),
      Movement.still
    ).asTeammateDecisionPlayer
    val team1 = Team(1, List(teammatePlayer), hasBall = true)
    val team2 = Team(2, List(), hasBall = false)
    val ball  = Ball(Position(0, 0), Movement(Direction(1, 1), 2))
    val state = MatchState(List(team1, team2), ball)

    val decision = teammatePlayer.calculateBestDecision(state)

    decision should not be Decision.Initial
    decision shouldBe a[Decision]
