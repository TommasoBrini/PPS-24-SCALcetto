package update.decide

import model.Match.*

object Decide:

  def decide(state: MatchState): MatchState =
    state.copy(
      teams = state.teams.map { team =>
        team.copy(players = team.players.map(player => strategySelector(player, state)))
      }
    )

  private def strategySelector(player: Player, matchState: MatchState): Player =
    val teamPossession: Boolean =
      matchState.teams.exists(team => team.hasBall && team.players.contains(player))
    val strategy: DecisionStrategy = player match
      case player if player.ball.isDefined => ControlPlayerStrategy
      case player if teamPossession        => TeammateStrategy
      case _                               => OpponentStrategy
    player.copy(decision = strategy.decide(player, matchState))
