package ee.tkasekamp.pliidikivi.parser
import org.scalatest.junit.AssertionsForJUnit
import scala.collection.mutable.ListBuffer
import org.junit.Assert._
import org.junit.Test
import org.junit.Before
import ee.tkasekamp.pliidikivi.card._
import ee.tkasekamp.pliidikivi.card.enums._
import ee.tkasekamp.pliidikivi.card.enums.MinionType._
import ee.tkasekamp.pliidikivi.card.enums.Type._
import scala.io.Source
import ee.tkasekamp.pliidikivi.game.Constants

class GameParserTest extends AssertionsForJUnit {

  @Test def filterTest() {
    val f1 = GameParser.filterMatch("AnyCreature")
    assertTrue(f1.isInstanceOf[AnyCreatureFilter])

    val f2 = GameParser.filterMatch("AnyHero")
    assertTrue(f2.isInstanceOf[AnyHeroFilter])

    val f3 = GameParser.filterMatch("AnyFriendly")
    assertTrue(f3.isInstanceOf[AnyFriendlyFilter])

    val f4 = GameParser.filterMatch("Type Murloc")
    assertTrue(f4.isInstanceOf[TypeFilter])
    f4 match {
      case f4: TypeFilter => assertEquals(MinionType.MURLOC, f4.minionType)
      case _ => throw new Exception("this is bad")
    }

    val f5 = GameParser.filterMatch("Self")
    assertTrue(f5.isInstanceOf[SelfFilter])

    val f6 = GameParser.filterMatch("Any [Self]")
    assertTrue(f6.isInstanceOf[AnyFilter])
    f6 match {
      case f6: AnyFilter => assertTrue(f6.filters.head.isInstanceOf[SelfFilter])
      case _ => throw new Exception("this is bad")
    }

  }

  @Test def creatureEffectTest() {
    val f1 = GameParser.creatureEffectMatch("Health Relative 3")
    assertTrue(f1.isInstanceOf[HealthCreatureEffect])

    f1 match {
      case f1: HealthCreatureEffect => { assertEquals(Type.RELATIVE, f1.effectType); assertEquals(3, f1.health) }
    }

    val f2 = GameParser.creatureEffectMatch("Attack Absolute (-5)")
    assertTrue(f2.isInstanceOf[AttackCreatureEffect])

    f2 match {
      case f2: AttackCreatureEffect => { assertEquals(Type.ABSOLUTE, f2.effectType); assertEquals(-5, f2.attack) }
    }

    val f3 = GameParser.creatureEffectMatch("Taunt True")
    assertTrue(f3.isInstanceOf[TauntCreatureEffect])

    f3 match {
      case f3: TauntCreatureEffect => assertTrue(f3.taunt)
    }

  }

  @Test def eventEffectTest() {

    val f1 = GameParser.eventEffectMatch("Choose [ ][Health Relative 2 ]")
    assertTrue(f1.isInstanceOf[ChooseEventEffect])
    f1 match {
      case f1: ChooseEventEffect => { assertTrue(f1.filters.isEmpty); assertTrue(f1.creatureEffects.head.isInstanceOf[HealthCreatureEffect]) }
    }

    val f2 = GameParser.eventEffectMatch("All [Self, AnyHero][Taunt False]")
    assertTrue(f2.isInstanceOf[AllEventEffect])
    f2 match {
      case f2: AllEventEffect => { assertEquals(2, f2.filters.size); assertTrue(f2.creatureEffects.head.isInstanceOf[TauntCreatureEffect]) }
    }

    val f3 = GameParser.eventEffectMatch("DrawCard")
    assertTrue(f3.isInstanceOf[DrawCardEventEffect])
  }

  @Test def effectTest() {
    val f1 = GameParser.effectMatch("OnPlay [Choose [] [Health Relative (-1)]]")
    assertEquals(EffectType.ONPLAY, f1.effectType)
    assertTrue(f1.eventEffects.head.isInstanceOf[ChooseEventEffect])
  }

  @Test def cardTypeTest() {
    val f1 = GameParser.cardTypeMatch("MinionCard [] 6 7 False Nothing")
    assertTrue(f1.isInstanceOf[MinionCard])
    assertTrue(f1.effects.isEmpty)
    val f1p = f1.asInstanceOf[MinionCard]
    assertEquals(6, f1p.health)
    assertEquals(7, f1p.attack)
    assertFalse(f1p.taunt)
    assertNull(f1p.minionType)

    val f2 = GameParser.cardTypeMatch("MinionCard [] 6 7 False Just Beast")
    assertTrue(f2.isInstanceOf[MinionCard])
    assertTrue(f2.effects.isEmpty)
    f2 match {
      case f2: MinionCard => assertEquals(MinionType.BEAST, f2.minionType)
    }

    val f3 = GameParser.cardTypeMatch("SpellCard [OnPlay [DrawCard]]")
    assertTrue(f3.isInstanceOf[SpellCard])
    assertEquals(EffectType.ONPLAY, f3.effects.head.effectType)
    assertTrue(f3.effects.head.eventEffects.head.isInstanceOf[DrawCardEventEffect])

  }

  @Test def fullTest() {
    val filename = Constants.TEST
    val s = new StringBuilder
    for (line <- Source.fromFile(filename).getLines()) {
      s.append(line)
    }

    val cardList = GameParser.doMatch(s.toString)
    assertEquals(3, cardList.size)

    assertEquals("Boulderfist Ogre", cardList(0).name)

    assertEquals(4, cardList(1).cost)

    assertTrue(cardList(2).cardType.isInstanceOf[MinionCard])

  }
}