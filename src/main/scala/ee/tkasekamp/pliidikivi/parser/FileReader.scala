package ee.tkasekamp.pliidikivi.parser

import ee.tkasekamp.pliidikivi.card.Card
import scala.io.Source
import ee.tkasekamp.pliidikivi.game.Constants

object FileReader {

  def readCards(path: String): List[Card] = {
    val s = new StringBuilder
    for (line <- Source.fromFile(path).getLines()) {
      s.append(line)
    }

    val cardList = GameParser.doMatch(s.toString)
    cardList
  }

  def readPaths() {
    for (line <- Source.fromFile(Constants.PATH_FILE).getLines()) {
      val a = line.split('=')
      if (a(0) == "red") {
        Constants.RED_PATH = a(1)
      } else if (a(0) == "blue") {
        Constants.BLUE_PATH = a(1)
      }
    }
  }
}