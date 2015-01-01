package ee.tkasekamp.pliidikivi

import ee.tkasekamp.pliidikivi.util.Utilities
import scala.collection.mutable.Buffer
import ee.tkasekamp.pliidikivi.card.Card

/**
 * Holds methods that print out stuff
 */
object InputHelper {

  def inputLoop(): Int = {
    for (ln <- io.Source.stdin.getLines)
      (ln) match {
        case "help" => printHelp()
        case "info" => printInfo()
        case "field all" => printCardsInField()
        case "field my" => printMyCardsInField()
        case "hand" => printCardsInHand()
        case "end" =>
          println("Lõpetan käigu"); return 0
        case "" =>
          println("Käigu lõpetamiseks kirjuta end või 0")
        case _ => if (Utilities.isAllDigits(ln)) return ln.toInt else println("Ei saanud aru, kirjuta natuke paremini")
      }
    0
  }

  private def printHelp() {
    println("--- HELP --- \nNeid käske saab kasutada peaaegu alati.")
    println("end - lõpeta praegune käik")
    println("0 - lõpeta praegune käik")
    println("field all - näita kõiki praegu mängulaual asuvaid kaarte")
    println("field my - näita minu praegu mängulaual asuvaid kaarte")
    println("info - näita praeguse käigu infot")
    println("hand - näita praeguse mängija käes asuvaid kaarte")
    println("help - näita abi")

  }

  def printTurnStart() {
    val player = Game.getCurrentPlayer()
    printf(s"--- UUS KÄIK --- \nOn mängija ${player.name} kord\n")
    println(s"Tegutsemispunkte on ${player.actionPoints}")
  }

  def printPossibleCards() {
    val player = Game.getCurrentPlayer()

    val cards = player.possibleCards()
    if (cards.size == 0) {
      println("Tegutsemispunkte pole piisavalt. Võid käigu lõpetada")
    } else {
      println(s"Siin on kaardid, mida mängija ${player.name} võib praegu mänguväljale panna")
      cardsPrinter(cards)
    }

  }

  private def printMyCardsInField() {
    val player = Game.getCurrentPlayer()
    println(s"--- FIELD --- \n${player.name} kaardid väljakul")
    for { c <- Game.fieldCards if c.playerName == player.name }
      println(c)
  }

  private def printCardsInField() {
    println(s"--- FIELD --- \n Kõik kaardid väljakul")
    for { c <- Game.fieldCards }
      println(c)
  }

  private def printInfo() {
    val player = Game.getCurrentPlayer()
    println(s"--- INFO --- \n${player.name}")
    println(s"${player.actionPoints} tegutsemispunkti")
    println(s"Kangelase seis: ${player.getHero().toString()}")
  }

  private def printCardsInHand() {
    val player = Game.getCurrentPlayer()
    println(s"--- HAND --- \n${player.name} kaardid käes")
    for (c <- player.cardsInHand)
      println(c)
  }

  def cardsPrinter(cards: List[Card]) {
    var i = 1
    for (c <- cards) {
      print(s"$i - $c\n");
      i += 1
    }
  }
}