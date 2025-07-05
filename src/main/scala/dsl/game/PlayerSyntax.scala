package dsl.game

import model.Match.Player

object PlayerSyntax:
  extension (p: Player)
    def hasBall: Boolean = p.ball.isDefined
