package init

import config.UIConfig.*
import config.MatchConfig.*
import model.Match.*
import model.Match.Side.West
import model.decisions.PlayerDecisionFactory.*
import dsl.game.PlayerSyntax.*

import scala.util.Random

object GameInitializer:

  private val realFieldWidth: Int  = fieldWidth
  private val realFieldHeight: Int = fieldHeight

  extension (side: Side)
    private def seed: Int = side match
      case West => 1
      case _    => 2

  import Side.*
  def initialSimulationState(): Match =
    val ball: Ball = Ball(Position(realFieldWidth / 2, realFieldHeight / 2), Movement(Direction(0, 0), 0))
    Match((createTeam(West, ball), createTeam(East, ball)), ball)

  private def createTeam(side: Side, ball: Ball): Team =
    val minX: Int = if side == West then 1 else realFieldWidth / 2 + 1
    val maxX: Int = if side == West then realFieldWidth / 2 - 1 else realFieldWidth - 2

    val players = (0 until teamSize).map { i =>
      val posX: Int = Random.between(minX, maxX + 1)
      val posY: Int = Random.between(1, realFieldHeight - 1)
      Player(id = side.seed * 10 + i, position = Position(posX, posY))
    }.toList
    getTeam(players, side, ball)

  private def getTeam(players: List[Player], side: Side, ball: Ball): Team =
    val updated: List[Player] = side match
      case East =>
        val carrier = players.head.copy(position = Position(realFieldWidth / 2, realFieldHeight / 2), ball = Some(ball))
        players.map(p => if p.id == carrier.id then carrier.asControlDecisionPlayer else p.asTeammateDecisionPlayer)
      case _ => players.map(_.asOpponentDecisionPlayer)
    Team(updated, side, hasBall = updated.exists(_.hasBall))
