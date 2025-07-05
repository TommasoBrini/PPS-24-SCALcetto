package update.decide.behaviours

import model.Match.*

trait PlayerBehavior:
  def decide(player: Player, matchState: Match): Decision
