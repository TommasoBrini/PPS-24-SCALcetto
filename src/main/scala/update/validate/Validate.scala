package update.validate

import model.Match.{Action, Decision, MatchState, Player, Team}
import Decision.*
import config.MatchConfig
import config.UIConfig
import model.Space.Position
import dsl.SpaceSyntax.*

import scala.util.Random

object Validate:
  def validate(state: MatchState): MatchState =
    state.copy(teams = state.teams.map(validate))

  def validate(team: Team): Team =
    Team(team.id, team.players.map(validate), team.hasBall)

  def validate(player: Player): Player =
    val accuracy = Random.nextDouble()
    player.copy(nextAction =
      if accuracy < getSuccessRate(player.decision) then getSuccessAction(player.decision)
      else getFailureAction(player.decision, accuracy)
    )

  private def getSuccessRate(decision: Decision): Double =
    decision match
      case Pass(_, _)           => 0.7
      case Tackle(_)            => 0.5
      case Shoot(striker, goal) => shootSuccess(striker, goal)
      case _                    => 1

  private def getSuccessAction(decision: Decision): Action =
    decision match
      case Confusion(step)           => Action.Stopped(step)
      case Pass(from, to)            => Action.Hit(from.position.getDirection(to.position), MatchConfig.ballSpeed)
      case Shoot(striker, goal)      => Action.Hit(striker.position.getDirection(goal), MatchConfig.ballSpeed + 1)
      case Run(direction, _)         => Action.Move(direction, MatchConfig.playerWithBallSpeed)
      case MoveToGoal(goalDirection) => Action.Move(goalDirection, MatchConfig.playerWithBallSpeed)
      case Tackle(ball)              => Action.Take(ball)
      case ReceivePass(ball)         => Action.Take(ball)
      case Intercept(ball)           => Action.Take(ball)
      case MoveToBall(direction)     => Action.Move(direction, MatchConfig.playerMaxSpeed)
      case MoveRandom(direction, _)  => Action.Move(direction, MatchConfig.playerSpeed)
      case Mark(player, target, teamId) =>
        if target.hasBall then
          Action.Move(player.position.getDirection(target.position), MatchConfig.playerSpeed)
        else
          val strategicDirection = calculateMarkDirection(player, target, teamId)
          Action.Move(strategicDirection, MatchConfig.playerSpeed)
      case _ => Action.Initial

  private def getFailureAction(decision: Decision, accuracy: Double): Action =
    (decision, accuracy) match
      case (Pass(from, to), _) => Action.Hit(from.position.getDirection(to.position).jitter, MatchConfig.ballSpeed)
      case (Tackle(_), _)      => Action.Stopped(MatchConfig.stoppedAfterTackle)
      case (Shoot(striker, goal), accuracy) => failedShoot(striker, goal, accuracy)
      case _                                => Action.Initial

  private def shootSuccess(striker: Player, goal: Position): Double = striker.position.getDistance(goal) match
    case goalDistance if goalDistance <= MatchConfig.lowDistanceToGoal  => 0.1
    case goalDistance if goalDistance <= MatchConfig.highDistanceToGoal => 0.4
    case _                                                              => 0.0

  private def failedShoot(striker: Player, goal: Position, accuracy: Double): Action =
    val targetOffset: Double =
      UIConfig.goalHeight.toDouble * Math.abs(shootSuccess(striker, goal) - accuracy) * 2
    val newShootingTarget: Position = Random.nextDouble() match
      case rand if rand >= 0.5 => Position(goal.x, goal.y - targetOffset.toInt)
      case _                   => Position(goal.x, goal.y + targetOffset.toInt)
    Action.Hit(striker.position.getDirection(newShootingTarget), MatchConfig.ballSpeed + 1)

  private def calculateMarkDirection(defender: Player, target: Player, teamId: Int): model.Space.Direction =
    val ownGoalX = if teamId == 1 then UIConfig.goalWestX else UIConfig.goalEastX
    val ownGoalY = UIConfig.midGoalY
    val ownGoal  = Position(ownGoalX, ownGoalY)

    val targetToGoalDirection = target.position.getDirection(ownGoal)

    val strategicX = target.position.x + (targetToGoalDirection.x * MatchConfig.proximityRange).toInt
    val strategicY = target.position.y + (targetToGoalDirection.y * MatchConfig.proximityRange).toInt

    val clampedX          = Math.max(0, Math.min(UIConfig.fieldWidth, strategicX))
    val clampedY          = Math.max(0, Math.min(UIConfig.fieldHeight, strategicY))
    val strategicPosition = Position(clampedX, clampedY)

    defender.position.getDirection(strategicPosition)
