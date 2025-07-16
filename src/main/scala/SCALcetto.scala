import model.Match.*
import SimulationLoop.*
import dsl.creation.SituationGenerator

object SCALcetto:

  def main(args: Array[String]): Unit =
    app()

  @main def app(): Unit =

    val initialState: Match = SituationGenerator.kickOff(Score.init())

    initialize(initialState, 50)
