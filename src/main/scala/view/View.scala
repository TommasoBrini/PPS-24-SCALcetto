package view

import scala.swing.*
import scala.swing.event.*
import java.awt.geom.{Ellipse2D, Line2D, Rectangle2D}
import config.UIConfig.*
import model.Match.*
import model.Match.Position
import view.RenderUtils.*
import java.awt.BasicStroke
import dsl.SpaceSyntax.*

object View:
  class MatchPanel(var state: Match) extends Panel:
    background = Colors.backgroundColor

    override def paintComponent(g: Graphics2D): Unit =
      super.paintComponent(g)
      setupGraphics(g)

      val centerX      = size.width / 2
      val centerY      = size.height / 2
      val fieldOffsetX = centerX - fieldWidth / 2
      val fieldOffsetY = centerY - fieldHeight / 2

      g.translate(fieldOffsetX, fieldOffsetY)

      g.setColor(Colors.fieldGreen)
      g.fillRect(0, 0, fieldWidth, fieldHeight)

      g.setColor(Colors.fieldLines)
      g.setStroke(new BasicStroke(Drawing.fieldBorderWidth))

      g.drawLine(0, 0, fieldWidth, 0)
      g.drawLine(0, 0, 0, fieldHeight)
      g.drawLine(fieldWidth, 0, fieldWidth, fieldHeight)
      g.drawLine(0, fieldHeight, fieldWidth, fieldHeight)

      g.drawLine(fieldWidth / 2, 0, fieldWidth / 2, fieldHeight)

      val circleSize = 100
      val circleX    = (fieldWidth - circleSize) / 2
      val circleY    = (fieldHeight - circleSize) / 2
      g.drawOval(circleX, circleY, circleSize, circleSize)

      g.drawRect(0, (fieldHeight - goalAreaHeight) / 2, goalAreaWidth, goalAreaHeight)
      g.drawRect(
        fieldWidth - goalAreaWidth,
        (fieldHeight - goalAreaHeight) / 2,
        goalAreaWidth,
        goalAreaHeight
      )

      drawCenteredOval(g, state.ball.position, ballSize, Colors.ballColor, Drawing.ballBorderWidth)

      g.setColor(Colors.goalColor)
      g.fillRect(-goalWidth, (fieldHeight - goalHeight) / 2, goalWidth, goalHeight)
      g.fillRect(fieldWidth, (fieldHeight - goalHeight) / 2, goalWidth, goalHeight)

      val teamA  = state.teams.teamA
      val colorA = Colors.teamBlue
      teamA.players.foreach { player =>
        drawCenteredRect(g, player.position, playerSize, colorA, Drawing.playerBorderWidth)
      }

      val teamB  = state.teams.teamB
      val colorB = Colors.teamRed
      teamB.players.foreach { player =>
        drawCenteredRect(g, player.position, playerSize, colorB, Drawing.playerBorderWidth)
      }

      g.translate(-fieldOffsetX, -fieldOffsetY)

  class InfoPanel extends Panel:
    background = Colors.infoPanelColor
    border = Swing.EmptyBorder(10)

    override def paintComponent(g: Graphics2D): Unit =
      super.paintComponent(g)
      setupGraphics(g)

      val yOffset = 20
      drawText(g, "SCALcetto - Football Simulation", 10, yOffset, Colors.textColor, titleFont.getSize)

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

  class SwingView(initialState: Match):
    private val panel: MatchPanel    = new MatchPanel(initialState)
    private val infoPanel: InfoPanel = new InfoPanel()

    panel.preferredSize = new Dimension(fieldPanelWidth, fieldPanelHeight)
    infoPanel.preferredSize = new Dimension(fieldPanelWidth, infoPanelHeight)

    private val startButton: StyledButton  = new StyledButton("▶ Start")
    private val pauseButton: StyledButton  = new StyledButton("⏸ Pause")
    private val resumeButton: StyledButton = new StyledButton("▶ Resume")
    private val resetButton: StyledButton  = new StyledButton("🔄 Reset")

    pauseButton.enabled = false
    resumeButton.enabled = false

    private val controlPanel: Panel = new FlowPanel:
      contents ++= Seq(
        startButton,
        pauseButton,
        resumeButton,
        resetButton
      )
      border = Swing.EmptyBorder(10)
      background = Colors.backgroundColor

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

    def render(state: Match): Unit =
      panel.state = state
      panel.repaint()
