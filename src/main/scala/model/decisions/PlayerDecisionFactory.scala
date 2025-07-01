package model.decisions

import model.player.Player
import model.decisions.DecisorPlayer.*
import model.Match.*
import config.UIConfig

object PlayerDecisionFactory:
  extension (p: Player)
    def asControlDecisionPlayer: ControlPlayer =
      new Player(p.id, p.position, p.movement, p.ball, p.nextAction, p.decision) with CanDecideToPass
        with CanDecideToShoot with CanDecideToMoveToGoal

    def asOpponentDecisionPlayer: OpponentPlayer =
      new Player(p.id, p.position, p.movement, p.ball, p.nextAction, p.decision) with CanDecideToMark
        with CanDecideToTackle with CanDecideToIntercept

    def asTeammateDecisionPlayer: TeammatePlayer =
      new Player(p.id, p.position, p.movement, p.ball, p.nextAction, p.decision) with CanDecideToMoveRandom
        with CanDecideToReceivePass

object DecisorPlayer:
  type ControlPlayer  = Player & CanDecideToPass & CanDecideToShoot & CanDecideToMoveToGoal
  type OpponentPlayer = Player & CanDecideToMark & CanDecideToTackle & CanDecideToIntercept
  type TeammatePlayer = Player & CanDecideToMoveRandom & CanDecideToReceivePass
