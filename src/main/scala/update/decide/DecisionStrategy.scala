package update.decide

import config.FieldConfig
import model.Match.*
import model.Match.Action.*

import scala.util.Random

enum PlayerAction:
  case Pass(from: Player, to: Player)
  case Shoot(striker: Player, goal: Position)
  case MoveToGoal(attacker: Player, goal: Position)

  def toAction: Action = this match
    case Pass(from, to)             => Hit(from.position.getDirection(to.position), FieldConfig.ballSpeed)
    case Shoot(striker, goal)       => Hit(striker.position.getDirection(goal), FieldConfig.ballSpeed)
    case MoveToGoal(attacker, goal) => Move(attacker.position.getDirection(goal), FieldConfig.playerSpeed)

import PlayerAction.*
trait DecisionStrategy:
  def decide(player: Player, matchState: MatchState): Player

object BallPlayerStrategy extends DecisionStrategy:

  private def getClosestTeammate(ballPlayer: Player, teams: List[Team]): Player =
    teams.filter(_.players.contains(ballPlayer)).head.players.filter(_.id != ballPlayer.id).head

  private def possiblePasses(player: Player, state: MatchState): List[PlayerAction] =
    for
      team     <- state.teams.filter(_.players.contains(player))
      teammate <- team.players.filter(!_.equals(player))
    yield Pass(player, teammate)

  private def possibleMoves(player: Player, matchState: MatchState): List[PlayerAction] = ???

  private def possibleShots(player: Player, matchState: MatchState): List[PlayerAction] = ???

  private def calculateBestAction(player: Player, state: MatchState): Action =
    val possibleActions = possiblePasses(player, state) ++
      possibleMoves(player, state) ++ possibleShots(player, state)
    type Rating = Double
    val actionRatings: Map[PlayerAction, Rating] = possibleActions
      .map(action => (action, calculateActionRating(action, player, state))).toMap
    actionRatings.maxBy(_._2)._1.toAction

  private def calculateActionRating(action: PlayerAction, player: Player, state: MatchState): Double =
    action match
      case Pass(from, to)             => 1 / from.position.getDistance(to.position)
      case Shoot(striker, goal)       => ???
      case MoveToGoal(attacker, goal) => ???

  def decide(player: Player, state: MatchState): Player =
    player.copy(
      nextAction = Some(calculateBestAction(player, state))
    )

object TeamPossessionStrategy extends DecisionStrategy:
  def decide(player: Player, matchState: MatchState): Player =
    val dx: Int   = Random.between(-1, 2)
    val dy: Int   = Random.between(-1, 2)
    val direction = Direction(dx, dy)
    player.copy(
      nextAction = Some(Action.Move(direction, FieldConfig.playerSpeed))
    )

object NoControlStrategy extends DecisionStrategy:
  def decide(player: Player, matchState: MatchState): Player =
    val ball: Ball = matchState.ball
    val dx: Int    = Math.abs(player.position.x - ball.position.x)
    val dy: Int    = Math.abs(player.position.y - ball.position.y)
    val act: Some[Action] =
      if dx < FieldConfig.takeBallRange && dy < FieldConfig.takeBallRange
      then Some(Action.Take(ball))
      else Some(Action.Move(player.position.getDirection(ball.position), FieldConfig.playerSpeed))
    player.copy(
      nextAction = act
    )
