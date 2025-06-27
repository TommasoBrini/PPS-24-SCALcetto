package model.decisions

import model.Match.*
import model.player.Player

trait CanDecideToRun:
  def decideRun(direction: Direction): Decision = Decision.Run(direction)

trait CanDecideConfusion:
  def decideConfusion(step: Int): Decision = Decision.Confusion(remainingStep = step)

trait CanDecideToPass:
  def decidePass(to: Player): Decision = Decision.Pass(this.asInstanceOf[Player], to)

trait CanDecideToShoot:
  def decideShoot(goal: Position): Decision = Decision.Shoot(this.asInstanceOf[Player], goal)

trait CanDecideToMoveToGoal:
  def decideMoveToGoal(direction: Direction): Decision = Decision.MoveToGoal(direction)

trait CanDecideToMark:
  def decideMark(target: Player): Decision = Decision.Mark(this.asInstanceOf[Player], target)

trait CanDecideToTackle:
  def decideTackle(ball: Ball): Decision = Decision.Tackle(ball)

trait CanDecideToIntercept:
  def decideIntercept(ball: Ball): Decision = Decision.Intercept(ball)

trait CanDecideToMoveRandom:
  def decideMoveRandom(direction: Direction, steps: Int): Decision =
    Decision.MoveRandom(direction, steps)

trait CanDecideToReceivePass:
  def decideReceivePass(ball: Ball): Decision = Decision.ReceivePass(ball)

trait CanDecideToMoveToBall:
  def decideMoveToBall(directionToBall: Direction): Decision = Decision.MoveToBall(directionToBall)
