package update.decide.behaviours

import model.Match.*
import model.Match.Decision.Initial
import model.player.Player

trait PlayerBehavior:
  def decide(player: Player, matchState: MatchState): Decision
