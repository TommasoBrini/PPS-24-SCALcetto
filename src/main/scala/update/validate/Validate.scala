package update.validate

import model.Match.{Match, Player, Team}
import dsl.decisions.DecisionValidator.toAction
import dsl.game.TeamsSyntax.map
import monads.States.State

object Validate:
  def validateStep: State[Match, Unit] =
    State(s => (s.validate(), {}))

  extension (state: Match)
    def validate(): Match =
      state.copy(teams = state.teams.map(_.validate()))

  extension (team: Team)
    def validate(): Team =
      team.copy(players = team.players.map(_.validate()))

  extension (player: Player)
    def validate(): Player =
      player.copy(nextAction = player.decision.toAction)
