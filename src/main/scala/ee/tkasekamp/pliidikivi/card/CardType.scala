package ee.tkasekamp.pliidikivi.card

import ee.tkasekamp.pliidikivi.card.enums.MinionType
import ee.tkasekamp.pliidikivi.card.enums.Type
import ee.tkasekamp.pliidikivi.card.enums.EffectType

abstract class CardType(val effects: List[Effect]) {
  var alive = true
  def applyEffect(creatureEffect: CreatureEffect): Int
}

class MinionCard(override val effects: List[Effect], var health: Int, var attack: Int, var taunt: Boolean, val minionType: MinionType.Value) extends CardType(effects) {

  override def toString(): String = {
    "MinionCard. Effects: " + effects.mkString("[", ",", "]") + ", health: " + health + ", attack: " + attack + ", taunt: " + taunt + ", minionType: " + minionType
  }

  /**
   * Apply the creatureEffect. Returns an int, by default 0.
   */
  override def applyEffect(creatureEffect: CreatureEffect): Int = {
    if (alive) {
      (creatureEffect) match {
        case creatureEffect: HealthCreatureEffect =>
          health = creatureEffect.getNewValue(health); checkStuff(creatureEffect)
        case creatureEffect: AttackCreatureEffect =>
          attack = creatureEffect.getNewValue(attack); 0
        case creatureEffect: TauntCreatureEffect => taunt = creatureEffect.taunt; 0
      }
    } else 0
  }

  /**
   * Checks if any ondamage or untildeath effects have to be activated.
   */
  private def checkStuff(cef: HealthCreatureEffect): Int = {
    if (checkForDeath() && checkForDamage(cef)) {
      3 // Dead and damage effects
    } else if (checkForDeath()) {
      2 // Dead effects
    } else if (checkForDamage(cef)) {
      1 // Damage effects
    } else
      0 // No effect
  }

  /**
   * Check if this card is dead and if it has a ONDEATH effect.
   */
  private def checkForDeath(): Boolean = {
    if (health <= 0 && effects.filter { x => x.effectType == EffectType.ONDEATH }.length != 0) {
      alive = false
      true
    } else if (health <= 0) {
      alive = false
      false
    } else
      false
  }

  /**
   * Check if the card was damaged and if it has a ONDAMAGE effect.
   */
  private def checkForDamage(cef: HealthCreatureEffect): Boolean = {
    if (cef.effectType == Type.RELATIVE && effects.filter { x => x.effectType == EffectType.ONDAMAGE }.length != 0)
      true
    else
      false
  }
}

class SpellCard(override val effects: List[Effect]) extends CardType(effects) {
  override def toString(): String = {
    "SpellCard. Effects: " + effects.mkString("[", ",", "]")
  }

  override def applyEffect(creatureEffect: CreatureEffect): Int = {
    0
  }
}

class HeroCard() extends CardType(List()) {
  var health = 30;

  override def toString(): String = {
    "HeroCard. Health: " + health
  }

  override def applyEffect(creatureEffect: CreatureEffect): Int = {
    (creatureEffect) match {
      case creatureEffect: HealthCreatureEffect =>
        health = creatureEffect.getNewValue(health); 0
      case _ => 0
    }
  }
}