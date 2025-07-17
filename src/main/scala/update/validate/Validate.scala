package update.validate

import model.Match.{MatchState, Player, Team}
import dsl.decisions.DecisionValidator.toAction
import dsl.`match`.TeamsSyntax.map
import monads.States.State

object Validate:
  def validateStep: State[MatchState, Unit] =
    State(s => (s.validate(), {}))

  extension (state: MatchState)
    def validate(): MatchState =
      state.copy(teams = state.teams.map(_.validate()))

  extension (team: Team)
    def validate(): Team =
      team.copy(players = team.players.map(_.validate()))

  extension (player: Player)
    def validate(): Player =
      player.copy(nextAction = player.decision.toAction)
