package ee.tkasekamp.pliidikivi.card

import ee.tkasekamp.pliidikivi.card.enums.EffectType

class Effect(val effectType: EffectType.Value, val eventEffects: List[EventEffect]) {

  override def toString(): String = {
    "EffectType: " + effectType.toString() + ", eventEffects: " + eventEffects.mkString("[", ",", "]")
  }
}
