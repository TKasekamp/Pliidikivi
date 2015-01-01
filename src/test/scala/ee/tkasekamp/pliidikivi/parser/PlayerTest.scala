package ee.tkasekamp.pliidikivi.parser
import org.scalatest.junit.AssertionsForJUnit
import scala.collection.mutable.ListBuffer
import org.junit.Assert._
import org.junit.Test
import org.junit.Before
import ee.tkasekamp.pliidikivi.card._
import ee.tkasekamp.pliidikivi.card.enums._
import ee.tkasekamp.pliidikivi.card.enums.MinionType._
import ee.tkasekamp.pliidikivi.card.enums.Type._
import scala.io.Source
import ee.tkasekamp.pliidikivi.game.Constants
import ee.tkasekamp.pliidikivi.game.Player

class PlayerTest extends AssertionsForJUnit {
  var red: Player = null;

  @Before def setUp() {
    val cardList = FileReader.readCards(Constants.TEST)
    red = new Player("Red", cardList.toBuffer)
  }

  @Test def possibleTest() {
    red.cardsInHand = red.cardPack

    val l = red.possibleCards()
    assertEquals(1, l.length)

    red.actionPoints = 4;
    val l1 = red.possibleCards()
    assertEquals(3, l1.length)

  }

  @Test def takeIntoHandTest() {
    red.takeCard()
    assertEquals(1, red.cardsInHand.length)
    assertEquals(2, red.cardPack.length)
  }
}