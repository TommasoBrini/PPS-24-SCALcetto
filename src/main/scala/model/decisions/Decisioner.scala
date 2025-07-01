package model.decisions

import model.Match.*
import model.player.Player
import model.decisions.DecisorPlayer.*
import model.decisions.PlayerDecisionFactory.*
import config.UIConfig
import model.decisions.CommonPlayerDecisions.*

object Decisioner:

  extension (player: Player)
    def possibleDecision(state: MatchState): List[Decision] =
      player match
        case p: ControlPlayer =>
          p.possibleShots(state) ++ p.possibleMoves(state) ++ p.possiblePasses(state) ++ p.possibleMovesToGoal(state)
        case _: OpponentPlayer => ???
        case _: TeammatePlayer => ???
        case _                 => throw new IllegalArgumentException("Unknown player type")
