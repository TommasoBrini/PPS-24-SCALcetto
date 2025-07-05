package config

import config.UIConfig

object MatchConfig:
  val teamSize: Int              = 10
  val playerSpeed: Int           = 2
  val ballSpeed: Int             = 3
  val interceptBallRange: Int    = 7
  val tackleRange: Int           = 7
  val stoppedAfterHit: Int       = 10
  val stoppedAfterTackle: Int    = 15
  val moveRandomSteps: Int       = 40
  val passDirectionRange: Double = 0.8
  val lowDistanceShoot: Int      = UIConfig.goalAreaWidth
  val midDistanceShoot: Int      = UIConfig.fieldWidth / 3
  val highDistanceShoot: Int     = UIConfig.fieldWidth / 2
