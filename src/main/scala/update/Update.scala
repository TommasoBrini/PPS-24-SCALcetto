package update

import model.Model.*
import Event.*

import scala.annotation.tailrec
import scala.util.Random

object Update:
  @tailrec
  def update(simulationState: SimulationState, event: Event): SimulationState = event match
    case Step   => update(simulationState, Decide)
    case Decide => ??? // decidePlayerControl()      // MUNI
    // player in team with ball -> moveRandom()         --  TOM
    // players in team without ball -> decidePlayerMovement()  -- TOM
    // DECIDE THE NEXT ACTION FOR EACH PLAYER, AND SET THE NEXT ACTION IN THE PLAYER'S STATE
    case Act     => ??? // ACT THE NEXT ACTION FOR EACH PLAYER, AND UPDATE THE STATE OF THE SIMULATION  -- EMI
    case Goal    => update(simulationState, Restart)
    case Restart => ??? // RESTART THE GAME, RESETTING THE BALL POSITION AND PLAYER POSITIONS (RANDOM???)  --- TOM
    case _       => simulationState
