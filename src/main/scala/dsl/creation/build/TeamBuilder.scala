package dsl.creation.build

import model.Match.*

case class TeamBuilder(
    side: Side,
    private val players: List[Player] = List.empty,
    private val hasBall: Boolean = false
):
  def withPlayer(newPlayer: Player): TeamBuilder =
    copy(players = players :+ newPlayer)

  def withPlayers(newPlayers: List[Player]): TeamBuilder =
    copy(players = players ++ newPlayers)

  def havingBall(has: Boolean = true): TeamBuilder =
    copy(hasBall = has)

  def build(): Team = Team(players, side, hasBall)
