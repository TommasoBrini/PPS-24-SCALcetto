package update.validate

import model.Match.{Action, Decision, MatchState, Team}
import Decision.*
import config.FieldConfig
import model.player.Player
import model.Space.Position

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
      case Confusion(step)      => Action.Stopped(step)
      case Pass(from, to)       => Action.Hit(from.position.getDirection(to.position), FieldConfig.ballSpeed)
      case Shoot(striker, goal) => Action.Hit(striker.position.getDirection(goal), FieldConfig.ballSpeed + 1) // todo
      case Run(direction)       => Action.Move(direction, FieldConfig.playerSpeed)
      case MoveToGoal(goalDirection) => Action.Move(goalDirection, FieldConfig.playerSpeed)
      case Tackle(ball)              => Action.Take(ball)
      case ReceivePass(ball)         => Action.Take(ball)
      case Intercept(ball)           => Action.Take(ball)
      case MoveToBall(direction)     => Action.Move(direction, FieldConfig.playerSpeed)
      case MoveRandom(direction, _)  => Action.Move(direction, FieldConfig.playerSpeed)
      case Mark(player, target) => Action.Move(player.position.getDirection(target.position), FieldConfig.playerSpeed)
      case _                    => Action.Initial

  private def getFailureAction(decision: Decision, accuracy: Double): Action =
    (decision, accuracy) match
      case (Pass(from, to), _) => Action.Hit(from.position.getDirection(to.position).jitter, FieldConfig.ballSpeed)
      case (Tackle(_), _)      => Action.Stopped(FieldConfig.stoppedAfterTackle)
      case (Shoot(striker, goal), accuracy) => failedShoot(striker, goal, accuracy)
      case _                                => Action.Initial

  private def shootSuccess(striker: Player, goal: Position): Double = striker.position.getDistance(goal) match
    case goalDistance if goalDistance <= FieldConfig.lowDistanceShoot  => 0.1
    case goalDistance if goalDistance <= FieldConfig.midDistanceShoot  => 0.6
    case goalDistance if goalDistance <= FieldConfig.highDistanceShoot => 0.4
    case _                                                             => 0.0

  private def failedShoot(striker: Player, goal: Position, accuracy: Double): Action =
    val targetOffset: Double =
      FieldConfig.goalHeightScaled.toDouble * Math.abs(shootSuccess(striker, goal) - accuracy) * 2
    val newShootingTarget: Position = Random.nextDouble() match
      case rand if rand >= 0.5 => Position(goal.x, goal.y - targetOffset.toInt)
      case _                   => Position(goal.x, goal.y + targetOffset.toInt)
    Action.Hit(striker.position.getDirection(newShootingTarget), FieldConfig.ballSpeed + 1)
