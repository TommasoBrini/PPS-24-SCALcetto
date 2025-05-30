package try_CI

trait Try_CI:
  def n(): Int

object Try_CI_Impl extends Try_CI:
  override def n(): Int = 1

var x = 0; var y = 1;
