package update

import model.Model.*
import model.Player.Action.*
import model.Player.{Action, Player, PlayerStatus}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import Decide.*

class TestDecide extends AnyFlatSpec with Matchers:
  "Player in team with ball" should "take decision" in:
    val player: Player = Player(1, Position(0, 0), PlayerStatus.teamControl)
    val simulationState: SimulationState =
      SimulationState(List(Team(1, List(player))), Ball(Position(0, 0), Movement(Direction.zero, 0)))
    val newPlayer: Player = decideActionForPlayer(player, simulationState)
    player.nextAction shouldBe Initial
    newPlayer.nextAction should not be Initial

  it should "set next action to Move" in:
    val player: Player = Player(1, Position(0, 0), PlayerStatus.teamControl)
    val simulationState: SimulationState =
      SimulationState(List(Team(1, List(player))), Ball(Position(0, 0), Movement(Direction.zero, 0)))
    val newPlayer: Player  = decideActionForPlayer(player, simulationState)
    val position: Position = newPlayer.nextAction.asInstanceOf[Move].target
    newPlayer.nextAction shouldBe Move(position)

  it should "set correct target" in:
    val player: Player = Player(1, Position(0, 0), PlayerStatus.teamControl)
    val simulationState: SimulationState =
      SimulationState(List(Team(1, List(player))), Ball(Position(0, 0), Movement(Direction.zero, 0)))
    val newPlayer: Player  = decideActionForPlayer(player, simulationState)
    val position: Position = newPlayer.nextAction.asInstanceOf[Move].target
    val differentX: Int    = player.position.x - position.x
    val differentY: Int    = player.position.y - position.y
    differentX should be <= 1
    differentY should be <= 1

  "The state of the simulation" should "be updated with the new player actions" in:
    val player1 = Player(
      id = 1,
      position = Position(5, 5),
      status = PlayerStatus.teamControl
    )

    val team: Team             = Team(1, List(player1))
    val ball: Ball             = Ball(Position(10, 10), Movement(Direction.zero, 0))
    val state: SimulationState = SimulationState(List(team), ball)

    val updatedState = Decide.takeDecisions(state)
    val updatedTeam  = updatedState.teams.head

    val updatedPlayer = updatedTeam.players.find(_.id == 1).get
    updatedPlayer.nextAction should not be Initial
    updatedPlayer.nextAction.isInstanceOf[Action.Move] shouldBe true

  "Players with noControl" should "move toward the ball" in:
    val noControlPlayer = Player(
      id = 3,
      position = Position(0, 0),
      status = PlayerStatus.noControl
    )

    val team  = Team(2, List(noControlPlayer))
    val ball  = Ball(Position(10, 10), Movement(Direction(0, 0), 0))
    val state = SimulationState(List(team), ball)

    val newState      = Decide.takeDecisions(state)
    val updatedPlayer = newState.teams.head.players.head
    updatedPlayer.nextAction should not be Initial
    updatedPlayer.nextAction.isInstanceOf[Action.Move] shouldBe true
    updatedPlayer.nextAction.asInstanceOf[Action.Move].target shouldBe Position(10, 10)
