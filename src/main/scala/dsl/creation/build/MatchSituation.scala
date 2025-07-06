package dsl.creation.build

import model.Match.*

sealed trait MatchSituation:
  def apply(state: Match): Match

object MatchSituation {}
