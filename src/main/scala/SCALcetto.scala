import model.Match.*
import SimulationLoop.*
import dsl.creation.SituationGenerator

object SCALcetto:

  /** release 3.2.0
    */
  def main(args: Array[String]): Unit =
    app()

  @main def app(): Unit =

    val initialState: MatchState = SituationGenerator.kickOff(Score.init())

    initialize(initialState, 50)
