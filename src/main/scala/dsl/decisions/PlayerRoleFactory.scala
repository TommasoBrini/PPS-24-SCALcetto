package dsl.decisions

import model.Match.*
import config.UIConfig
import CommonPlayerDecisions.*
import PlayerTypes.*

/** Factory for creating specialized player instances with specific decision-making capabilities
  */
object PlayerRoleFactory:

  /** Transforms a base player into an ball carrier player
    * @param player
    *   the base player
    * @return
    *   an ball carrier player
    */
  extension (player: Player)
    def asBallCarrierPlayer: PlayerTypes.BallCarrierPlayer =
      new Player(player.id, player.position, player.movement, player.ball, player.decision, player.nextAction)
        with CanDecideToPass
        with CanDecideToShoot with CanDecideToMoveToGoal

    /** Transforms a base player into an opponent player
      * @return
      *   an opponent player
      */
    def asOpponentPlayer: PlayerTypes.OpponentPlayer =
      new Player(player.id, player.position, player.movement, player.ball, player.decision, player.nextAction)
        with CanDecideToMark
        with CanDecideToTackle with CanDecideToIntercept

    /** Transforms a base player into a teammate player
      * @return
      *   a teammate player
      */
    def asTeammatePlayer: PlayerTypes.TeammatePlayer =
      new Player(player.id, player.position, player.movement, player.ball, player.decision, player.nextAction)
        with CanDecideToMoveRandom
        with CanDecideToReceivePass

/** Factory for generating all possible decisions for a given player
  */
object DecisionGenerator:
  /** Generates all possible decisions for an ball carrier player Combines offensive actions using functional
    * composition
    */
  extension (player: PlayerTypes.BallCarrierPlayer)
    def generateAllPossibleDecisions(state: Match): List[Decision] =
      player.decision match
        case Decision.Initial => player.generatePossiblePasses(state)
        case _ =>
          player.generatePossibleRunDirections(state) ++
            player.generatePossiblePasses(state) ++
            player.generatePossibleShots(state) ++
            player.generatePossibleMovesToGoal(state)
