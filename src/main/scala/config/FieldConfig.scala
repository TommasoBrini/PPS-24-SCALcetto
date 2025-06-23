package config

object FieldConfig:
  val scale: Int           = 10
  val fieldWidth: Int      = 70
  val fieldHeight: Int     = 40
  val widthBound: Int      = fieldWidth * scale
  val heightBound: Int     = fieldHeight * scale
  val teamSize: Int        = 5
  val ballSize: Int        = 10
  val playerSize: Int      = 10
  val InfoPanelHeight: Int = 50
  val playerSpeed: Int     = 1
  val ballSpeed: Int       = 3
  val takeBallRange: Int   = 10
  val stoppedSteps: Int    = 10

  // GOAL
  val goalAreaWidth: Int    = 6
  val goalAreaHeight: Int   = 10
  val goalWidth: Int        = 1
  val goalHeight: Int       = 5
  val goalWidthScaled: Int  = goalWidth * scale
  val goalHeightScaled: Int = goalHeight * scale
  val goalEastX: Int        = widthBound
  val goalWestX: Int        = 0
  val firstPoleY: Int       = (heightBound - goalHeightScaled) / 2
  val midGoalY: Int         = heightBound / 2
  val secondPoleY: Int      = firstPoleY + goalHeightScaled
