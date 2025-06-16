package update.decide

import model.Model.*

object Decide:

  def takeDecisions(state: MatchState): MatchState =
    state.copy(
      teams = state.teams.map { team =>
        team.copy(players = team.players.map(player => strategySelector(player, state)))
      }
    )

  private def strategySelector(player: Player, matchState: MatchState): Player =
    val teamPossesion: Boolean =
      matchState.teams.exists(team => team.players.exists(_.ball.isDefined) && team.players.contains(player))
    val strategy: DecisionStrategy = player match
      case player if player.ball.isDefined => BallPlayerStrategy
      case player if teamPossesion         => TeamPossesionStrategy
      case _                               => NoControlStrategy
    strategy.decide(player, matchState)
