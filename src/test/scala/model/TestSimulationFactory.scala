package model

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import model.Model.*
import update.factory.SimulationFactory.*
import config.FieldConfig.*

class TestSimulationFactory extends AnyFlatSpec with Matchers:

  val state: SimulationState = initialSimulationState()

  "A initialSimulationState" should "be created with the correct number of teams and players" in:
    state.teams.size shouldBe 2
    state.teams.foreach(_.players.size shouldBe teamSize)

  it should "create the ball in correct position" in:
    state.ball.position.x shouldBe fieldWidth * scale / 2
    state.ball.position.y shouldBe fieldHeight * scale / 2
    state.ball.movement.speed shouldBe 0
    state.ball.movement.direction shouldBe Direction(0, 0)

  it should "spawn team1 players in left half of the field" in:
    state.teams.head.players.foreach { player =>
      player.position.x should be <= fieldWidth * scale / 2
      player.position.y should be >= 0
      player.position.y should be <= fieldHeight * scale
    }

  it should "spawn team2 players in right half of the field" in:
    state.teams(1).players.foreach { player =>
      player.position.x should be > fieldWidth * scale / 2
      player.position.y should be >= 0
      player.position.y should be <= fieldHeight * scale - 2
    }

  it should "place all players within vertical bounds" in:
    val allPlayers = state.teams.flatMap(_.players)
    all(allPlayers.map(_.position.y)) should be > 0
    all(allPlayers.map(_.position.y)) should be < fieldHeight * scale

  it should "set all players to noControl status" in:
    state.teams.flatMap(_.players).foreach { player =>
      player.status shouldBe PlayerStatus.noControl
    }
