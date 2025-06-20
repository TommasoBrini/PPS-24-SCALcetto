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
      case Pass(from, to) => 1
      case _              => 1

  private def getSuccessAction(decision: Decision): Action =
    decision match
      case Pass(from, to) => Action.Hit(from.position.getDirection(to.position), FieldConfig.ballSpeed)
      case _              => Action.Initial

  private def getFailureAction(decision: Decision): Action =
    decision match
      case Pass(from, to) => Action.Hit(from.position.getDirection(to.position), FieldConfig.ballSpeed)
      case _              => Action.Initial

}
