package dsl.`match`

import model.Match.Score

/** **Extension methods** that change the match score in an expressive, immutable football-centric way.
  */
object ScoreSyntax:
  extension (score: Score)
    /** Records a goal for the **West** team and returns the updated score. */
    def westGoal: Score = Score(score.westScore + 1, score.eastScore)

    /** Records a goal for the **East** team and returns the updated score. */
    def eastGoal: Score = Score(score.westScore, score.eastScore + 1)
