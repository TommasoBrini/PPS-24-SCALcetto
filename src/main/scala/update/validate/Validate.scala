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
      case Tackle(_)  => 0.5
      case _          => 1

  private def getSuccessAction(decision: Decision): Action =
    decision match
      case Confusion(step)      => Action.Stopped(step)
      case Pass(from, to)       => Action.Hit(from.position.getDirection(to.position), FieldConfig.ballSpeed)
      case Shoot(striker, goal) => Action.Hit(striker.position.getDirection(goal), FieldConfig.ballSpeed + 1) // todo
      case Run(direction)       => Action.Move(direction, FieldConfig.playerSpeed)
      case MoveToGoal(goalDirection) =>
        Action.Move(goalDirection, FieldConfig.playerSpeed)
      case Tackle(ball) => Action.Take(ball)
      case ReceivePass(ball) =>
        Action.Take(ball)
      case Intercept(ball) =>
        Action.Take(ball)
      case MoveToBall(direction, speed) => Action.Move(direction, speed)
      case MoveRandom(direction) =>
        Action.Move(direction, FieldConfig.playerSpeed)
      case _ => Action.Initial

  private def getFailureAction(decision: Decision): Action =
    decision match
      case Pass(from, to) => Action.Hit(from.position.getDirection(to.position), FieldConfig.ballSpeed)
      case Tackle(_)      => Action.Stopped(FieldConfig.stoppedAfterTackle)
      case _              => Action.Initial

}
