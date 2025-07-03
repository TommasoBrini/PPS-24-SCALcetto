package config

import config.UIConfig

object MatchConfig:
  val teamSize: Int              = 10
  val playerSpeed: Int           = 2
  val ballSpeed: Int             = 3
  val interceptBallRange: Int    = 10
  val tackleRange: Int           = 7
  val stoppedAfterHit: Int       = 20
  val stoppedAfterTackle: Int    = 15
  val moveRandomSteps: Int       = 40
  val passDirectionRange: Double = 0.6
  val lowDistanceToGoal: Int     = UIConfig.fieldWidth / 8
  val highDistanceToGoal: Int    = UIConfig.fieldWidth / 3
  val proximityRange: Int        = 70
  val runSteps: Int              = 5
