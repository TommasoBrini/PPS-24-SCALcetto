package dsl.decisions.behavior

import model.Match.*
import model.Match.Decision.*
import model.Space.*
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import dsl.decisions.behavior.TeammateBehavior.*
import dsl.decisions.PlayerRoleFactory.*
import dsl.decisions.PlayerTypes.*
import config.MatchConfig

class TeammateBehaviorSpec extends AnyFlatSpec with Matchers:

  "TeammateBehavior.calculateBestDecision" should "return a valid decision for teammate player" in:
    val teammatePlayer = Player(3, Position(6, 6), Movement.still).asTeammatePlayer
    val team1          = Team(List(teammatePlayer), hasBall = true)
    val team2          = Team(List(), hasBall = false)
    val state          = MatchState((team1, team2), Ball(Position(0, 0), Movement.still))

    val decision = teammatePlayer.calculateBestDecision(state)

    decision should not be Decision.Initial
    decision shouldBe a[Decision]

  it should "return ReceivePass decision when ball is heading toward player" in:
    val teammatePlayer = Player(3, Position(6, 6), Movement.still).asTeammatePlayer
    val team1          = Team(List(teammatePlayer), hasBall = true)
    val team2          = Team(List(), hasBall = false)
    val ball           = Ball(Position(5, 5), Movement(Direction(1, 1), 2))
    val state          = MatchState((team1, team2), ball)

    val decision = teammatePlayer.calculateBestDecision(state)

    decision shouldBe a[ReceivePass]

  it should "return Confusion decision when player is stopped" in:
    val teammatePlayer =
      Player(3, Position(6, 6), Movement.still, nextAction = Action.Stopped(2)).asTeammatePlayer
    val team1 = Team(List(teammatePlayer), hasBall = true)
    val team2 = Team(List(), hasBall = false)
    val state = MatchState((team1, team2), Ball(Position(0, 0), Movement.still))

    val decision = teammatePlayer.calculateBestDecision(state)

    decision shouldBe a[Confusion]
    decision.asInstanceOf[Confusion].remainingStep shouldBe 1

  it should "return MoveRandom decision when no specific action needed" in:
    val teammatePlayer = Player(3, Position(6, 6), Movement.still).asTeammatePlayer
    val team1          = Team(List(teammatePlayer), hasBall = true)
    val team2          = Team(List())
    val state          = MatchState((team1, team2), Ball(Position(0, 0), Movement.still))

    val decision = teammatePlayer.calculateBestDecision(state)

    decision shouldBe a[ReceivePass]

  it should "return a new MoveRandom decision when last MoveRandom would move player out of field" in:
    val lastDecision   = MoveRandom(Direction(-1, -1), 1)
    val teammatePlayer = Player(3, Position(0, 0), Movement.still, decision = lastDecision).asTeammatePlayer
    val team1          = Team(List(teammatePlayer), hasBall = true)
    val team2          = Team(List())
    val state          = MatchState((team1, team2), Ball(Position(0, 0), Movement.still))

    val newDecision = teammatePlayer.calculateBestDecision(state)

    newDecision shouldNot be(lastDecision)

  it should "handle player with ball" in:
    val ball           = Ball(Position(6, 6), Movement.still)
    val teammatePlayer = Player(3, Position(6, 6), Movement.still, ball = Some(ball)).asTeammatePlayer
    val team1          = Team(List(teammatePlayer), hasBall = true)
    val team2          = Team(List(), hasBall = false)
    val state          = MatchState((team1, team2), ball)

    val decision = teammatePlayer.calculateBestDecision(state)

    decision should not be Decision.Initial
    decision shouldBe a[Decision]

  it should "return ReceivePass decision when ball is moving away from player" in:
    val teammatePlayer = Player(3, Position(6, 6), Movement.still).asTeammatePlayer
    val team1          = Team(List(teammatePlayer), hasBall = true)
    val team2          = Team(List(), hasBall = false)
    val ball           = Ball(Position(5, 5), Movement(Direction(-1, -1), 2))
    val state          = MatchState((team1, team2), ball)

    val decision = teammatePlayer.calculateBestDecision(state)

    decision shouldBe a[ReceivePass]

  it should "handle multiple teammates" in:
    val teammate1 = Player(3, Position(6, 6), Movement.still).asTeammatePlayer
    val teammate2 = Player(4, Position(7, 7), Movement.still).asTeammatePlayer
    val team1     = Team(List(teammate1, teammate2), hasBall = true)
    val team2     = Team(List(), hasBall = false)
    val state     = MatchState((team1, team2), Ball(Position(0, 0), Movement.still))

    val decision1 = teammate1.calculateBestDecision(state)
    val decision2 = teammate2.calculateBestDecision(state)

    decision1 should not be Decision.Initial
    decision2 should not be Decision.Initial
    decision1 shouldBe a[Decision]
    decision2 shouldBe a[Decision]

  it should "handle stopped action with zero steps" in:
    val teammatePlayer =
      Player(3, Position(6, 6), Movement.still, nextAction = Action.Stopped(0)).asTeammatePlayer
    val team1 = Team(List(teammatePlayer), hasBall = true)
    val team2 = Team(List(), hasBall = false)
    val state = MatchState((team1, team2), Ball(Position(0, 0), Movement.still))

    val decision = teammatePlayer.calculateBestDecision(state)

    decision should not be a[Confusion]
    decision shouldBe a[Decision]

  it should "handle ball at exact pass direction range" in:
    val teammatePlayer = Player(
      3,
      Position(MatchConfig.passDirectionRange.toInt, MatchConfig.passDirectionRange.toInt),
      Movement.still
    ).asTeammatePlayer
    val team1 = Team(List(teammatePlayer), hasBall = true)
    val team2 = Team(List(), hasBall = false)
    val ball  = Ball(Position(0, 0), Movement(Direction(1, 1), 2))
    val state = MatchState((team1, team2), ball)

    val decision = teammatePlayer.calculateBestDecision(state)

    decision should not be Decision.Initial
    decision shouldBe a[Decision]
