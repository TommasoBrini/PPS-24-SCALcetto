package dsl.creation

import CreationSyntax.*
import model.Space.{Direction, Movement, Position}
import model.Match.{Match, Side}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.matchers.should.Matchers.*
import dsl.MatchSyntax.*
import model.Match.Score

final class MatchCreationSyntaxSpec extends AnyFlatSpec with Matchers {

  "newMatch" should "assemble a complete Match when two teams and a ball are declared" in:
    val kickoff: Match = newMatch(Score.init()):
      team(Side.West):
        player(10) at (10, 20)
      team(Side.East):
        player(20) at (80, 20)
      ball at (45, 30) move (Direction(0, 0), 0)
    val (west, east) = kickoff.teams
    west.side shouldBe Side.West
    east.side shouldBe Side.East
    kickoff.ball.position shouldBe Position(45, 30)
    kickoff.ball.movement shouldBe Movement(Direction(0, 0), 0)

  it should "propagate the implicit builders across nested blocks" in:
    val matchObj = newMatch(Score.init()):
      team(Side.West) withBall:
        player(1) at (5, 5) ownsBall true
      team(Side.East):
        player(2) at (90, 5)
      ball at (50, 35)

    matchObj.teams.teamWest.hasBall shouldBe true
    matchObj.teams.teamEast.hasBall shouldBe false

  it should "throw when fewer than two teams are provided" in:
    an[IllegalArgumentException] should be thrownBy {
      newMatch(Score.init()):
        team(Side.West):
          player(3) at (1, 1)
        ball at (10, 10)
    }

}
