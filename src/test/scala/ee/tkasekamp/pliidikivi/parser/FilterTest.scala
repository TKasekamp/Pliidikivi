package ee.tkasekamp.pliidikivi.parser
import org.scalatest.junit.AssertionsForJUnit
import scala.collection.mutable.ListBuffer
import org.junit.Assert._
import org.junit.Test
import org.junit.Before
import ee.tkasekamp.pliidikivi._
import ee.tkasekamp.pliidikivi.card._
import ee.tkasekamp.pliidikivi.card.enums._
import ee.tkasekamp.pliidikivi.card.enums.MinionType._
import ee.tkasekamp.pliidikivi.card.enums.Type._
import scala.io.Source
import ee.tkasekamp.pliidikivi.game.Constants
import ee.tkasekamp.pliidikivi.game.Player
import ee.tkasekamp.pliidikivi.card.Filter

class FilterTest extends AssertionsForJUnit {
  var cardList: List[Card] = null

  @Before def setUp() {
    cardList = FileReader.readCards(Constants.TEST)

  }

  @Test def filTest() {

    assertTrue(Game.filtersApply(cardList(0), List(new AnyCreatureFilter), cardList(1)))

    assertFalse(Game.filtersApply(cardList(0), List(new AnyHeroFilter), cardList(1)))

    assertTrue(Game.filtersApply(cardList(0), List(new AnyFriendlyFilter), cardList(1)))

    assertFalse(Game.filtersApply(cardList(0), List(new TypeFilter(MinionType.BEAST)), cardList(1)))

    assertTrue(Game.filtersApply(cardList(0), List(new AnyFilter(List(new AnyHeroFilter, new AnyCreatureFilter))), cardList(1)))

  }

}