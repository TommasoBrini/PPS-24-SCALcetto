package dsl.creation.build

import model.Match.*
import scala.collection.mutable.ListBuffer

final class TeamBuilder(side: Side):
  private val players: ListBuffer[PlayerBuilder] = ListBuffer[PlayerBuilder]()
  private var hasBall: Boolean                   = false

  def withBall: TeamBuilder = {
    hasBall = true
    this
  }

  def player(id: ID): PlayerBuilder =
    val player = PlayerBuilder(id)
    players += player
    player

  def apply(body: TeamBuilder ?=> Unit): TeamBuilder =
    body(using this)
    this

  def build(): Team = Team(players.map(_.build()).toList, side, hasBall)
