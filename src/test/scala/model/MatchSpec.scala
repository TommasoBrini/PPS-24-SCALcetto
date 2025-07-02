package model

import model.Match.*
import model.Space.*
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class MatchSpec extends AnyFlatSpec with Matchers:
  "A Team" should "contain an id and a list of players" in:
    val players = List(
      Player(1, Position(1, 1), Movement.still),
      Player(2, Position(2, 2), Movement.still)
    )
    val team = Team(7, players, false)

    team.id shouldBe 7
    team.players should have size 2

  "A Ball" should "store its position and movement" in:
    val ball = Ball(Position(3, 4), Movement(Direction(1.0, 1.0), 2))

    ball.position shouldBe Position(3, 4)
    ball.movement.direction shouldBe Direction(1.0, 1.0)
    ball.movement.speed shouldBe 2

  "A MatchState" should "contain two teams and a ball" in:
    val team1 = Team(1, List(Player(1, Position(1, 1), Movement.still)), false)
    val team2 = Team(2, List(Player(2, Position(2, 2), Movement.still)), false)
    val ball  = Ball(Position(0, 0), Movement.still)
    val state = MatchState(List(team1, team2), ball)

    state.teams should have size 2
    state.ball shouldBe ball
