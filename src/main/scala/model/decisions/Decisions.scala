package model.decisions

import model.Match.*
import model.player.Player

trait CanDecideToRun:
  self: Player =>
  def decideRun(direction: Direction): Decision = Decision.Run(direction)

trait CanDecideConfusion:
  self: Player =>
  def decideConfusion(step: Int): Decision = Decision.Confusion(remainingStep = step)

trait CanDecideToPass:
  self: Player =>
  def decidePass(to: Player): Decision = Decision.Pass(this, to)

trait CanDecideToShoot:
  self: Player =>
  def decideShoot(goal: Position): Decision = Decision.Shoot(this, goal)

trait CanDecideToMoveToGoal:
  self: Player =>
  def decideMoveToGoal(direction: Direction): Decision = Decision.MoveToGoal(direction)

trait CanDecideToMark:
  self: Player =>
  def decideMark(target: Player): Decision = Decision.Mark(this, target)

trait CanDecideToTackle:
  self: Player =>
  def decideTackle(ball: Ball): Decision = Decision.Tackle(ball)

trait CanDecideToIntercept:
  self: Player =>
  def decideIntercept(ball: Ball): Decision = Decision.Intercept(ball)

trait CanDecideToMoveRandom:
  self: Player =>
  def decideMoveRandom(direction: Direction, steps: Int): Decision =
    Decision.MoveRandom(direction, steps)

trait CanDecideToReceivePass:
  self: Player =>
  def decideReceivePass(ball: Ball): Decision = Decision.ReceivePass(ball)

trait CanDecideToMoveToBall:
  self: Player =>
  def decideMoveToBall(directionToBall: Direction): Decision = Decision.MoveToBall(directionToBall)
