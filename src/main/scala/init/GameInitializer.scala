package init

import config.UIConfig.*
import config.MatchConfig.*
import model.Match.*
import model.Match.Action.Initial
import model.decisions.PlayerRoleFactory.*

import scala.util.Random

object GameInitializer:

  private val realFieldWidth: Int  = fieldWidth
  private val realFieldHeight: Int = fieldHeight

  def initialSimulationState(): MatchState =
    val ball: Ball   = Ball(Position(realFieldWidth / 2, realFieldHeight / 2), Movement(Direction(0, 0), 0))
    val teamsA: Team = createTeam(1, true)
    val teamsB: Team = createTeamWithBall(2, false, ball)
    MatchState((teamsA, teamsB), ball)

  private def createTeam(id: Int, isLeftSide: Boolean): Team =
    val minX: Int = if isLeftSide then 1 else realFieldWidth / 2 + 1
    val maxX: Int = if isLeftSide then realFieldWidth / 2 - 1 else realFieldWidth - 2

    val players = (0 until teamSize).map { i =>
      val posX: Int = Random.between(minX, maxX + 1)
      val posY: Int = Random.between(1, realFieldHeight - 1)
      Player(
        id = id * 10 + i,
        position = Position(posX, posY)
      ).asDefendingPlayer
    }.toList
    Team(id, players, false)

  private def createTeamWithBall(id: Int, isLeftSide: Boolean, b: Ball): Team =
    val minX: Int = if isLeftSide then 1 else realFieldWidth / 2 + 1
    val maxX: Int = if isLeftSide then realFieldWidth / 2 - 1 else realFieldWidth - 2

    val players: List[Player] = (0 until teamSize - 1).map { i =>
      val posX: Int = Random.between(minX, maxX + 1)
      val posY: Int = Random.between(1, realFieldHeight - 1)
      Player(
        id = id * 10 + i,
        position = Position(posX, posY),
        movement = Movement(Direction(0, 0), 0),
        decision = Decision.Initial
      ).asTeammatePlayer
    }.toList
    val ballPlayer: Player = Player(
      id = id * 10 + 22,
      position = Position(realFieldWidth / 2, realFieldHeight / 2),
      ball = Some(b),
      movement = Movement(Direction(0, 0), 0),
      decision = Decision.Initial
    ).asAttackingPlayer
    Team(id, ballPlayer :: players, true)
