package dsl.decisions

import model.Match.*
import Side.*
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import dsl.decisions.DecisionMaker.*
import dsl.decisions.PlayerRoleFactory.*
import dsl.creation.build.PlayerBuilder
import dsl.creation.CreationSyntax.*
import Decision.Run
import config.MatchConfig

class DecisionMakerSpec extends AnyFlatSpec with Matchers:

  "DecisionMaker.decide" should "return a decision for carrier player" in:
    val carrierPlayer =
      PlayerBuilder(1).at(5, 5).decidedTo(Run(Direction(1, 0), MatchConfig.runSteps)).build().asBallCarrierPlayer
    val teammatePlayer = PlayerBuilder(3).at(6, 6).build().asTeammatePlayer
    val state = newMatch(Score.init()):
      team(West) withBall:
        player(1) at (5, 5) ownsBall true
      team(East):
        player(3) at (6, 6)
      ball at (0, 0)
    val markings = Map[Player, Player]()

    val decision = carrierPlayer.decide(state, markings)

    decision should not be Decision.Initial
    decision shouldBe a[Decision]

  it should "return a decision for OpponentPlayer" in:
    val opponentPlayer = PlayerBuilder(2).at(10, 10).build().asOpponentPlayer
    val state = newMatch(Score.init()):
      team(West) withBall:
        player(2) at (10, 10)
      team(East):
        player(3) at (6, 6)
      ball at (0, 0)
    val markings = Map[Player, Player]()

    val decision = opponentPlayer.decide(state, markings)

    decision should not be Decision.Initial
    decision shouldBe a[Decision]

  it should "return a decision for TeammatePlayer" in:
    val teammatePlayer = PlayerBuilder(3).at(6, 6).build().asTeammatePlayer
    val state = newMatch(Score.init()):
      team(West) withBall:
        player(3) at (6, 6)
      team(East):
        player(2) at (10, 10)
      ball at (0, 0)
    val markings = Map[Player, Player]()

    val decision = teammatePlayer.decide(state, markings)

    decision should not be Decision.Initial
    decision shouldBe a[Decision]

  it should "throw IllegalArgumentException for unknown player type" in:
    val unknownPlayer = PlayerBuilder(4).at(7, 7).build()
    val state = newMatch(Score.init()):
      team(West) withBall:
        player(4) at (7, 7)
      team(East):
        player(3) at (6, 6)
      ball at (0, 0)
    val markings = Map[Player, Player]()

    an[IllegalArgumentException] should be thrownBy unknownPlayer.decide(state, markings)

  it should "handle markings for OpponentPlayer" in:
    val player1 = PlayerBuilder(2).at(10, 10).build()
    val player2 = PlayerBuilder(1).at(5, 5).build()
    val state = newMatch(Score.init()):
      team(West) withBall:
        player(1) at (5, 5) ownsBall true
      team(East):
        player(2) at (10, 10)
      ball at (0, 0)
    val markings = Map(player1 -> player2)

    val decision = player1.asOpponentPlayer.decide(state, markings)

    decision should not be Decision.Initial
    decision shouldBe a[Decision]

  it should "handle empty markings for OpponentPlayer" in:
    val opponentPlayer = PlayerBuilder(2).at(10, 10).build().asOpponentPlayer
    val state = newMatch(Score.init()):
      team(West) withBall:
        player(2) at (10, 10)
      team(East):
        player(3) at (6, 6)
      ball at (0, 0)
    val markings = Map[Player, Player]()

    val decision = opponentPlayer.decide(state, markings)

    decision should not be Decision.Initial
    decision shouldBe a[Decision]

  it should "work with different state configurations" in:
    val player1        = PlayerBuilder(1).at(5, 5).build()
    val player2        = PlayerBuilder(2).at(10, 10).build()
    val player3        = PlayerBuilder(3).at(6, 6).build()
    val carrierPlayer  = player1.asBallCarrierPlayer
    val opponentPlayer = player2.asOpponentPlayer
    val teammatePlayer = player3.asTeammatePlayer

    val state = newMatch(Score.init()):
      team(East) withBall:
        player(1) at (5, 5) ownsBall true
        player(3) at (6, 6)
      team(West):
        player(2) at (10, 10)
      ball at (15, 15) move (Direction(1, 1), 2)
    val markings = Map(player2 -> player1)

    val carrierDecision  = carrierPlayer.decide(state, markings)
    val opponentDecision = opponentPlayer.decide(state, markings)
    val teammateDecision = teammatePlayer.decide(state, markings)

    carrierDecision should not be Decision.Initial
    opponentDecision should not be Decision.Initial
    teammateDecision should not be Decision.Initial

  it should "handle players with ball" in:
    val carrierPlayer  = PlayerBuilder(1).at(5, 5).build().asBallCarrierPlayer
    val teammatePlayer = PlayerBuilder(3).at(6, 6).build().asTeammatePlayer
    val state = newMatch(Score.init()):
      team(East) withBall:
        player(1) at (5, 5) ownsBall true
        player(3) at (6, 6)
      team(West):
        player(2) at (10, 10)
      ball at (15, 15) move (Direction(1, 1), 2)
    val markings = Map[Player, Player]()
    val decision = carrierPlayer.decide(state, markings)

    decision should not be Decision.Initial
    decision shouldBe a[Decision]

  it should "handle players near ball" in:
    val opponentPlayer = PlayerBuilder(2).at(5, 5).build().asOpponentPlayer
    val state = newMatch(Score.init()):
      team(East) withBall:
        player(2) at (5, 5) ownsBall true
      team(West):
        player(3) at (6, 6)
      ball at (15, 15) move (Direction(1, 1), 2)
    val markings = Map[Player, Player]()

    val decision = opponentPlayer.decide(state, markings)

    decision should not be Decision.Initial
    decision shouldBe a[Decision]
