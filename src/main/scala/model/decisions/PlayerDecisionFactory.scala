package model.decisions

import model.decisions.DecisorPlayer.*
import model.Match.*
import config.UIConfig
import model.decisions.CommonPlayerDecisions.*
import scala.annotation.targetName

object PlayerDecisionFactory:
  extension (p: Player)
    def asControlDecisionPlayer: ControlPlayer =
      new Player(p.id, p.position, p.movement, p.ball, p.decision, p.nextAction) with CanDecideToPass
        with CanDecideToShoot with CanDecideToMoveToGoal

    def asOpponentDecisionPlayer: OpponentPlayer =
      new Player(p.id, p.position, p.movement, p.ball, p.decision, p.nextAction) with CanDecideToMark
        with CanDecideToTackle with CanDecideToIntercept

    def asTeammateDecisionPlayer: TeammatePlayer =
      new Player(p.id, p.position, p.movement, p.ball, p.decision, p.nextAction) with CanDecideToMoveRandom
        with CanDecideToReceivePass

object DecisorPlayer:
  type ControlPlayer  = Player & CanDecideToPass & CanDecideToShoot & CanDecideToMoveToGoal
  type OpponentPlayer = Player & CanDecideToMark & CanDecideToTackle & CanDecideToIntercept
  type TeammatePlayer = Player & CanDecideToMoveRandom & CanDecideToReceivePass

object PossibleDecisionFactory:
  extension (player: ControlPlayer)
    def possibleDecisions(state: Match): List[Decision] =
      player.possibleRunDirections(state) ++ player.possiblePasses(state) ++ player.possibleShots(
        state
      ) ++ player.possibleMovesToGoal(state)
