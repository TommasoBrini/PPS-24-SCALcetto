package dsl.decisions

import model.Match.*
import CommonPlayerDecisions.*

/** Type aliases that define player roles with specific decision-making capabilities. */
object PlayerTypes:
  type BallCarrierPlayer = Player with CanDecideToPass with CanDecideToShoot with CanDecideToMoveToGoal
  type OpponentPlayer    = Player with CanDecideToMark with CanDecideToTackle with CanDecideToIntercept
  type TeammatePlayer    = Player with CanDecideToMoveRandom with CanDecideToReceivePass
