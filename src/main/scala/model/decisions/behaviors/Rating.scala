package model.decisions.behaviors

import model.Match.*
import model.Space.*
import config.Util
import config.MatchConfig

object Rating:

  extension (player: Player)
    /** Calculate the rating of an action for a player.
      *
      * @param playerDecision
      *   the decision to rate
      * @param state
      *   the current state of the match
      * @return
      *   the rating of the action
      */
    def calculateActionRating(playerDecision: Decision, state: MatchState): Double =
      playerDecision match
        case Decision.Pass(from, to)        => 1 / from.position.getDistance(to.position)
        case Decision.Shoot(striker, goal)  => striker.shootRating(state, goal)
        case Decision.MoveToGoal(direction) => player.moveToGoalRating(direction, state)
        case Decision.Run(direction)        => player.runRating(direction, state)
        case _                              => 0

    private def shootRating(state: MatchState, goal: Position): Double =
      val opponentsInBetween: List[Player] = state.teams
        .flatMap(_.players)
        .filterNot(_.hasBall)
        .filter(opp => Util.positionIsInBetween(player.position, goal, opp.position))
      val shootRating: Double = player.position.getDistance(goal) match
        case dist if dist <= MatchConfig.lowDistanceShoot  => 3 // todo change this values next meeting
        case dist if dist <= MatchConfig.midDistanceShoot  => 0.70
        case dist if dist <= MatchConfig.highDistanceShoot => 0.30
        case _                                             => 0.0
      if opponentsInBetween.isEmpty
      then shootRating
      else 0.0

    private def moveToGoalRating(goalDirection: Direction, state: MatchState): Double =
      if Util.isPathClear(player.position, goalDirection, state) then 0.9 else 0.0

    private def runRating(dir: Direction, state: MatchState): Double =
      0
