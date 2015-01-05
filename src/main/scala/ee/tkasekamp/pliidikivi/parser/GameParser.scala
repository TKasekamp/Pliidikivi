package ee.tkasekamp.pliidikivi.parser

import scala.util.parsing.combinator.syntactical.StandardTokenParsers
import ee.tkasekamp.pliidikivi.card._
import ee.tkasekamp.pliidikivi.card.enums._
import ee.tkasekamp.pliidikivi.card.enums.MinionType._
import ee.tkasekamp.pliidikivi.card.enums.Type._
/*
 * Parser done using Scala built-in tokenparser. This makes it look a lot clearer, but also a bit more "magic".
 */
object GameParser extends StandardTokenParsers {
  lexical.delimiters ++= List("(", ")", "[", "]", ",", "-")
  lexical.reserved += ("MinionCard", "SpellCard", "Just", "Nothing", "Beast", "Murloc", "OnPlay", "UntilDeath", "OnDamage", "OnDeath", "All", "Choose", "Random", "DrawCard", "Health", "Attack", "Taunt", "Relative", "Absolute", "AnyCreature", "AnyHero", "AnyFriendly", "Type", "Self", "Not", "Any", "True", "False")

  def file: Parser[List[Card]] = "[" ~> repsep(card, ",") <~ "]" ^^ { case c => c }

  def card: Parser[Card] = "(" ~> stringLit ~ "," ~ numericLit ~ "," ~ cardType <~ ")" ^^ {
    case n ~ "," ~ c ~ "," ~ ct => { new Card(n, c.toInt, ct) }
  }

  // elupunktide arv
  def health: Parser[String] = number

  // ründepunktide arv
  def attack: Parser[String] = number

  /**
   * Getting this right took about 1 hour. Yay me
   */
  def number: Parser[String] = (numericLit | "(" ~> "-" ~ numericLit <~ ")") ^^ {
    case "-" ~ a => { "-" + a.toString }
    case a => a.toString
  }

  // mõnitav olend
  def taunt: Parser[Boolean] = ("True" | "False") ^^ {
    case "True" => true
    case "False" => false
  }

  def cardType: Parser[CardType] = ("MinionCard" ~ "[" ~ repsep(effect, ",") ~ "]" ~ health ~ attack ~ taunt ~ "Just" ~ minionType
    | "MinionCard" ~ "[" ~ repsep(effect, ",") ~ "]" ~ health ~ attack ~ taunt ~ "Nothing"
    | "SpellCard" ~ "[" ~ repsep(effect, ",") ~ "]") ^^ {
      case "MinionCard" ~ "[" ~ (e: List[Effect]) ~ "]" ~ (h: String) ~ (a: String) ~ (t: Boolean) ~ "Just" ~ (m: MinionType.Value) => new MinionCard(e, h.toInt, a.toInt, t, m)
      case "MinionCard" ~ "[" ~ (e: List[Effect]) ~ "]" ~ (h: String) ~ (a: String) ~ (t: Boolean) ~ "Nothing" => new MinionCard(e, h.toInt, a.toInt, t, null)
      case "SpellCard" ~ "[" ~ (e: List[Effect]) ~ "]" => new SpellCard(e)
    }

  // olenditel võivad olla sellised tüübid
  def minionType: Parser[MinionType.Value] = ("Beast" | "Murloc") ^^ {
    case "Beast" => BEAST
    case "Murloc" => MURLOC
  }

  def effect: Parser[Effect] = (("OnPlay" | "UntilDeath" | "OnDamage" | "OnDeath") ~ "[" ~ repsep(eventEffect, ",") ~ "]") ^^ {
    case "OnPlay" ~ "[" ~ (ef: List[EventEffect]) ~ "]" => new Effect(EffectType.ONPLAY, ef)
    case "UntilDeath" ~ "[" ~ (ef: List[EventEffect]) ~ "]" => new Effect(EffectType.UNTILDEATH, ef)
    case "OnDamage" ~ "[" ~ (ef: List[EventEffect]) ~ "]" => new Effect(EffectType.ONDAMAGE, ef)
    case "OnDeath" ~ "[" ~ (ef: List[EventEffect]) ~ "]" => new Effect(EffectType.ONDEATH, ef)
  }

  def eventEffect: Parser[EventEffect] = (("All" | "Choose" | "Random") ~ "[" ~ repsep(filter, ",") ~ "]" ~ "[" ~ repsep(creatureEffect, ",") ~ "]"
    | "DrawCard") ^^ {
      case "All" ~ "[" ~ (f: List[Filter]) ~ "]" ~ "[" ~ (cf: List[CreatureEffect]) ~ "]" => new AllEventEffect(f, cf)
      case "Choose" ~ "[" ~ (f: List[Filter]) ~ "]" ~ "[" ~ (cf: List[CreatureEffect]) ~ "]" => new ChooseEventEffect(f, cf)
      case "Random" ~ "[" ~ (f: List[Filter]) ~ "]" ~ "[" ~ (cf: List[CreatureEffect]) ~ "]" => new RandomEventEffect(f, cf)
      case "DrawCard" => new DrawCardEventEffect
    }

  def creatureEffect: Parser[CreatureEffect] = ("Health" ~ funType ~ health
    | "Attack" ~ funType ~ attack
    | "Taunt" ~ taunt) ^^ {
      case "Health" ~ (fu: Type.Value) ~ (h: String) => new HealthCreatureEffect(fu, h.toInt)
      case "Attack" ~ (fu: Type.Value) ~ (a: String) => new AttackCreatureEffect(fu, a.toInt)
      case "Taunt" ~ (t: Boolean) => new TauntCreatureEffect(t)
    }

  // muutuse tüüp
  def funType: Parser[Type.Value] = ("Relative" | "Absolute") ^^ {
    case "Absolute" => ABSOLUTE
    case "Relative" => RELATIVE
  }

  def filter: Parser[Filter] = ("AnyCreature"
    | "AnyHero"
    | "AnyFriendly"
    | "Type" ~ minionType
    | "Self"
    | "Any" ~ "[" ~ repsep(filter, ",") ~ "]"
    | "Not" ~ "[" ~ repsep(filter, ",") ~ "]") ^^ {
      case "AnyCreature" => new AnyCreatureFilter
      case "AnyHero" => new AnyHeroFilter
      case "AnyFriendly" => new AnyFriendlyFilter
      case "Type" ~ (minion: MinionType.Value) => new TypeFilter(minion)
      case "Self" => new SelfFilter
      case "Any" ~ "[" ~ (f: List[Filter]) ~ "]" => new AnyFilter(f)
      case "Not" ~ "[" ~ (f: List[Filter]) ~ "]" => new NotFilter(f)
    }

  /* Methods to test stuff with */

  def filterMatch(text: String): Filter = {
    filter(new lexical.Scanner(text)) match {
      case Success(ord, _) => ord
      case Failure(msg, _) =>
        println(msg); null
      case Error(msg, _) => println(msg); null
    }
  }

  def creatureEffectMatch(text: String): CreatureEffect = {
    creatureEffect(new lexical.Scanner(text)) match {
      case Success(ord, _) => ord
      case Failure(msg, _) =>
        println(msg); null
      case Error(msg, _) => println(msg); null
    }
  }

  def eventEffectMatch(text: String): EventEffect = {
    eventEffect(new lexical.Scanner(text)) match {
      case Success(ord, _) => ord
      case Failure(msg, _) =>
        println(msg); null
      case Error(msg, _) => println(msg); null
    }
  }

  def effectMatch(text: String): Effect = {
    effect(new lexical.Scanner(text)) match {
      case Success(ord, _) => ord
      case Failure(msg, _) =>
        println(msg); null
      case Error(msg, _) => println(msg); null
    }
  }

  def cardTypeMatch(text: String): CardType = {
    cardType(new lexical.Scanner(text)) match {
      case Success(ord, _) => ord
      case Failure(msg, _) =>
        println(msg); null
      case Error(msg, _) => println(msg); null
    }
  }

  /**
   * Method for parsing everything in a cardpack
   */
  def doMatch(text: String): List[Card] = {
    file(new lexical.Scanner(text)) match {
      case Success(ord, _) => ord
      case Failure(msg, _) =>
        println(msg); null
      case Error(msg, _) => println(msg); null
    }
  }

}