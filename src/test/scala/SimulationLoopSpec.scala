import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import model.Match.*
import init.GameInitializer

class SimulationLoopSpec extends AnyFlatSpec with Matchers:

  "SimulationLoop" should "initialize without errors" in:
    val state = GameInitializer.initialSimulationState()
    noException should be thrownBy SimulationLoop.initialize(state)

  it should "start, pause, resume and reset without errors" in:
    val state = GameInitializer.initialSimulationState()
    SimulationLoop.initialize(state)
    noException should be thrownBy SimulationLoop.initialize(state)
