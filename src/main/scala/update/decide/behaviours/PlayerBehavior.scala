package update.decide.behaviours

import model.Match.*

trait PlayerBehavior:
  def decide(player: Player, matchState: MatchState): Decision
