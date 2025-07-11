package dsl.game

import model.Match.Score

object ScoreSyntax:
  extension (score: Score)
    def westGoal: Score = Score(score.westScore + 1, score.eastScore)
    def eastGoal: Score = Score(score.westScore, score.eastScore + 1)
