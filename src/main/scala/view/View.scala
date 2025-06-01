package view

import model.SimulationState

object View:
  def render(model: SimulationState): Unit =
    println(s" ball pos: ${model.ball.position}")
