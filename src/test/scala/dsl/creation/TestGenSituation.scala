package dsl.creation

import config.MatchConfig.teamSize
import config.UIConfig.{fieldHeight, fieldWidth}
import model.Match.Match
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import dsl.game.TeamsSyntax.*

class TestGenSituation extends AnyFlatSpec with Matchers {

  val kickOff: Match = GenSituation.kickOff

  "kickOff" should "create initial simulation state with two teams and a ball" in:
    kickOff.ball should not be null

  it should "assign players to teams correctly" in:
    val teamA = kickOff.teams.head
    val teamB = kickOff.teams(1)
    teamA.players should have size teamSize
    teamB.players should have size teamSize

  it should "assign the ball to one player in team B" in:
    kickOff.teams.teamB.players.exists(_.ball.isDefined) shouldEqual false
    kickOff.teams.teamA.players.exists(_.ball.isDefined) shouldEqual true

  it should "place the ball at the center of the field" in:
    kickOff.ball.position.x shouldEqual (fieldWidth / 2)
    kickOff.ball.position.y shouldEqual (fieldHeight / 2)

  it should "place players within the field boundaries" in:
    kickOff.teams.teamA.players.foreach { player =>
      player.position.x should be >= 1
      player.position.x should be <= fieldWidth - 2
      player.position.y should be >= 1
      player.position.y should be <= fieldHeight - 2
    }

  it should "place players on the correct side of the field" in:
    val teamA = kickOff.teams.head
    val teamB = kickOff.teams(1)

    teamA.players.foreach { player =>
      player.position.x should be <= fieldWidth / 2
    }
    teamB.players.foreach { player =>
      player.position.x should be >= fieldWidth / 2
    }

}
