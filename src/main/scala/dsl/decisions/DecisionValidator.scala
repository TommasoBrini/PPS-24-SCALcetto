package dsl.decisions

import config.{MatchConfig, UIConfig}
import dsl.MatchSyntax.*
import dsl.SpaceSyntax.*
import model.Match.Decision.*
import model.Match.Side.*
import model.Match.*

import scala.util.Random

object DecisionSuccessRate:
  val Pass              = 0.7
  val Tackle            = 0.5
  val ShortDistanceShot = 0.1
  val LongDistanceShot  = 0.4
  val SureDecision      = 1

object DecisionValidator:
  extension (decision: Decision)
    def toAction: Action =
      val accuracy = Random.nextDouble()
      if accuracy < decision.getSuccessRate
      then decision.getSuccessAction
      else decision.getFailureAction(accuracy)

    def getSuccessRate: Double =
      decision match
        case Pass(_, _)           => DecisionSuccessRate.Pass
        case Tackle(_)            => DecisionSuccessRate.Tackle
        case Shoot(striker, goal) => shootSuccess(striker, goal)
        case _                    => DecisionSuccessRate.SureDecision

    def getSuccessAction: Action =
      decision match
        case Confusion(step)           => Action.Stopped(step)
        case Pass(from, to)            => Action.Hit(from.position.getDirection(to.position), MatchConfig.ballSpeed)
        case Shoot(striker, goal)      => Action.Hit(striker.position.getDirection(goal), MatchConfig.ballSpeed + 1)
        case Run(direction, _)         => Action.Move(direction, MatchConfig.playerWithBallSpeed)
        case MoveToGoal(goalDirection) => Action.Move(goalDirection, MatchConfig.playerWithBallSpeed)
        case Tackle(ball)              => Action.Take(ball)
        case ReceivePass(ball)         => Action.Take(ball)
        case Intercept(ball)           => Action.Take(ball)
        case MoveToBall(ballDirection) => Action.Move(ballDirection, MatchConfig.playerMaxSpeed)
        case MoveRandom(direction, _)  => Action.Move(direction, MatchConfig.playerSpeed)
        case Mark(player, target, teamSide) =>
          if target.hasBall then
            Action.Move(player.position.getDirection(target.position), MatchConfig.playerSpeed)
          else
            val strategicDirection = calculateMarkDirection(player, target, teamSide)
            Action.Move(strategicDirection, MatchConfig.playerSpeed)
        case _ => Action.Initial

    def getFailureAction(accuracy: Double): Action =
      (decision, accuracy) match
        case (Pass(from, to), _) => Action.Hit((from.position getDirection to.position).jitter, MatchConfig.ballSpeed)
        case (Tackle(_), _)      => Action.Stopped(MatchConfig.stoppedAfterTackle)
        case (Shoot(striker, goal), accuracy) => failedShoot(striker, goal, accuracy)
        case _                                => Action.Initial

  private def shootSuccess(striker: Player, goal: Position): Double = striker.position distanceFrom goal match
    case goalDistance if goalDistance <= MatchConfig.lowDistanceToGoal  => DecisionSuccessRate.ShortDistanceShot
    case goalDistance if goalDistance <= MatchConfig.highDistanceToGoal => DecisionSuccessRate.LongDistanceShot
    case _                                                              => 0.0

  private def failedShoot(striker: Player, goal: Position, accuracy: Double): Action =
    val targetOffset: Double =
      UIConfig.goalHeight.toDouble * Math.abs(shootSuccess(striker, goal) - accuracy) * 2
    val newShootingTarget: Position = Random.nextDouble() match
      case rand if rand >= 0.5 => Position(goal.x, goal.y - targetOffset.toInt)
      case _                   => Position(goal.x, goal.y + targetOffset.toInt)
    Action.Hit(striker.position.getDirection(newShootingTarget), MatchConfig.ballSpeed + 1)

  private def calculateMarkDirection(defender: Player, target: Player, teamSide: Side): model.Space.Direction =
    val ownGoalX = if teamSide == West then UIConfig.goalWestX else UIConfig.goalEastX
    val ownGoalY = UIConfig.midGoalY
    val ownGoal  = Position(ownGoalX, ownGoalY)

    val targetToGoalDirection = target.position.getDirection(ownGoal)

    val strategicX = target.position.x + (targetToGoalDirection.x * MatchConfig.proximityRange).toInt
    val strategicY = target.position.y + (targetToGoalDirection.y * MatchConfig.proximityRange).toInt

    val clampedX          = Math.max(0, Math.min(UIConfig.fieldWidth, strategicX))
    val clampedY          = Math.max(0, Math.min(UIConfig.fieldHeight, strategicY))
    val strategicPosition = Position(clampedX, clampedY)

    defender.position.getDirection(strategicPosition)
