package config

import java.awt.{Color, Font}

object UIConfig:

  val windowTitle     = "SCALcetto - Football Simulation"
  val windowMinWidth  = 900
  val windowMinHeight = 600
  val windowResizable = true

  val fieldPanelWidth  = 800
  val fieldPanelHeight = 500
  val infoPanelHeight  = 80
  val fieldWidth: Int  = 700
  val fieldHeight: Int = 400

  val ballSize: Int       = 10
  val playerSize: Int     = 10
  val goalAreaWidth: Int  = 60
  val goalAreaHeight: Int = 100
  val goalWidth: Int      = 10
  val goalHeight: Int     = 50
  val goalEastX: Int      = fieldWidth
  val goalWestX: Int      = 0
  val firstPoleY: Int     = (fieldHeight - goalHeight) / 2
  val midGoalY: Int       = fieldHeight / 2
  val secondPoleY: Int    = firstPoleY + goalHeight

  val buttonWidth   = 80
  val buttonHeight  = 35
  val buttonPadding = 8
  val defaultFont   = new Font("Arial", Font.BOLD, 12)
  val titleFont     = new Font("Arial", Font.BOLD, 14)

  object Colors:
    val fieldGreen      = new Color(34, 139, 34)
    val fieldLines      = new Color(255, 255, 255)
    val teamBlue        = new Color(30, 144, 255)
    val teamRed         = new Color(220, 20, 60)
    val ballColor       = new Color(255, 255, 255)
    val goalColor       = new Color(0, 0, 0)
    val backgroundColor = new Color(240, 240, 240)
    val buttonColor     = new Color(70, 130, 180)
    val buttonHover     = new Color(100, 149, 237)
    val textColor       = new Color(50, 50, 50)
    val infoPanelColor  = new Color(255, 255, 255)

  object Drawing:
    val fieldBorderWidth  = 1
    val ballBorderWidth   = 1
    val playerBorderWidth = 1
