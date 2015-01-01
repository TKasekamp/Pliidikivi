package ee.tkasekamp.pliidikivi.card.enums

object EffectType extends Enumeration {
  type EffectType = Value
  val ONPLAY, UNTILDEATH, ONDAMAGE, ONDEATH = Value
}