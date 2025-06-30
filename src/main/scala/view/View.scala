package view

import scala.swing.*
import scala.swing.event.*
import java.awt.{BasicStroke, Color, Font, Graphics2D, RenderingHints}
import java.awt.geom.{Ellipse2D, Line2D, Rectangle2D}
import config.FieldConfig.*
import config.UIConfig.*
import model.Match.*
import model.Match.Position
import view.RenderUtils.*

object View:

  // Enhanced field panel with better rendering
  class MatchPanel(var state: MatchState) extends Panel:
    background = Colors.backgroundColor

    override def paintComponent(g: Graphics2D): Unit =
      super.paintComponent(g)
      setupGraphics(g)

      // Calculate center offset for better positioning
      val centerX      = size.width / 2
      val centerY      = size.height / 2
      val fieldOffsetX = centerX - (fieldWidth * scale) / 2
      val fieldOffsetY = centerY - (fieldHeight * scale) / 2

      g.translate(fieldOffsetX, fieldOffsetY)

      // Draw field background
      g.setColor(Colors.fieldGreen)
      g.fillRect(0, 0, fieldWidth * scale, fieldHeight * scale)

      // Draw field lines with better styling
      g.setColor(Colors.fieldLines)
      g.setStroke(new BasicStroke(Drawing.fieldBorderWidth))

      // Field border - all four sides
      g.drawLine(0, 0, fieldWidth * scale, 0)                                     // Top line
      g.drawLine(0, 0, 0, fieldHeight * scale)                                    // Left line
      g.drawLine(fieldWidth * scale, 0, fieldWidth * scale, fieldHeight * scale)  // Right line
      g.drawLine(0, fieldHeight * scale, fieldWidth * scale, fieldHeight * scale) // Bottom line

      // Center line
      g.drawLine(fieldWidth * scale / 2, 0, fieldWidth * scale / 2, fieldHeight * scale)

      // Center circle
      val circleSize = 100
      val circleX    = (fieldWidth * scale - circleSize) / 2
      val circleY    = (fieldHeight * scale - circleSize) / 2
      g.drawOval(circleX, circleY, circleSize, circleSize)

      // Goal areas
      g.drawRect(0, ((fieldHeight - goalAreaHeight) * scale) / 2, goalAreaWidth * scale, goalAreaHeight * scale)
      g.drawRect(
        (fieldWidth - goalAreaWidth) * scale,
        (fieldHeight - goalAreaHeight) * scale / 2,
        goalAreaWidth * scale,
        goalAreaHeight * scale
      )

      // Draw ball with shadow effect
      drawCenteredOval(g, state.ball.position, ballSize, Colors.ballColor, Drawing.ballBorderWidth)

      // Draw goals
      g.setColor(Colors.goalColor)
      g.fillRect(-(goalWidth * scale), (fieldHeight - goalHeight) * scale / 2, goalWidth * scale, goalHeight * scale)
      g.fillRect(fieldWidth * scale, (fieldHeight - goalHeight) * scale / 2, goalWidth * scale, goalHeight * scale)

      // Draw players with team colors
      for team <- state.teams do
        val color = if team.id == 1 then Colors.teamBlue else Colors.teamRed
        team.players.foreach { player =>
          drawCenteredRect(g, player.position, playerSize, color, 1)
        }

      // Reset transformation
      g.translate(-fieldOffsetX, -fieldOffsetY)

    // Simple info panel
  class InfoPanel extends Panel:
    background = Colors.infoPanelColor
    border = Swing.EmptyBorder(10)

    override def paintComponent(g: Graphics2D): Unit =
      super.paintComponent(g)
      setupGraphics(g)

      val yOffset = 20
      drawText(g, "SCALcetto - Football Simulation", 10, yOffset, Colors.textColor, titleFont.getSize)

  // Modern styled button
  class StyledButton(text: String) extends Button(text):
    background = Colors.buttonColor
    foreground = Colors.fieldLines
    font = defaultFont
    border = Swing.EmptyBorder(buttonPadding, buttonPadding * 2, buttonPadding, buttonPadding * 2)
    preferredSize = new Dimension(buttonWidth, buttonHeight)

    reactions += {
      case ButtonClicked(_) =>
        background = Colors.buttonHover
        repaint()
    }

  // Enhanced main view with better layout and controls
  class SwingView(initialState: MatchState):
    private val panel: MatchPanel    = new MatchPanel(initialState)
    private val infoPanel: InfoPanel = new InfoPanel()

    // Set preferred size for better responsiveness
    panel.preferredSize = new Dimension(fieldPanelWidth, fieldPanelHeight)
    infoPanel.preferredSize = new Dimension(fieldPanelWidth, infoPanelHeight)

    // Enhanced controls
    private val startButton: StyledButton  = new StyledButton("▶ Start")
    private val pauseButton: StyledButton  = new StyledButton("⏸ Pause")
    private val resumeButton: StyledButton = new StyledButton("▶ Resume")
    private val resetButton: StyledButton  = new StyledButton("🔄 Reset")

    // Initialize button states
    pauseButton.enabled = false
    resumeButton.enabled = false

    // Create control panel with better layout
    private val controlPanel: Panel = new FlowPanel:
      contents ++= Seq(
        startButton,
        pauseButton,
        resumeButton,
        resetButton
      )
      border = Swing.EmptyBorder(10)
      background = Colors.backgroundColor

    // Main frame with improved layout
    private val frame: MainFrame = new MainFrame:
      title = windowTitle
      contents = new BorderPanel {
        layout(infoPanel) = BorderPanel.Position.North
        layout(panel) = BorderPanel.Position.Center
        layout(controlPanel) = BorderPanel.Position.South
      }
      resizable = windowResizable
      minimumSize = new Dimension(windowMinWidth, windowMinHeight)
      centerOnScreen()
      visible = true

    // Event handlers with improved state management
    def onStart(action: => Unit): Unit =
      startButton.reactions += {
        case ButtonClicked(_) =>
          startButton.enabled = false
          pauseButton.enabled = true
          action
      }

    def onPause(action: => Unit): Unit =
      pauseButton.reactions += {
        case ButtonClicked(_) =>
          pauseButton.enabled = false
          resumeButton.enabled = true
          action
      }

    def onResume(action: => Unit): Unit =
      resumeButton.reactions += {
        case ButtonClicked(_) =>
          pauseButton.enabled = true
          resumeButton.enabled = false
          action
      }

    def onReset(action: => Unit): Unit =
      resetButton.reactions += {
        case ButtonClicked(_) =>
          startButton.enabled = true
          pauseButton.enabled = false
          resumeButton.enabled = false
          action
      }

    def render(state: MatchState): Unit =
      panel.state = state
      panel.repaint()
