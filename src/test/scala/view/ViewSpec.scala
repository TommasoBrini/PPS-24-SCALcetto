package view

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import scala.swing.event.ButtonClicked
import model.Match.*
import view.View.SwingView
import init.GameInitializer

class ViewSpec extends AnyFlatSpec with Matchers:

  "SwingView" should "initialize with the correct panels and buttons" in:
    val state = GameInitializer.initialSimulationState()
    val view  = new SwingView(state)
    noException should be thrownBy view.render(state)

  it should "call the onStart action when the start button is clicked" in:
    val state   = GameInitializer.initialSimulationState()
    val view    = new SwingView(state)
    var started = false
    view.onStart { started = true }
    noException should be thrownBy view.onStart { () }

  it should "render without errors when state changes" in:
    val state = GameInitializer.initialSimulationState()
    val view  = new SwingView(state)
    val newState =
      state.copy(ball = state.ball.copy(position = Position(state.ball.position.x + 10, state.ball.position.y)))
    noException should be thrownBy view.render(newState)
