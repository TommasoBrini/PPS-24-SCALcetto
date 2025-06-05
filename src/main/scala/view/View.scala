package view

import scala.swing.*
import java.awt.{Color, Graphics2D}
import config.FieldConfig.*
import model.Model.SimulationState
import model.Model.Position

object View:

  private def drawCenteredOval(g: Graphics2D, position: Position, size: Int, color: Color): Unit =
    g.setColor(color)
    g.fillOval(
      position.x * scale - size / 2,
      position.y * scale - size / 2,
      size,
      size
    )

  private def drawCenteredRect(g: Graphics2D, position: Position, size: Int, color: Color): Unit =
    g.setColor(color)
    g.fillRect(
      position.x * scale - size / 2,
      position.y * scale - size / 2,
      size,
      size
    )

  class MatchPanel(var state: SimulationState) extends Panel:

    override def paintComponent(g: Graphics2D): Unit =
      super.paintComponent(g)

      g.translate(goalWidth * scale, 0)

      g.setColor(Color.GREEN)
      g.fillRect(0, 0, fieldWidth * scale, fieldHeight * scale)

      g.setColor(Color.WHITE)
      g.drawRect(0, 0, fieldWidth * scale - 1, fieldHeight * scale - 1)
      g.drawLine(fieldWidth * scale / 2, 0, fieldWidth * scale / 2, fieldHeight * scale)
      g.drawOval((fieldWidth * scale - 100)/ 2, (fieldHeight * scale - 100) / 2, 100, 100)

      g.drawRect(0, ((fieldHeight - goalAreaHeight) * scale) / 2, goalAreaWidth * scale, goalAreaHeight * scale)
      g.drawRect((fieldWidth - goalAreaWidth) * scale, (fieldHeight - goalAreaHeight) * scale / 2, goalAreaWidth * scale, goalAreaHeight * scale)
      drawCenteredOval(g, state.ball.position, ballSize, Color.WHITE)

      g.setColor(Color.black)
      g.fillRect(-(goalWidth * scale), (fieldHeight - goalHeight) * scale / 2, goalWidth * scale, goalHeight * scale)
      g.fillRect(fieldWidth * scale, (fieldHeight - goalHeight) * scale / 2, goalWidth * scale, goalHeight * scale)


      for team <- state.teams do
        val color = if team.id == 1 then Color.BLUE else Color.RED
        team.players.foreach(p => drawCenteredRect(g, p.position, playerSize, color))


  class SwingView(initialState: SimulationState):
    private val panel     = new MatchPanel(initialState)
    panel.preferredSize = new Dimension(
      (fieldWidth * scale) + 2 * (goalWidth * scale), fieldHeight * scale)
    private val infoLabel = new Label("SCALcetto - A simple soccer simulation")
    private val frame = new MainFrame:
      title = "SCALcetto"
      contents = new BorderPanel {
        layout(infoLabel) = BorderPanel.Position.North
        layout(panel) = BorderPanel.Position.Center
      }
      resizable = false
      centerOnScreen()
      visible = true

    def render(state: SimulationState): Unit =
      panel.state = state
      panel.repaint()
