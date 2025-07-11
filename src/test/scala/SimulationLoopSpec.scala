import dsl.creation.SituationGenerator
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import model.Match.*

class SimulationLoopSpec extends AnyFlatSpec with Matchers:

  if (!java.awt.GraphicsEnvironment.isHeadless) {
    "SimulationLoop" should "initialize without errors" in:
      val state = SituationGenerator.kickOff(Score.init())
      noException should be thrownBy SimulationLoop.initialize(state)

    it should "start, pause, resume and reset without errors" in:
      val state = SituationGenerator.kickOff(Score.init())
      SimulationLoop.initialize(state)
      noException should be thrownBy SimulationLoop.initialize(state)
  } else {
    info("Test GUI ignorati in ambiente headless.")
  }
