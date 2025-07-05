package update.decide

import config.UIConfig
import model.Match.*
import Side.*
import model.Match.Decision.*
import model.Space.*
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import Decide.*
import model.decisions.PlayerDecisionFactory.*
import config.MatchConfig
import dsl.SpaceSyntax.*

class DecideSpec extends AnyFlatSpec with Matchers:

  "Decide.decide" should "update all players with new decisions" in:
    val player1 = Player(1, Position(5, 5), Movement.still).asControlDecisionPlayer
    val player2 = Player(2, Position(10, 10), Movement.still).asOpponentDecisionPlayer
    val player3 = Player(3, Position(15, 15), Movement.still).asTeammateDecisionPlayer
    val team1   = Team(List(player1, player3), hasBall = true)
    val team2   = Team(List(player2))
    val ball    = Ball(Position(0, 0), Movement.still)
    val state   = Match((team1, team2), ball)

    val updatedState = decide(state)

    updatedState.teams.players.foreach { player =>
      player.decision should not be Decision.Initial
    }

  it should "assign markings between defenders and attackers" in:
    val attacker      = Player(1, Position(5, 5), Movement.still).asControlDecisionPlayer
    val defender      = Player(2, Position(10, 10), Movement.still).asOpponentDecisionPlayer
    val teammate      = Player(3, Position(15, 15), Movement.still).asTeammateDecisionPlayer
    val attackingTeam = Team(List(attacker, teammate), hasBall = true)
    val defendingTeam = Team(List(defender))
    val ball          = Ball(Position(0, 0), Movement.still)
    val state         = Match((attackingTeam, defendingTeam), ball)

    val updatedState    = decide(state)
    val teamWithBall    = updatedState.teams.withBall
    val updatedAttacker = teamWithBall.get.players.head
    val updatedDefender = updatedState.teams.opponentOf(teamWithBall.get).players.head

    updatedAttacker.decision should not be Decision.Initial
    updatedDefender.decision should not be Decision.Initial

  it should "handle empty teams gracefully" in:
    val emptyTeam1 = Team(List(), hasBall = true)
    val emptyTeam2 = Team(List())
    val ball       = Ball(Position(0, 0), Movement.still)
    val state      = Match((emptyTeam1, emptyTeam2), ball)

    val updatedState = decide(state)

    updatedState.teams.players should have size 0

  it should "preserve team structure and ball state" in:
    val player1  = Player(1, Position(5, 5), Movement.still).asControlDecisionPlayer
    val player2  = Player(2, Position(10, 10), Movement.still).asOpponentDecisionPlayer
    val teammate = Player(3, Position(15, 15), Movement.still).asTeammateDecisionPlayer
    val team1    = Team(List(player1, teammate), hasBall = true)
    val team2    = Team(List(player2), hasBall = false)
    val ball     = Ball(Position(15, 15), Movement(Direction(1, 1), 2))
    val state    = Match((team1, team2), ball)

    val updatedState = decide(state)

    updatedState.teams.head.hasBall shouldBe true
    updatedState.teams(1).hasBall shouldBe false
    updatedState.ball shouldBe ball

  it should "update decisions for multiple players per team" in:
    val player1 = Player(1, Position(5, 5), Movement.still).asControlDecisionPlayer
    val player2 = Player(2, Position(6, 6), Movement.still).asTeammateDecisionPlayer
    val player3 = Player(3, Position(10, 10), Movement.still).asOpponentDecisionPlayer
    val player4 = Player(4, Position(11, 11), Movement.still).asOpponentDecisionPlayer

    val team1 = Team(List(player1, player2), hasBall = true)
    val team2 = Team(List(player3, player4), hasBall = false)
    val ball  = Ball(Position(0, 0), Movement.still)
    val state = Match((team1, team2), ball)

    val updatedState = decide(state)

    updatedState.teams.players.foreach { player =>
      player.decision should not be Decision.Initial
    }

  it should "handle players with existing decisions" in:
    val player1 = Player(
      1,
      Position(5, 5),
      Movement.still,
      decision = Run(Direction(1, 0), MatchConfig.runSteps)
    ).asControlDecisionPlayer
    val player2 =
      Player(2, Position(10, 10), Movement.still, decision = Mark(player1, player1, East)).asOpponentDecisionPlayer
    val team1 = Team(List(player1), side = West, hasBall = true)
    val team2 = Team(List(player2), side = East, hasBall = false)
    val ball  = Ball(Position(0, 0), Movement.still)
    val state = Match((team1, team2), ball)

    val updatedState = decide(state)

    updatedState.teams.players.foreach { player =>
      player.decision should not be Run(Direction(1, 0), MatchConfig.runSteps)
      player.decision should not be Mark(player1, player1, East)
    }
