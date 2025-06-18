package update.decide

import model.Match.{Action, MatchState, Player, Team}
import model.Space.Position
import Action.*
import config.FieldConfig

object ControlPlayerStrategy extends DecisionStrategy:

  private enum PlayerDecision:
    case Pass(from: Player, to: Player)
    case Shoot(striker: Player, goal: Position)
    case MoveToGoal(attacker: Player, goal: Position)

    def toAction: Action = this match
      case Pass(from, to)             => Hit(from.position.getDirection(to.position), FieldConfig.ballSpeed)
      case Shoot(striker, goal)       => Hit(striker.position.getDirection(goal), FieldConfig.ballSpeed)
      case MoveToGoal(attacker, goal) => Move(attacker.position.getDirection(goal), FieldConfig.playerSpeed)

  import PlayerDecision.*

  private def getClosestTeammate(ballPlayer: Player, teams: List[Team]): Player =
    teams.filter(_.players.contains(ballPlayer)).head.players.filter(_.id != ballPlayer.id).head

  private def possiblePasses(player: Player, state: MatchState): List[PlayerDecision] =
    for
      team     <- state.teams.filter(_.players.contains(player))
      teammate <- team.players.filter(!_.equals(player))
    yield Pass(player, teammate)

  private def possibleMoves(player: Player, matchState: MatchState): List[PlayerDecision] = Nil

  private def possibleShots(player: Player, matchState: MatchState): List[PlayerDecision] = Nil

  private def calculateBestAction(player: Player, state: MatchState): Action =
    val possibleActions = possiblePasses(player, state) ++
      possibleMoves(player, state) ++ possibleShots(player, state)
    type Rating = Double
    val actionRatings: Map[PlayerDecision, Rating] = possibleActions
      .map(action => (action, calculateActionRating(action, player, state))).toMap
    actionRatings.maxBy(_._2)._1.toAction

  private def calculateActionRating(action: PlayerDecision, player: Player, state: MatchState): Double =
    action match
      case Pass(from, to)             => 1 / from.position.getDistance(to.position)
      case Shoot(striker, goal)       => ???
      case MoveToGoal(attacker, goal) => ???

  def decide(player: Player, state: MatchState): Player =
    player.copy(
      nextAction = Some(calculateBestAction(player, state))
    )
