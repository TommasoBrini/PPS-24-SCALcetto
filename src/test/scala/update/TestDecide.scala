package update

import model.Model.*
import model.Model.Action.Move
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class TestDecide extends AnyFlatSpec with Matchers:

  "Player in team with ball" should "take decision" in:
    val player: Player = Player(1, Position(0, 0), PlayerStatus.teamControl, None, Movement(Direction.zero, 0))
    val newPlayer      = Decide.decideOfPlayerInTeamWithBall(player)
    newPlayer.nextAction.isDefined shouldBe true

  it should "set next action to Move" in:
    val player: Player = Player(1, Position(0, 0), PlayerStatus.teamControl, None, Movement(Direction.zero, 0))
    val newPlayer      = Decide.decideOfPlayerInTeamWithBall(player)
    val position       = newPlayer.nextAction.get.asInstanceOf[Move].target
    newPlayer.nextAction.get shouldBe Move(position)

  it should "set correct target" in:
    val player: Player  = Player(1, Position(0, 0), PlayerStatus.teamControl, None, Movement(Direction.zero, 0))
    val newPlayer       = Decide.decideOfPlayerInTeamWithBall(player)
    val position        = newPlayer.nextAction.get.asInstanceOf[Move].target
    val differentX: Int = player.position.x - position.x
    val differentY: Int = player.position.y - position.y
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

    val team: Team             = Team(1, List(player1))
    val ball: Ball             = Ball(Position(10, 10), Movement(Direction.zero, 0))
    val state: SimulationState = SimulationState(List(team), ball)

    val updatedState = Decide.takeDecisions(state)
    val updatedTeam  = updatedState.teams.head

    val updatedPlayer = updatedTeam.players.find(_.id == 1).get
    updatedPlayer.nextAction.isDefined shouldBe true
    updatedPlayer.nextAction.get.isInstanceOf[Action.Move] shouldBe true
