package model.decisions

import model.Match.*
import model.decisions.CommonPlayerDecisions.*

/** Type aliases for player roles
  */
object PlayerTypes:
  type AttackingPlayer = Player & CanDecideToPass & CanDecideToShoot & CanDecideToMoveToGoal
  type DefendingPlayer = Player & CanDecideToMark & CanDecideToTackle & CanDecideToIntercept
  type TeammatePlayer  = Player & CanDecideToMoveRandom & CanDecideToReceivePass
