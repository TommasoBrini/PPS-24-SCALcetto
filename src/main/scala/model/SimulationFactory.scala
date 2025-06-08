package model

import scala.util.Random
import config.FieldConfig.*
import model.Model.*

object SimulationFactory:

  def initialSimulationState(): SimulationState =
    val teamsA = createTeam(1, true)
    val teamsB = createTeam(2, false)
    val ball   = Ball(Position(fieldWidth / 2, fieldHeight / 2), Movement(Direction(0, 0), 0))
    SimulationState(List(teamsA, teamsB), ball)

  private def createTeam(id: Int, isLeftSide: Boolean): Team =
    val minX = if isLeftSide then 1 else fieldWidth / 2 + 1
    val maxX = if isLeftSide then fieldWidth / 2 - 1 else fieldWidth - 2

    val players = (0 until teamSize).map { i =>
      val posX = Random.between(minX, maxX + 1)
      val posY = Random.between(1, fieldHeight - 1)
      Player(
        id = id * 10 + i,
        position = Position(posX, posY),
        status = PlayerStatus.noControl,
        nextAction = None,
        movement = Movement(Direction(0, 0), 0)
      )
    }.toList
    Team(id, players)

  private def createTeamWithBall(id: Int, ballControl: Boolean): Team =
    val minX: Int = if ballControl then 1 else fieldWidth / 2 + 1
    val maxX: Int = if ballControl then fieldWidth / 2 - 1 else fieldWidth - 2

    val players: List[Player] = (0 until teamSize - 1).map { i =>
      val posX = Random.between(minX, maxX + 1)
      val posY = Random.between(1, fieldHeight - 1)
      Player(
        id = id * 10 + i,
        position = Position(posX, posY),
        status = PlayerStatus.teamControl,
        nextAction = None,
        movement = Movement(Direction(0, 0), 0)
      )
    }.toList

    val ballPlayer: Player = Player(
      id = id * 10 + 22, // TODO change this 22, its ugly
      position = Position(fieldWidth / 2 , fieldHeight / 2 + 1),
      status = PlayerStatus.ballControl,
      nextAction = None,
      movement = Movement(Direction(0, 0), 0)

    )

    Team(id, ballPlayer :: players)