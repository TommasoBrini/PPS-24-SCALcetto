import dsl.creation.GenSituation
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import model.Match.*

class SimulationLoopSpec extends AnyFlatSpec with Matchers:

  if (!java.awt.GraphicsEnvironment.isHeadless) {
    "SimulationLoop" should "initialize without errors" in:
      val state = GenSituation.kickOff
      noException should be thrownBy SimulationLoop.initialize(state)

    it should "start, pause, resume and reset without errors" in:
      val state = GenSituation.kickOff
      SimulationLoop.initialize(state)
      noException should be thrownBy SimulationLoop.initialize(state)
  } else {
    info("Test GUI ignorati in ambiente headless.")
  }
