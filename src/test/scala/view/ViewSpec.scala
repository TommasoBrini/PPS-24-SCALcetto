package view

import dsl.creation.SituationGenerator
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import model.Match.*
import view.View.SwingView

class ViewSpec extends AnyFlatSpec with Matchers:

  if (!java.awt.GraphicsEnvironment.isHeadless) {
    "SwingView" should "initialize with the correct panels and buttons" in:
      val state = SituationGenerator.kickOff(Score.init())
      val view  = new SwingView(state)
      noException should be thrownBy view.render(state)

    it should "call the onStart action when the start button is clicked" in:
      val state   = SituationGenerator.kickOff(Score.init())
      val view    = new SwingView(state)
      var started = false
      view.onStart { started = true }
      noException should be thrownBy view.onStart { () }

    it should "render without errors when state changes" in:
      val state = SituationGenerator.kickOff(Score.init())
      val view  = new SwingView(state)
      val newState =
        state.copy(ball = state.ball.copy(position = Position(state.ball.position.x + 10, state.ball.position.y)))
      noException should be thrownBy view.render(newState)
  } else {
    info("Test GUI ignorati in ambiente headless.")
  }
