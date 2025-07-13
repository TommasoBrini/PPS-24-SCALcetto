package dsl.creation.build

import model.Match.*
import scala.collection.mutable.ListBuffer

/** Mutable **builder** that assembles a `Team` for one side of the pitch. The class is meant to be used from the
  * creation DSL:
  *
  * team(West) withBall : player(1) at (3,4)
  *
  * All mutability is confined to the builder; call [[build]] to obtain the immutable `Team`.
  *
  * @param side
  *   the half (`West` / `East`) the team will defend
  */
final class TeamBuilder(side: Side):
  private val players: ListBuffer[PlayerBuilder] = ListBuffer[PlayerBuilder]()
  private var hasBall: Boolean                   = false

  /** Flags this team as the one that starts in possession of the ball.
    *
    * The call is chain-able and can wrap a configuration block:
    *
    * team(West) withBall
    *
    * @return
    *   this builder for fluent invocation
    */
  def withBall: TeamBuilder =
    hasBall = true
    this

  /** Adds a new player to the team and returns a dedicated [[PlayerBuilder]] so that the caller can further configure
    * the player.
    *
    * @param id
    *   identifier unique within the scenario
    * @return
    *   a mutable builder for the freshly created player
    */
  def player(id: ID): PlayerBuilder =
    val player = PlayerBuilder(id)
    players += player
    player

  /** Syntactic sugar enabling
    *
    * team(West): … // inside the TeamBuilder scope
    *
    * @param body
    *   block executed with `this` as the implicit receiver
    * @return
    *   this builder for method chaining
    */
  def apply(body: TeamBuilder ?=> Unit): TeamBuilder =
    body(using this)
    this

  /** Finalises construction and returns an immutable `Team`.
    *
    * @return
    *   the fully configured team
    */
  def build(): Team = Team(players.map(_.build()).toList, side, hasBall)
