package update.decide

import model.Match.*
import monads.States.*
import update.decide.behaviours.{ControlPlayerBehavior, OpponentBehaviour, TeammateBehavior}
object Decide:

  def decideStep: State[Match, Unit] =
    State(s => (decide(s), ()))

  def decide(state: Match): Match =
    state.teams match
      case List(teamA, teamB) =>
        val (defenders, attackers) =
          if teamA.hasBall then (teamB, teamA)
          else (teamA, teamB)

        val markings = assignMarkings(defenders.players, attackers.players)

        def decideFor(player: Player, team: Team, opponents: Team): Decision =
          val behavior =
            if player.hasBall then ControlPlayerBehavior
            else if team.hasBall then TeammateBehavior
            else new OpponentBehaviour(markings.get(player))

          behavior.decide(player, state)

        val newTeams = List(teamA, teamB).map { team =>
          val opponents  = if team == teamA then teamB else teamA
          val newPlayers = team.players.map(p => p.copy(decision = decideFor(p, team, opponents)))
          team.copy(players = newPlayers)
        }

        state.copy(teams = newTeams)
      case _ =>
        throw new IllegalArgumentException("MatchState must contain exactly two teams.")

  private def assignMarkings(defenders: List[Player], attackers: List[Player]): Map[Player, Player] =
    val (withBallOpt, others) = attackers.partition(_.hasBall) match
      case (ballCarrier :: Nil, rest) => (Some(ballCarrier), rest)
      case _                          => (None, attackers)

    var unassignedAttackers = others.toSet
    var availableDefenders  = defenders
    var markings            = Map.empty[Player, Player]

    withBallOpt.foreach { ballCarrier =>
      val maybeDefender = availableDefenders.minByOption(_.position.getDistance(ballCarrier.position))
      maybeDefender.foreach { defender =>
        markings += (defender -> ballCarrier)
        availableDefenders = availableDefenders.filterNot(_ == defender)
      }
    }

    availableDefenders.foreach { defender =>
      val maybeTarget = unassignedAttackers.minByOption(_.position.getDistance(defender.position))
      maybeTarget.foreach { target =>
        markings += (defender -> target)
        unassignedAttackers -= target
      }
    }

    markings
