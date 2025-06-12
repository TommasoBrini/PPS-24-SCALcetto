package update.decide

import config.FieldConfig
import model.Model.*
import model.Model.Action.Move
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class TestDecide extends AnyFlatSpec with Matchers:

  "Player in team with ball" should "take decision" in:
    val player: Player = Player(1, Position(0, 0), PlayerStatus.teamControl, None, Movement(Direction.none, 0))
    val newPlayer      = Decide.decideOfPlayerInTeamWithBall(player)
    newPlayer.nextAction.isDefined shouldBe true

  it should "set next action to Move" in:
    val player: Player = Player(1, Position(0, 0), PlayerStatus.teamControl, None, Movement(Direction.none, 0))
    val newPlayer      = Decide.decideOfPlayerInTeamWithBall(player)
    val position       = newPlayer.nextAction.get.asInstanceOf[Move].direction
    newPlayer.nextAction.get shouldBe Move(position, FieldConfig.playerSpeed)

  it should "set correct target" in:
    val player: Player  = Player(1, Position(0, 0), PlayerStatus.teamControl, None, Movement(Direction.none, 0))
    val newPlayer       = Decide.decideOfPlayerInTeamWithBall(player)
    val position        = newPlayer.nextAction.get.asInstanceOf[Move].direction
    val differentX: Int = player.position.x - position.x.toInt
    val differentY: Int = player.position.y - position.y.toInt
    differentX should be <= 1
    differentY should be <= 1

  "The state of the simulation" should "be updated with the new player actions" in:
    val player1 = Player(
      id = 1,
      position = Position(5, 5),
      status = PlayerStatus.teamControl,
      nextAction = None,
      movement = Movement(Direction(0, 0), 0)
    )

    val team: Team        = Team(1, List(player1))
    val ball: Ball        = Ball(Position(10, 10), Movement(Direction.none, 0))
    val state: MatchState = MatchState(List(team), ball)

    val updatedState = Decide.takeDecisions(state)
    val updatedTeam  = updatedState.teams.head

    val updatedPlayer = updatedTeam.players.find(_.id == 1).get
    updatedPlayer.nextAction.isDefined shouldBe true
    updatedPlayer.nextAction.get.isInstanceOf[Action.Move] shouldBe true

  "Players with noControl" should "move toward the ball" in:
    val noControlPlayer = Player(
      id = 3,
      position = Position(0, 0),
      status = PlayerStatus.noControl,
      nextAction = None,
      movement = Movement(Direction(0, 0), 0)
    )

    val team         = Team(2, List(noControlPlayer))
    val ballPosition = Position(10, 10)
    val ball         = Ball(ballPosition, Movement(Direction(0, 0), 0))
    val state        = MatchState(List(team), ball)

    val newState      = Decide.takeDecisions(state)
    val updatedPlayer = newState.teams.head.players.head
    updatedPlayer.nextAction.isDefined shouldBe true
    updatedPlayer.nextAction.get.isInstanceOf[Action.Move] shouldBe true
    updatedPlayer.nextAction.get.asInstanceOf[Action.Move]
      .direction shouldBe noControlPlayer.position.getDirection(ballPosition)
