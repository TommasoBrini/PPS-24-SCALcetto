package update.decide

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import model.Match.*
import config.UIConfig.*
import config.MatchConfig.*
import init.GameInitializer.{realFieldHeight, realFieldWidth}
import junit.runner.Version.id

import scala.::
import scala.util.Random

class TestControlPlayerStrategy extends AnyFunSpec with Matchers {

  private def generateTeamLeftInLine(id: Int): Team =
    val players = (0 until teamSize).map { i =>
      val posX: Int = 1
      val posY: Int = Random.between(1, fieldHeight)
      Player(
        id = id * 10 + i,
        position = Position(posX, posY),
        movement = Movement(Direction(0, 0), 0),
        decision = Decision.Initial
      )
    }.toList
    Team(players, Side.West)

  private def generateTeamRightTeamWithBall(id: Int): Team =
    val players: List[Player] = (0 until teamSize - 1).map { i =>
      val posX: Int = fieldWidth / 2
      val posY: Int = Random.between(1, fieldHeight - 1)
      Player(
        id = id * 10 + i,
        position = Position(posX, posY),
        movement = Movement(Direction(0, 0), 0),
        decision = Decision.Initial
      )
    }.toList
    Team(players, Side.East)

//  describe("A controlling player (has the ball)") {
//
//    it("shoots when close and the path is clear") {
//      val opponents: Team = generateTeamLeftInLine(1)
//      val shootPosition: Position = Position(goalEastX - goalAreaWidthScaled, midGoalY)
//      val striker: Player = Player(
//        id = 2 * 10 + 50,
//        position = shootPosition,
//        ball = Some(Ball(shootPosition, Movement(Direction(0, 0), 0))),
//        movement = Movement(Direction(0, 0), 0),
//        decision = Decision.Confusion(1)
//      )
//      val baseTeam = generateTeamRightTeamWithBall(2)
//      val ballTeam = baseTeam.copy(
//        players = striker :: baseTeam.players
//      )
//
//      val ms = update(state(opponents, baseTeam), Event.StepEvent)
//      ControlPlayerStrategy.decide(striker, ms) shouldBe a [Decision.Shoot]
//    }

//    it("passes when a defender blocks the straight line") {
//      val striker = player(1, Position(goalEastX - 10, midGoalY), withBall = true)
//      val blocker = player(3, Position(goalEastX - 5,  midGoalY))      // on the line
//      val mate    = player(2, Position(goalEastX - 20, midGoalY + 8))
//
//      val ms = state(team(1, striker, mate), team(2, blocker))
//
//    }
//
//    it("prefers a short safe pass to a long-range shot") {
//      val striker = player(1, Position(goalEastX - midDistanceShoot + 1, midGoalY), withBall = true)
//      val mate    = player(2, Position(goalEastX - 5, midGoalY + 3))
//
//      val ms = state(team(1, striker, mate))
//    }
//  }

//  describe("A teammate (same team, no ball)") {
//
//    it("never tries to shoot") {
//      val runner = player(1, Position(40, 20))                            // no ball
//      val striker= player(2, Position(goalEastX - 8, midGoalY), withBall = true)
//
//      val ms = state(team(1, striker, runner))
//      true
//    }
//  }
}
