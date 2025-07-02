package model.decisions.rating
import model.Match.*
import config.MatchConfig
import config.Util

object ControlDecisionRating:
  extension (run: Decision.Run)
    def rate(state: MatchState): Double = 0

  extension (pass: Decision.Pass)
    def rate(state: MatchState): Double =
      1 / pass.from.position.getDistance(pass.to.position)

  extension (shoot: Decision.Shoot)
    def rate(state: MatchState): Double =
      val opponentsInBetween: List[Player] = state.teams
        .flatMap(_.players)
        .filterNot(_.hasBall)
        .filter(opp => Util.positionIsInBetween(shoot.striker.position, shoot.goal, opp.position))
      val shootRating: Double = shoot.striker.position.getDistance(shoot.goal) match
        case dist if dist <= MatchConfig.lowDistanceShoot  => 3
        case dist if dist <= MatchConfig.midDistanceShoot  => 0.70
        case dist if dist <= MatchConfig.highDistanceShoot => 0.30
        case _                                             => 0.0
      if opponentsInBetween.isEmpty && shoot.striker.decision != Decision.Initial
      then shootRating
      else 0.0

  extension (moveToGoal: Decision.MoveToGoal)
    def rate(player: Player, state: MatchState): Double =
      if Util.isPathClear(player.position, moveToGoal.goalDirection, state) && player.decision != Decision.Initial then
        0.9
      else 0.0
