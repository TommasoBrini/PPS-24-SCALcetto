package dsl.creation.build

import model.Match.*

import scala.collection.mutable.ListBuffer

/** Root builder in the creation DSL. It collects exactly two teams plus a ball and materialises an immutable
  * [[model.Match.Match]] once [[build]] is called.
  *
  * @param score
  *   initial scoreboard for the fixture
  */
final class MatchBuilder(score: Score):
  private val teams: ListBuffer[TeamBuilder] = ListBuffer[TeamBuilder]()
  private val matchBall: BallBuilder         = BallBuilder()

  /** Starts describing a team that defends the given side.
    *
    * @param side
    *   West or East
    * @return
    *   a fresh [[TeamBuilder]] tied to this match
    */
  def team(side: Side): TeamBuilder =
    val newTeam: TeamBuilder = TeamBuilder(side)
    teams += newTeam
    newTeam

  /** Provides access to the singleton [[BallBuilder]] associated with the match.
    *
    * @return
    *   the ball builder
    */
  def ball: BallBuilder = matchBall

  /** Validates the collected pieces and returns an immutable `Match`.
    *
    * @throws IllegalArgumentException
    *   if fewer or more than two teams supplied
    * @return
    *   the finished match object
    */
  def build(): Match =
    require(teams.size == 2, "exactly two teams required")
    Match((teams.head.build(), teams.last.build()), ball.build(), score)
