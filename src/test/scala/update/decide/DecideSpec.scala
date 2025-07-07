package update.decide

import config.UIConfig
import model.Match.*
import Side.*
import model.Match.Decision.*
import model.Space.*
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import Decide.*
import dsl.decisions.PlayerRoleFactory.*
import config.MatchConfig
import dsl.creation.CreationSyntax.*
import dsl.game.TeamsSyntax.*

class DecideSpec extends AnyFlatSpec with Matchers:

  "Decide.decide" should "update all players with new decisions" in:
    val state: Match = newMatch {

      team(West).withBall {
        player(1).at(5, 5).ownsBall(true) // ball-carrier
        player(3).at(15, 15)
      }

      team(East) {
        player(2) at (10, 10)
      }

      ball at (0, 0) move (Direction(0, 0), 0)
    }

    val updatedState = decide(state)

    updatedState.teams.players.foreach { player =>
      player.decision should not be Decision.Initial
    }

  it should "assign markings between defenders and attackers" in:
    val state: Match = newMatch {

      /* ---------- ATTACKING team (starts with the ball) ---------- */
      team(West) withBall {
        player(1).at(5, 5).ownsBall(true)
        player(3).at(15, 15)
      }

      /* ---------- DEFENDING team --------------------------------- */
      team(East) { // East defends, no .withBall
        player(2) at (10, 10)
      }

      /* -------------------- BALL --------------------------------- */
      ball at (0, 0) move (Direction(0, 0), 0)
    }
    val updatedState    = decide(state)
    val teamWithBall    = updatedState.teams.withBall
    val updatedAttacker = teamWithBall.get.players.head
    val updatedDefender = updatedState.teams.opponentOf(teamWithBall.get).players.head

    updatedAttacker.decision should not be Decision.Initial
    updatedDefender.decision should not be Decision.Initial

  it should "handle empty teams gracefully" in:
    val state: Match = newMatch {
      team(West) withBall
        team(East)
      ball at (0, 0) move (Direction(0, 0), 0)
    }
    val updatedState = decide(state)
    updatedState.teams.players should have size 0

  it should "preserve team structure and ball state" in:
    val currBall = Ball(Position(15, 15), Movement(Direction(1, 1), 2))
    val state: Match = newMatch {
      team(West) withBall {
        player(1) at (5, 5) ownsBall true
        player(3) at (15, 15)
      }

      team(East) {
        player(2) at (10, 10)
      }

      ball at (15, 15) move (Direction(1, 1), 2)
    }
    val updatedState = decide(state)
    updatedState.teams.head.hasBall shouldBe true
    updatedState.teams(1).hasBall shouldBe false
    updatedState.ball shouldBe currBall

  it should "update decisions for multiple players per team" in:
    val state: Match = newMatch {
      team(West) withBall {               // team1 → hasBall = true
        player(1) at (5, 5) ownsBall true // ball-carrier
        player(2) at (6, 6)               // teammate
      }

      team(East) {            // team2 → hasBall = false
        player(3) at (10, 10) // opponent players
        player(4) at (11, 11)
      }

      ball at (0, 0) move (Direction(0, 0), 0) // still ball at origin
    }

    val updatedState = decide(state)

    updatedState.teams.players.foreach { player =>
      player.decision should not be Decision.Initial
    }

  it should "handle players with existing decisions" in:
    val player1 = playerSolo(1) at (5, 5) decidedTo Run(Direction(1, 0), MatchConfig.runSteps) ownsBall true
    val state: Match = newMatch {
      team(West) withBall {
        playerSolo(1) at (5, 5) decidedTo Run(Direction(1, 0), MatchConfig.runSteps) ownsBall true
      }

      team(East) {
        player(2) at (10, 10) decidedTo Mark(player1.build(), player1.build(), East)
      }

      ball at (0, 0) move (Direction(0, 0), 0)
    }

    val updatedState = decide(state)

    updatedState.teams.players.foreach { player =>
      player.decision should not be Run(Direction(1, 0), MatchConfig.runSteps)
      player.decision should not be Mark(player1.build(), player1.build(), East)
    }
