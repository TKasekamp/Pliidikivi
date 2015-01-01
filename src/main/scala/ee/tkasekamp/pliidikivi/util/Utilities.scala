package ee.tkasekamp.pliidikivi.util

object Utilities {

  def isAllDigits(x: String): Boolean = x.forall(_.isDigit)
}