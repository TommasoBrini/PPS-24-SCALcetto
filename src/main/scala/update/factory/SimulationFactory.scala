package update.factory

import config.FieldConfig.*
import model.Model.*

import scala.util.Random

object SimulationFactory:

  private val realFieldWidth: Int  = fieldWidth * scale
  private val realFieldHeight: Int = fieldHeight * scale

  def initialSimulationState(): SimulationState =
    val teamsA: Team = createTeam(1, true)
    val teamsB: Team = createTeamWithBall(2, false)
    val ball: Ball   = Ball(Position(realFieldWidth / 2, realFieldHeight / 2), Movement(Direction(0, 0), 0))
    SimulationState(List(teamsA, teamsB), ball)

  private def createTeam(id: Int, isLeftSide: Boolean): Team =
    val minX: Int = if isLeftSide then 1 else realFieldWidth / 2 + 1
    val maxX: Int = if isLeftSide then realFieldWidth / 2 - 1 else realFieldWidth - 2

    val players = (0 until teamSize).map { i =>
      val posX: Int = Random.between(minX, maxX + 1)
      val posY: Int = Random.between(1, realFieldHeight - 1)
      Player(
        id = id * 10 + i,
        position = Position(posX, posY),
        status = PlayerStatus.noControl,
        nextAction = None,
        movement = Movement(Direction(0, 0), 0),
        team = PlayerTeam.TeamA
      )
    }.toList
    Team(id, players)

  private def createTeamWithBall(id: Int, isLeftSide: Boolean): Team =
    val minX: Int = if isLeftSide then 1 else realFieldWidth / 2 + 1
    val maxX: Int = if isLeftSide then realFieldWidth / 2 - 1 else realFieldWidth - 2

    val players: List[Player] = (0 until teamSize - 1).map { i =>
      val posX: Int = Random.between(minX, maxX + 1)
      val posY: Int = Random.between(1, realFieldHeight - 1)
      Player(
        id = id * 10 + i,
        position = Position(posX, posY),
        status = PlayerStatus.teamControl,
        nextAction = None,
        movement = Movement(Direction(0, 0), 0),
        team = PlayerTeam.TeamB
      )
    }.toList
    val ballPlayer: Player = Player(
      id = id * 10 + 22, // TODO change this 22, its ugly
      position = Position(realFieldWidth / 2, realFieldHeight / 2),
      status = PlayerStatus.ballControl,
      nextAction = None,
      movement = Movement(Direction(0, 0), 0),
      team = PlayerTeam.TeamB
    )
    Team(id, ballPlayer :: players)
