package dsl.decisions

import model.Match.*
import CommonPlayerDecisions.*

/** Type aliases that define player roles with specific decision-making capabilities. */
object PlayerTypes:
  type BallCarrierPlayer = Player & CanDecideToPass & CanDecideToShoot & CanDecideToMoveToGoal
  type OpponentPlayer    = Player & CanDecideToMark & CanDecideToTackle & CanDecideToIntercept
  type TeammatePlayer    = Player & CanDecideToMoveRandom & CanDecideToReceivePass
