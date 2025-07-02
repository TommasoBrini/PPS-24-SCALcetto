package update.validate

import model.Match.*
import config.MatchConfig
import config.UIConfig
import model.Match.Action.*
import model.Match.Decision.*
import model.Space.Position
import dsl.SpaceSyntax.*

import scala.util.Random

object Validate {
  extension (state: MatchState)
    def validate(): MatchState =
      val updated = state.copy(teams = state.teams.map(_.validate()))
      val tackleWithSuccess = updated.players.exists {
        case Player(_, _, _, _, Tackle(_), Take(_)) => true
        case _                                      => false
      }
      if tackleWithSuccess then updated.confuseLastCarrier()
      else updated

    def confuseLastCarrier(): MatchState =
      state.copy(teams =
        state.teams.map(t =>
          t.copy(players = t.players.map({
            case p if p.hasBall => p.copy(action = Stopped(MatchConfig.stoppedAfterTackle))
            case p              => p
          }))
        )
      )

  extension (team: Team)
    def validate(): Team =
      Team(team.players.map(p => p.validate()), team.hasBall)

  extension (player: Player)
    def validate(): Player =
      val accuracy = Random.nextDouble()
      player.copy(action =
        if accuracy < getSuccessRate(player.decision)
        then getSuccessAction(player.decision)
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
      case Pass(from, to)       => Action.Hit(from.position.getDirection(to.position), MatchConfig.ballSpeed)
      case Shoot(striker, goal) => Action.Hit(striker.position.getDirection(goal), MatchConfig.ballSpeed + 1) // todo
      case Run(direction)       => Action.Move(direction, MatchConfig.playerSpeed)
      case MoveToGoal(goalDirection) => Action.Move(goalDirection, MatchConfig.playerSpeed)
      case Tackle(ball)              => Action.Take(ball)
      case ReceivePass(ball)         => Action.Take(ball)
      case Intercept(ball)           => Action.Take(ball)
      case MoveToBall(direction)     => Action.Move(direction, MatchConfig.playerSpeed)
      case MoveRandom(direction, _)  => Action.Move(direction, MatchConfig.playerSpeed)
      case Mark(player, target) => Action.Move(player.position.getDirection(target.position), MatchConfig.playerSpeed)
      case _                    => Action.Initial

  private def getFailureAction(decision: Decision, accuracy: Double): Action =
    (decision, accuracy) match
      case (Pass(from, to), _) => Action.Hit(from.position.getDirection(to.position).jitter, MatchConfig.ballSpeed)
      case (Tackle(_), _)      => Action.Stopped(MatchConfig.stoppedAfterTackle)
      case (Shoot(striker, goal), accuracy) => failedShoot(striker, goal, accuracy)
      case _                                => Action.Initial

  private def shootSuccess(striker: Player, goal: Position): Double = striker.position.getDistance(goal) match
    case goalDistance if goalDistance <= MatchConfig.lowDistanceShoot  => 0.1
    case goalDistance if goalDistance <= MatchConfig.midDistanceShoot  => 0.6
    case goalDistance if goalDistance <= MatchConfig.highDistanceShoot => 0.4
    case _                                                             => 0.0

  private def failedShoot(striker: Player, goal: Position, accuracy: Double): Action =
    val targetOffset: Double =
      UIConfig.goalHeight.toDouble * Math.abs(shootSuccess(striker, goal) - accuracy) * 2
    val newShootingTarget: Position = Random.nextDouble() match
      case rand if rand >= 0.5 => Position(goal.x, goal.y - targetOffset.toInt)
      case _                   => Position(goal.x, goal.y + targetOffset.toInt)
    Action.Hit(striker.position.getDirection(newShootingTarget), MatchConfig.ballSpeed + 1)

}
