package ee.tkasekamp.pliidikivi

import ee.tkasekamp.pliidikivi.game.Player
import ee.tkasekamp.pliidikivi.parser.FileReader
import ee.tkasekamp.pliidikivi.game.Constants
import ee.tkasekamp.pliidikivi.card._
import java.util.Scanner
import scala.collection.mutable.Buffer
import ee.tkasekamp.pliidikivi.card.enums.EffectType
import scala.util.Random

object Game extends App {
  var gameOver = false;

  // Opposite to starting. Will be set to start values in first preTurn()  
  var currentTurnPoints = 0;
  var redPlayerTurn = false;
  var fieldCards: Buffer[Card] = Buffer()

  // players
  var red: Player = null;
  var blue: Player = null;

  // Main loop
  setup()
  while (!gameOver) {
    preTurn()
    turn(getCurrentPlayer())
    postTurn()

  }

  def turn(player: Player) {
    InputHelper.printTurnStart()
    println(player.takeCard)
    var cardId = -1;
    while (cardId != 0) {
      InputHelper.printPossibleCards()
      cardId = InputHelper.inputLoop()
      putToField(cardId, player)
    }

  }

  def putToField(cardId: Int, player: Player) {
    if (cardId == 0) {
      return
    }
    try {
      val card = player.moveCardFromHandToField(cardId)
      fieldCards += card
      println("Väljakule pandud kaart on: " + card.toString())
      filterEffects(card, EffectType.ONPLAY)
      filterEffects(card, EffectType.UNTILDEATH)
    } catch {
      case t: IndexOutOfBoundsException =>
    }

  }

  def filterEffects(card: Card, ef: EffectType.Value) {
    println(s"MÄNG - ${ef} kontroll")
    for { e <- card.cardType.effects if e.effectType == ef }
      matchEventEffects(card, e.eventEffects)
  }

  def matchEventEffects(card: Card, e: List[EventEffect]) {
    for (ef <- e)
      (ef) match {
        case ef: AllEventEffect => allCard(card, ef)
        case ef: ChooseEventEffect => chooseCard(card, ef.filters, ef.creatureEffects)
        case ef: RandomEventEffect => randomCard(card, ef.filters, ef.creatureEffects)
        case ef: DrawCardEventEffect => println(getCurrentPlayer().takeCard())

      }
  }

  /**
   * Deletes all cards that are dead
   */
  def cleanUp() {
    println("MÄNG - Koristus. Kustutan surnud kaardid")
    fieldCards = fieldCards.filter { x => x.cardType.alive }
  }

  /**
   * Check if all filters from card apply to other card. If other is dead, then they won't apply
   */
  def filtersApply(card: Card, filters: List[Filter], other: Card): Boolean = {
    var applies = true

    for (f <- filters) {
      if (f.isInstanceOf[AnyFilter]) {
        applies = applies || filterApplies(card, f, other)
      } else {
        applies = applies && filterApplies(card, f, other)
      }
    }
    applies && other.cardType.alive
  }

  private def filterApplies(card: Card, f: Filter, other: Card): Boolean = {
    (f) match {
      case f: AnyCreatureFilter => other.cardType.isInstanceOf[MinionCard]
      case f: AnyHeroFilter => other.cardType.isInstanceOf[HeroCard]
      case f: AnyFriendlyFilter => (card.playerName == other.playerName)
      case f: TypeFilter => f.typeFilterHelp(card, other)
      case f: SelfFilter => card.name == other.name && card.playerName == other.playerName
      case f: NotFilter => !filtersApply(card, f.filters, other)
      case f: AnyFilter => filtersApply(card, f.filters, other)
    }

  }

  def allCard(card: Card, ef: AllEventEffect) {
    var cards = fieldCards.filter(filtersApply(card, ef.filters, _))
    cards = tauntCheck(cards)
    for { other <- cards } other.applyEffect(ef.creatureEffects)
  }
  /**
   * Applies creatureEffects to a random card
   */
  def randomCard(card: Card, filters: List[Filter], cef: List[CreatureEffect]) = {
    val rand = new Random(System.currentTimeMillis());

    var cards = fieldCards.filter(filtersApply(card, filters, _))
    cards = tauntCheck(cards)
    if (cards.length != 0) {
      val random_index = rand.nextInt(cards.length);
      val other = cards(random_index)
      other.applyEffect(cef)
    }

    println("MÄNG - Random card lõpp")

  }
  /**
   * Lets the player choose a card
   */
  def chooseCard(card: Card, filters: List[Filter], cef: List[CreatureEffect]) = {
    println("Vali vastase kaart, millele efekt mõjub")

    var cards = fieldCards.filter(filtersApply(card, filters, _))
    cards = tauntCheck(cards)

    if (cards.length != 0) {
      InputHelper.cardsPrinter(cards.toList)

      var cardId = -1;
      while (cardId <= 0) {
        cardId = InputHelper.inputLoop()
        putToField(cardId, getCurrentPlayer())
      }
      val c = cards(cardId - 1)
      fieldCards.filter { x => x.name == c.name && x.playerName == c.playerName }(0).applyEffect(cef)

    } else {
      println("Pole kaarte, millele filter kehtiks")
    }
    println("Choose card lõpp")

  }

  def checkForUntilDeath() {
    println(s"MÄNG - UntilDeath check. Kõik ${getCurrentPlayer().name} kaardid")
    for { card <- fieldCards if card.playerName == getCurrentPlayer().name }
      filterEffects(card, EffectType.UNTILDEATH)
    println("MÄNG - UntilDeath check lõppenud")
    cleanUp()
  }

  /**
   * Switch players
   * Increase currentTurnActionPoints
   * Reset actionPoints for both players
   */
  def preTurn() {
    switchPlayers()
    if (redPlayerTurn)
      increaseCurrentTurnPoints()
    checkForUntilDeath()
    blue.actionPoints = currentTurnPoints
    red.actionPoints = currentTurnPoints
  }

  /**
   * Check if either player has won
   */
  def postTurn() {

    if (red.isDead() && !blue.isDead()) {
      println("Sinine võitis!")
      gameOver = true
    } else if (!red.isDead() && blue.isDead()) {
      println("Punane võitis!")
      gameOver = true
    } else if (red.isDead() && blue.isDead()) {
      println("Mõlemad on surnud.")
      gameOver = true
    }
    cleanUp()

  }

  /**
   * Create players
   */
  def setup() {
    FileReader.readPaths()
    red = new Player("Punane", FileReader.readCards(Constants.RED_PATH).toBuffer);
    blue = new Player("Sinine", FileReader.readCards(Constants.BLUE_PATH).toBuffer);
    fieldCards += new Card("Punane kangelane", 0, new HeroCard, "Punane")
    fieldCards += new Card("Sinine kangelane", 0, new HeroCard, "Sinine")
    red.setOwner()
    blue.setOwner()

  }

  private def switchPlayers() {
    redPlayerTurn = !redPlayerTurn
  }

  private def increaseCurrentTurnPoints() {
    if (currentTurnPoints < 10) {
      currentTurnPoints += 1
    }
  }

  def getCurrentPlayer(): Player = {
    if (redPlayerTurn)
      red
    else
      blue
  }

  private def tauntCheck(cards: Buffer[Card]): Buffer[Card] = {
    if (cards.exists { x => tauntPredicate(x) })
      cards.filter { x => tauntPredicate(x) }
    else
      cards
  }

  private def tauntPredicate(x: Card): Boolean = {
    x.cardType.isInstanceOf[MinionCard] && x.cardType.asInstanceOf[MinionCard].taunt
  }

}