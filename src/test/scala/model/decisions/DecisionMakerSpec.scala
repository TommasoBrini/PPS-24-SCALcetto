package model.decisions

import model.Match.*
import model.Match.Decision.*
import Side.*
import model.Space.*
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import model.decisions.DecisionMaker.*
import model.decisions.PlayerDecisionFactory.*
import model.decisions.DecisorPlayer.*

class DecisionMakerSpec extends AnyFlatSpec with Matchers:

  "DecisionMaker.decide" should "return a decision for ControlPlayer" in:
    val controlPlayer  = Player(1, Position(5, 5), Movement.still).asControlDecisionPlayer
    val teammatePlayer = Player(3, Position(6, 6), Movement.still).asTeammateDecisionPlayer
    val team1          = Team(List(controlPlayer, teammatePlayer), West, hasBall = true)
    val team2          = Team(List(), East, hasBall = false)
    val state          = Match((team1, team2), Ball(Position(0, 0), Movement.still))
    val markings       = Map[Player, Player]()

    val decision = controlPlayer.decide(state, markings)

    decision should not be Decision.Initial
    decision shouldBe a[Decision]

  it should "return a decision for OpponentPlayer" in:
    val opponentPlayer = Player(2, Position(10, 10), Movement.still).asOpponentDecisionPlayer
    val team1          = Team(List(), hasBall = true)
    val team2          = Team(List(opponentPlayer), hasBall = false)
    val state          = Match((team1, team2), Ball(Position(0, 0), Movement.still))
    val markings       = Map[Player, Player]()

    val decision = opponentPlayer.decide(state, markings)

    decision should not be Decision.Initial
    decision shouldBe a[Decision]

  it should "return a decision for TeammatePlayer" in:
    val teammatePlayer = Player(3, Position(6, 6), Movement.still).asTeammateDecisionPlayer
    val team1          = Team(List(teammatePlayer), hasBall = true)
    val team2          = Team(List(), hasBall = false)
    val state          = Match((team1, team2), Ball(Position(0, 0), Movement.still))
    val markings       = Map[Player, Player]()

    val decision = teammatePlayer.decide(state, markings)

    decision should not be Decision.Initial
    decision shouldBe a[Decision]

  it should "throw IllegalArgumentException for unknown player type" in:
    val unknownPlayer = Player(4, Position(7, 7), Movement.still) // Player base senza trait
    val team1         = Team(List(unknownPlayer), hasBall = true)
    val team2         = Team(List(), hasBall = false)
    val state         = Match((team1, team2), Ball(Position(0, 0), Movement.still))
    val markings      = Map[Player, Player]()

    an[IllegalArgumentException] should be thrownBy unknownPlayer.decide(state, markings)

  it should "handle markings for OpponentPlayer" in:
    val player1        = Player(2, Position(10, 10), Movement.still)
    val player2        = Player(1, Position(5, 5), Movement.still)
    val opponentPlayer = player1.asOpponentDecisionPlayer
    val targetPlayer   = player2.asControlDecisionPlayer
    val team1          = Team(List(targetPlayer), hasBall = true)
    val team2          = Team(List(opponentPlayer), hasBall = false)
    val state          = Match((team1, team2), Ball(Position(0, 0), Movement.still))
    val markings       = Map(player1 -> player2)

    val decision = opponentPlayer.decide(state, markings)

    decision should not be Decision.Initial
    decision shouldBe a[Decision]

  it should "handle empty markings for OpponentPlayer" in:
    val opponentPlayer = Player(2, Position(10, 10), Movement.still).asOpponentDecisionPlayer
    val team1          = Team(List(), hasBall = true)
    val team2          = Team(List(opponentPlayer), hasBall = false)
    val state          = Match((team1, team2), Ball(Position(0, 0), Movement.still))
    val markings       = Map[Player, Player]()

    val decision = opponentPlayer.decide(state, markings)

    decision should not be Decision.Initial
    decision shouldBe a[Decision]

  it should "work with different state configurations" in:
    val player1        = Player(1, Position(5, 5), Movement.still)
    val player2        = Player(2, Position(10, 10), Movement.still)
    val player3        = Player(3, Position(6, 6), Movement.still)
    val controlPlayer  = player1.asControlDecisionPlayer
    val opponentPlayer = player2.asOpponentDecisionPlayer
    val teammatePlayer = player3.asTeammateDecisionPlayer

    val team1    = Team(List(controlPlayer, teammatePlayer), hasBall = true)
    val team2    = Team(List(opponentPlayer), hasBall = false)
    val ball     = Ball(Position(15, 15), Movement(Direction(1, 1), 2))
    val state    = Match((team1, team2), ball)
    val markings = Map(player2 -> player1)

    val controlDecision  = controlPlayer.decide(state, markings)
    val opponentDecision = opponentPlayer.decide(state, markings)
    val teammateDecision = teammatePlayer.decide(state, markings)

    controlDecision should not be Decision.Initial
    opponentDecision should not be Decision.Initial
    teammateDecision should not be Decision.Initial

  it should "handle players with ball" in:
    val ball           = Ball(Position(5, 5), Movement.still)
    val controlPlayer  = Player(1, Position(5, 5), Movement.still, ball = Some(ball)).asControlDecisionPlayer
    val teammatePlayer = Player(3, Position(6, 6), Movement.still).asTeammateDecisionPlayer
    val team1          = Team(List(controlPlayer, teammatePlayer), hasBall = true)
    val team2          = Team(List(), hasBall = false)
    val state          = Match((team1, team2), ball)
    val markings       = Map[Player, Player]()
    val decision       = controlPlayer.decide(state, markings)

    decision should not be Decision.Initial
    decision shouldBe a[Decision]

  it should "handle players near ball" in:
    val ball           = Ball(Position(6, 6), Movement.still)
    val opponentPlayer = Player(2, Position(5, 5), Movement.still).asOpponentDecisionPlayer
    val team1          = Team(List(), hasBall = true)
    val team2          = Team(List(opponentPlayer), hasBall = false)
    val state          = Match((team1, team2), ball)
    val markings       = Map[Player, Player]()

    val decision = opponentPlayer.decide(state, markings)

    decision should not be Decision.Initial
    decision shouldBe a[Decision]
