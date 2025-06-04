package view

import scala.swing.*
import java.awt.{Color, Graphics2D}

import model.SimulationState

object View:

  class MatchPanel(var state: SimulationState) extends Panel:
    preferredSize = new Dimension(600, 400)
    override def paintComponent(g: Graphics2D): Unit =
      super.paintComponent(g)

      val width  = size.width
      val height = size.height

      g.setColor(Color.GREEN)
      g.fillRect(0, 0, width, height)

      g.setColor(Color.WHITE)
      val lineThickness = 5
      g.fillRect(0, 0, width, lineThickness)
      g.fillRect(0, height - lineThickness, width, lineThickness)
      g.fillRect(0, 0, lineThickness, height)
      g.fillRect(width - lineThickness, 0, lineThickness, height)
      g.drawLine(width / 2, 0, width / 2, height)
      g.drawOval(width / 2 - 50, height / 2 - 50, 100, 100)

      g.setColor(Color.black)
      val goalHeight = 50
      val goalWidth  = 10
      g.fillRect(0, (height - goalHeight) / 2, goalWidth, goalHeight)
      g.fillRect(width - goalWidth, (height - goalHeight) / 2, goalWidth, goalHeight)

      g.setColor(Color.white)
      val areaWidth  = 60
      val areaHeight = 200
      g.drawRect(0, (height - areaHeight) / 2, areaWidth, areaHeight)                 // Area rigore sinistra
      g.drawRect(width - areaWidth, (height - areaHeight) / 2, areaWidth, areaHeight) // Area rigore destra

      g.setColor(Color.WHITE)
      g.fillOval(state.ball.position.x * 10, state.ball.position.y * 10, 10, 10)

      g.setColor(Color.blue)
      state.teams.foreach(t =>
        t.players.foreach(p =>
          g.fillRect(p.position.x * 10, p.position.y * 10, 10, 10)
        )
      )

  class SwingView(initialState: SimulationState):
    private val panel     = new MatchPanel(initialState)
    private val infoLabel = new Label("SCALcetto - A simple soccer simulation")
    private val frame = new MainFrame:
      title = "SCALcetto"
      contents = new BorderPanel {
        layout(infoLabel) = BorderPanel.Position.North
        layout(panel) = BorderPanel.Position.Center
      }
      size = new Dimension(600, 450)
      resizable = false
      centerOnScreen()
      visible = true

    def render(state: SimulationState): Unit =
      panel.state = state
      panel.repaint()
