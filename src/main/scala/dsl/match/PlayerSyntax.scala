package dsl.`match`

import model.Match.Player

/** Lightweight syntax that answers the common question “**does this player currently possess the ball?**”.
  */
object PlayerSyntax:
  extension (p: Player)
    /** `true` when `p.ball` is defined. */
    def hasBall: Boolean = p.ball.isDefined
