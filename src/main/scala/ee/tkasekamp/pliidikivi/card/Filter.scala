package ee.tkasekamp.pliidikivi.card

import ee.tkasekamp.pliidikivi.card.enums.MinionType

abstract class Filter {

}

class AnyCreatureFilter() extends Filter {
  override def toString(): String = {
    "AnyCreature"
  }
}
class AnyHeroFilter() extends Filter {
  override def toString(): String = {
    "AnyHero"
  }
}
class AnyFriendlyFilter() extends Filter {
  override def toString(): String = {
    "AnyFriendly"
  }
}
class TypeFilter(val minionType: MinionType.Value) extends Filter {
  override def toString(): String = {
    "Type: " + minionType.toString()
  }

  def typeFilterHelp(card: Card, other: Card): Boolean = {
    if (other.cardType.isInstanceOf[MinionCard]) {
      val o = other.cardType.asInstanceOf[MinionCard]
      o.minionType == minionType
    } else
      false
  }
}
class SelfFilter() extends Filter {
  override def toString(): String = {
    "Self"
  }
}
class NotFilter(val filters: List[Filter]) extends Filter {
  override def toString(): String = {
    "Not: " + filters.mkString("[", ",", "]")
  }
}
class AnyFilter(val filters: List[Filter]) extends Filter {
  override def toString(): String = {
    "Any: " + filters.mkString("[", ",", "]")
  }
}