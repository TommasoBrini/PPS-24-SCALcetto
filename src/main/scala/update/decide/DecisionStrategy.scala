package update.decide

import model.Match.*

trait DecisionStrategy:
  def decide(player: Player, matchState: MatchState): Decision
