package model

import model.Model.*
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class TestModel extends AnyFlatSpec with Matchers:

  val player: Player = Player(1, Position(0, 0), PlayerStatus.noControl, None, Movement(Direction.none, 0))

  "Position" should "store x and y coordinates" in:
    val position = Position(10, 20)
    position.x shouldBe 10
    position.y shouldBe 20

  "Direction" should "expose x and y components" in:
    val direction = Direction(1, 0)
    direction.x shouldBe 1
    direction.y shouldBe 0
  it should "have a zero direction" in:
    val zeroDirection = Direction.none
    zeroDirection.x shouldBe 0
    zeroDirection.y shouldBe 0

  "Movement" should "store direction and speed" in:
    val movement = Movement(Direction(1, 0), 5)
    movement.direction.x shouldBe 1
    movement.direction.y shouldBe 0
    movement.speed shouldBe 5

  "A player" should "be created correctly" in:
    player.id shouldEqual 1
    player.position shouldEqual Position(0, 0)
    player.nextAction shouldEqual None

  it should "have a movement with zero direction and speed" in:
    player.movement.direction shouldEqual Direction.none
    player.movement.speed shouldEqual 0

  it should "allow setting a next action" in:
    val action: Action = Action.Move(Direction(1, 1))
    val updatedPlayer  = player.copy(nextAction = Some(action))
    player.nextAction shouldEqual None
    updatedPlayer.nextAction shouldEqual Some(action)

  it should "take control of the ball" in:
    val updatedPlayer = player.copy(status = PlayerStatus.ballControl)
    updatedPlayer.status shouldEqual PlayerStatus.ballControl

  "A team" should "be created with players" in:
    val second_player: Player = Player(2, Position(1, 1), PlayerStatus.noControl, None, Movement(Direction.none, 0))
    val team: Team            = Team(1, List(player, second_player))
    team.players shouldEqual List(player, second_player)

  "A ball" should "be created with a position and movement" in:
    val ball: Ball = Ball(Position(0, 0), Movement(Direction.none, 0))
    ball.position shouldEqual Position(0, 0)
    ball.movement.direction shouldEqual Direction.none
    ball.movement.speed shouldEqual 0

  "A simulation state" should "contain teams and a ball" in:
    val second_player: Player = Player(2, Position(1, 1), PlayerStatus.noControl, None, Movement(Direction.none, 0))
    val team1: Team           = Team(1, List(player))
    val team2: Team           = Team(2, List(second_player))
    val ball: Ball            = Ball(Position(0, 0), Movement(Direction.none, 0))
    val state: MatchState     = MatchState(List(team1, team2), ball)
    state.teams shouldEqual List(team1, team2)
    state.ball shouldEqual ball
