package config

object FieldConfig:
  val scale: Int                 = 10
  val fieldWidth: Int            = 70
  val fieldHeight: Int           = 40
  val widthBound: Int            = fieldWidth * scale
  val heightBound: Int           = fieldHeight * scale
  val teamSize: Int              = 10
  val ballSize: Int              = 10
  val playerSize: Int            = 10
  val playerSpeed: Int           = 2
  val ballSpeed: Int             = 3
  val interceptBallRange: Int    = 7
  val tackleRange: Int           = 7
  val stoppedAfterHit: Int       = 10
  val stoppedAfterTackle: Int    = 15
  val moveRandomSteps: Int       = 40
  val passDirectionRange: Double = 0.6

  // GOAL
  val goalAreaWidth: Int        = 6
  val goalAreaHeight: Int       = 10
  val goalAreaWidthScaled: Int  = goalAreaWidth * scale
  val goalAreaHeightScaled: Int = goalAreaHeight * scale
  val goalWidth: Int            = 1
  val goalHeight: Int           = 5
  val goalWidthScaled: Int      = goalWidth * scale
  val goalHeightScaled: Int     = goalHeight * scale
  val goalEastX: Int            = widthBound
  val goalWestX: Int            = 0
  val firstPoleY: Int           = (heightBound - goalHeightScaled) / 2
  val midGoalY: Int             = heightBound / 2
  val secondPoleY: Int          = firstPoleY + goalHeightScaled

  // SHOOT
  val lowDistanceShoot: Int  = goalAreaWidthScaled
  val midDistanceShoot: Int  = widthBound / 3
  val highDistanceShoot: Int = widthBound / 2
