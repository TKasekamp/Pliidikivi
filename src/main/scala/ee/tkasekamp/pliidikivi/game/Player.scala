package ee.tkasekamp.pliidikivi.game

import ee.tkasekamp.pliidikivi.card.Card
import ee.tkasekamp.pliidikivi.Game
import scala.util.Random
import scala.collection.mutable.MutableList
import scala.collection.mutable.Buffer
import ee.tkasekamp.pliidikivi.card.HeroCard
import ee.tkasekamp.pliidikivi.card.HeroCard

class Player(val name: String, var cardPack: Buffer[Card]) {
  var actionPoints = 1;
  var cardsInHand: Buffer[Card] = Buffer()

  def possibleCards(): List[Card] = {
    cardsInHand.filter(x => x.cost <= actionPoints && !x.cardType.isInstanceOf[HeroCard]).toList
  }

  /**
   * Take random card from cardPack into Hand.
   */
  def takeCard(): String = {
    val rand = new Random(System.currentTimeMillis());
    if (cardPack.length == 0) {
      getHero().health -= 10
      return "Kaardipakk tühi, ei saanud võtta"
    }
    val random_index = rand.nextInt(cardPack.length);
    val result = cardPack.remove(random_index);

    cardsInHand += result
    "Võetud kaart on " + result.toString()
  }

  def moveCardFromHandToField(cardId: Int): Card = {
    val possible = possibleCards()
    val posName = possible(cardId - 1).name

    val card = cardsInHand.find { x => x.name == posName }

    cardsInHand -= card.get
    card.get.playerName = name
    actionPoints -= card.get.cost
    card.get

  }

  def isDead(): Boolean = {
    if (getHero().health <= 0)
      true
    else
      false
  }

  def getHero(): HeroCard = {
    Game.fieldCards.filter { x => x.cardType.isInstanceOf[HeroCard] && x.playerName == name }(0).cardType.asInstanceOf[HeroCard]
  }

  def setOwner() {
    cardPack.foreach { x => x.playerName = name }
  }
}