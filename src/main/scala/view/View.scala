package view

import scala.swing.*
import java.awt.{Color, Graphics2D}

import model.SimulationState

object View:

  class MatchPanel(var state: SimulationState) extends Panel:
    override def paintComponent(g: Graphics2D): Unit =
      super.paintComponent(g)
      g.setColor(Color.RED)
      g.fillOval(state.ball.position.x * 10, state.ball.position.y * 10, 10, 10)
      g.setColor(Color.blue)
      state.playerList.foreach(p =>
        g.fillRect(p.position.x * 10, p.position.y * 10, 10, 10)
      )

  class SwingView(initialState: SimulationState):
    private val panel = new MatchPanel(initialState)
    panel.background = Color.green
    private val frame = new MainFrame:
      title = "SCALcetto"
      contents = panel
      size = new Dimension(700, 700)
      centerOnScreen()
      visible = true

    def render(state: SimulationState): Unit =
      panel.state = state
      panel.repaint()
