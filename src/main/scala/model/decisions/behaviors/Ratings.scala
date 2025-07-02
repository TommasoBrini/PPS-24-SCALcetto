package model.decisions.behaviors

import model.Match.*
import model.Space.*
import config.Util
import config.MatchConfig
import model.decisions.DecisorPlayer.*
import scala.annotation.targetName

object Ratings:

  extension (player: ControlPlayer)
    /** Calculate the rating of an action for a player.
      *
      * @param playerDecision
      *   the decision to rate
      * @param state
      *   the current state of the match
      * @return
      *   the rating of the action
      */
    @targetName("calculateActionRatingControl")
    def calculateActionRating(playerDecision: Decision, state: MatchState): Double =
      playerDecision match
        case Decision.Pass(from, to)        => 1 / from.position.getDistance(to.position)
        case Decision.Shoot(stricker, goal) => player.shootRating(state, goal)
        case Decision.MoveToGoal(direction) => player.moveToGoalRating(direction, state)
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

  extension (player: OpponentPlayer)
    @targetName("calculateActionRatingOpponent")
    def calculateActionRating(playerDecision: Decision, state: MatchState): Double =
      playerDecision match
        case Decision.Tackle(ball)    => 100
        case Decision.Intercept(ball) => 80
        case Decision.Mark(_, _)      => 50
        case Decision.MoveToBall(_)   => 30
        case _                        => 0

  extension (player: TeammatePlayer)
    @targetName("calculateActionRatingTeammate")
    def calculateActionRating(playerDecision: Decision, state: MatchState): Double =
      playerDecision match
        case Decision.ReceivePass(ball)            => player.receivePassRating(state, ball)
        case Decision.MoveRandom(direction, steps) => player.moveRandomRating(direction, steps, state)
        case _                                     => 0

    private def receivePassRating(state: MatchState, ball: Ball): Double = 0

    private def moveRandomRating(direction: Direction, steps: Int, state: MatchState): Double = 10
