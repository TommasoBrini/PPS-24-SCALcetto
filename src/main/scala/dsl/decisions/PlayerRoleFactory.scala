package dsl.decisions

import model.Match.*
import config.UIConfig
import CommonPlayerDecisions.*
import PlayerTypes.*

/** Factory for creating specialized player instances with specific decision-making capabilities.
  *
  * This factory provides extension methods to transform base Player instances into role-specific players that can only
  * make certain types of decisions.
  */
object PlayerRoleFactory:

  extension (player: Player)
    /** Transforms a base player into a ball carrier player with offensive decision-making capabilities.
      *
      * @return
      *   A player with ball carrier decision-making capabilities
      */
    def asBallCarrierPlayer: PlayerTypes.BallCarrierPlayer =
      new Player(player.id, player.position, player.movement, player.ball, player.decision, player.nextAction)
        with CanDecideToPass
        with CanDecideToShoot with CanDecideToMoveToGoal

    /** Transforms a base player into an opponent player with defensive decision-making capabilities.
      *
      * @return
      *   A player with opponent decision-making capabilities
      */
    def asOpponentPlayer: PlayerTypes.OpponentPlayer =
      new Player(player.id, player.position, player.movement, player.ball, player.decision, player.nextAction)
        with CanDecideToMark
        with CanDecideToTackle with CanDecideToIntercept

    /** Transforms a base player into a teammate player with support decision-making capabilities.
      *
      * @return
      *   A player with teammate decision-making capabilities
      */
    def asTeammatePlayer: PlayerTypes.TeammatePlayer =
      new Player(player.id, player.position, player.movement, player.ball, player.decision, player.nextAction)
        with CanDecideToMoveRandom
        with CanDecideToReceivePass

object DecisionGenerator:
  /** Generates all possible decisions for a ball carrier player.
    *
    * @param state
    *   The current match state
    * @return
    *   A list of all possible decisions the ball carrier can make
    */
  extension (player: PlayerTypes.BallCarrierPlayer)
    def generateAllPossibleDecisions(state: MatchState): List[Decision] =
      player.decision match
        case Decision.Initial => player.generatePossiblePasses(state)
        case _ =>
          player.generatePossibleRunDirections(state) ++
            player.generatePossiblePasses(state) ++
            player.generatePossibleShots(state) ++
            player.generatePossibleMovesToGoal(state)
