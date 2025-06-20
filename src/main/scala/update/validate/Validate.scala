package update.validate

import model.Match.{Action, Decision, MatchState, Player, Team}
import Decision.*
import config.FieldConfig

import scala.util.Random

object Validate {
  def validate(state: MatchState): MatchState =
    state.copy(teams = state.teams.map(validate))

  def validate(team: Team): Team =
    Team(team.id, team.players.map(validate))

  def validate(player: Player): Player =
    player.copy(nextAction =
      if Random.nextDouble() < getSuccessRate(player.decision) then getSuccessAction(player.decision)
      else getFailureAction(player.decision)
    )

  private def getSuccessRate(decision: Decision): Double =
    decision match
      case Pass(_, _) => 1
      case _          => 1

  private def getSuccessAction(decision: Decision): Action =
    decision match
      case Pass(from, to) => Action.Hit(from.position.getDirection(to.position), FieldConfig.ballSpeed)
      case MoveToGoal(player, goalPosition) =>
        Action.Move(player.position.getDirection(goalPosition), FieldConfig.playerSpeed)
      case Run(direction)               => Action.Move(direction, FieldConfig.playerSpeed)
      case MoveToBall(direction, speed) => Action.Move(direction, speed)
      case Tackle(ball)                 => Action.Take(ball)
      case Confusion(step)              => Action.Stopped(step)
      case _                            => Action.Initial

  private def getFailureAction(decision: Decision): Action =
    decision match
      case Pass(from, to) => Action.Hit(from.position.getDirection(to.position), FieldConfig.ballSpeed)
      case _              => Action.Initial

}
