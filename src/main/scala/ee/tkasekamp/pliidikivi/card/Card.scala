package ee.tkasekamp.pliidikivi.card

import ee.tkasekamp.pliidikivi.Game
import ee.tkasekamp.pliidikivi.card.enums.EffectType

class Card(val name: String, val cost: Int, val cardType: CardType, var playerName: String = "") {

  override def toString(): String = {
    "Card: " + name + ", ownerName: " + playerName + ", cost: " + cost + ", cardType : " + cardType
  }

  /**
   * Apply the creatureEffects. If the card has ONDEATH or ONDAMAGE effects activated, then apply them to all the other cards.
   */
  def applyEffect(cList: List[CreatureEffect]) {
    for (creatureEffect <- cList) {
      val value = cardType.applyEffect(creatureEffect)
      value match {
        case 1 => Game.filterEffects(this, EffectType.ONDAMAGE)
        case 2 => Game.filterEffects(this, EffectType.ONDEATH)
        case 3 =>
          Game.filterEffects(this, EffectType.ONDAMAGE); Game.filterEffects(this, EffectType.ONDEATH)
        case 0 =>
      }
    }
  }
}