package model

import model.Match.*
import Side.*
import model.Space.*
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class MatchSpec extends AnyFlatSpec with Matchers:
  "A Team" should "contain a list of players and a side" in:
    val players = List(
      Player(1, Position(1, 1), Movement.still),
      Player(2, Position(2, 2), Movement.still)
    )
    val team = Team(players, West)

    team.players should have size 2
    team.side shouldBe West

  "A Ball" should "store its position and movement" in:
    val ball = Ball(Position(3, 4), Movement(Direction(1.0, 1.0), 2))

    ball.position shouldBe Position(3, 4)
    ball.movement.direction shouldBe Direction(1.0, 1.0)
    ball.movement.speed shouldBe 2

  "A Match" should "contain two teams and a ball" in:
    val team1 = Team(List(Player(1, Position(1, 1), Movement.still)), West)
    val team2 = Team(List(Player(2, Position(2, 2), Movement.still)), East)
    val ball  = Ball(Position(0, 0), Movement.still)
    val state = Match((team1, team2), ball)

    state.teams should matchPattern { case (_: Team, _: Team) => }
    state.ball shouldBe ball
