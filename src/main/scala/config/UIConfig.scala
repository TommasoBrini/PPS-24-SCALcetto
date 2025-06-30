package config

import java.awt.{Color, Dimension, Font}

object UIConfig:

  // Window settings
  val windowTitle     = "SCALcetto - Football Simulation"
  val windowMinWidth  = 900
  val windowMinHeight = 600
  val windowResizable = true

  // Panel dimensions
  val fieldPanelWidth  = 800
  val fieldPanelHeight = 500
  val infoPanelHeight  = 50

  // Button settings
  val buttonWidth   = 80
  val buttonHeight  = 35
  val buttonPadding = 8

  // Font settings
  val defaultFont = new Font("Arial", Font.BOLD, 12)
  val titleFont   = new Font("Arial", Font.BOLD, 14)

  // Animation settings
  val defaultFrameRate = 30

  // Color scheme
  object Colors:
    val fieldGreen      = new Color(34, 139, 34)   // Forest Green
    val fieldLines      = new Color(255, 255, 255) // White
    val teamBlue        = new Color(30, 144, 255)  // Dodger Blue
    val teamRed         = new Color(220, 20, 60)   // Crimson
    val ballColor       = new Color(255, 255, 255) // White
    val goalColor       = new Color(0, 0, 0)       // Black
    val backgroundColor = new Color(240, 240, 240) // Light Gray
    val buttonColor     = new Color(70, 130, 180)  // Steel Blue
    val buttonHover     = new Color(100, 149, 237) // Cornflower Blue
    val textColor       = new Color(50, 50, 50)    // Dark Gray
    val infoPanelColor  = new Color(255, 255, 255) // White

  // Drawing settings
  object Drawing:
    val fieldBorderWidth = 2
    val ballBorderWidth  = 2
