package update.validate

import model.Match.Action.Hit
import model.Match.{MatchState, Player, Team}

object Validate {
  def validate(state: MatchState): MatchState =
    state.copy(teams = state.teams.map(validate))

  def validate(team: Team): Team =
    Team(team.id, team.players.map(validate))

  def validate(player: Player): Player = player.decision match
    case _ => player.copy(nextAction = player.decision.toAction)

}
