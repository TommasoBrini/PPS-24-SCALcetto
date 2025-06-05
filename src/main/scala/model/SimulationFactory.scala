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
