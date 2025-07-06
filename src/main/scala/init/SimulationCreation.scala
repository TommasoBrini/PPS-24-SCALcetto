package init

import model.Match.{Match, Side}
import model.Match.Side.{East, West}
import dsl.creation.CreationSyntax.*
import dsl.creation.build.MatchBuilder
import dsl.creation.build.TeamBuilder
import model.Space.Direction

object SimulationCreation:
  val kickOff: Match =
    newMatch {
      team(West) withBall {
        player(9) at (15, 25) ownsBall true
        player(10) at (20, 30)
      }

      team(East) {
        player(11) at (85, 25)
        player(12) at (80, 40)
      }

      ball at (50, 25)
    }
  def corner(attackingTeam: Side, state: Match): Match  = ???
  def throwIn(attackingTeam: Side, state: Match): Match = ???
