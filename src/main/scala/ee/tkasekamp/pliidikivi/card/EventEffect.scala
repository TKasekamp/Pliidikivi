package ee.tkasekamp.pliidikivi.card

abstract class EventEffect {

}

class AllEventEffect(val filters: List[Filter], val creatureEffects: List[CreatureEffect]) extends EventEffect {
  override def toString(): String = {
    "ALL. Filters: " + filters.mkString("[", ",", "]") + ", CreatureEffects: " + creatureEffects.mkString("[", ",", "]")
  }
}
class ChooseEventEffect(val filters: List[Filter], val creatureEffects: List[CreatureEffect]) extends EventEffect {
  override def toString(): String = {
    "CHOOSE. Filters: " + filters.mkString("[", ",", "]") + ", CreatureEffects: " + creatureEffects.mkString("[", ",", "]")
  }
}
class RandomEventEffect(val filters: List[Filter], val creatureEffects: List[CreatureEffect]) extends EventEffect {
  override def toString(): String = {
    "RANDOM. Filters: " + filters.mkString("[", ",", "]") + ", CreatureEffects: " + creatureEffects.mkString("[", ",", "]")
  }
}
class DrawCardEventEffect extends EventEffect {
  override def toString(): String = {
    "DrawCard"
  }
}