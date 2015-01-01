Pliidikivi
==========

MTAT.03.006 projekt. Scala + maven

Vägev kaardimäng. Tõsine asi. Siin pole midagi naljakat. 

## Käivitamine
Käivitamiseks on kaks võimalust:

* `` mvn exec:java `` , kuid kahjuks siis Windowsi peal puuduvad täpitähed
* `` mvn package`` ning seejärel ``java -jar target/Pliidikivi-1.0.0.jar``.

Teste on 9 ning nende jaoks ``mvn test``.

Kaartide sisselugemine käib läbi faili src/main/resources/paths.txt. Seal on defineeritud mõlema mängija kaardipakkide asukohad. 

pom.xml koosneb suvaliselt kokku kopeeritud asjadest. Kõik selles ei pruugi olla vajalik.

## Eclipse importimine
Projekt on tehtud kasutades Eclipse'i koos Scala IDE plugin'iga. Projekti kaustas tuleb teha ``mvn eclipse:eclipse`` ning siis projekt importida. Seejärel Eclipse valikutes Configure -> Add Scala nature. Teha tuleks ka Convert to Maven project. Kui kõik on punane, siis Project -> Clean. Nüüd peaks Eclipse rahul olema.

Märkus: maven võib katki olla, kui Eclipse'il pole m2e scala plugini või midagi sellist. See tuleks ka installida.  

## Mängust ise:
Parser on täiesti töökorras. Parseri tegemisel kasutasin üht scala librarit, mis oli varem scala keele osa. Kahjuks nad võtsid selle välja ning selle kasutamiseks pidin tegema maven projekti.
Mäng teeb kõike, mis projekti nõuetes oli ette nähtud. Ma ei suuda küll anda garantiid, et see teeb kõike 100% õigesti.

## Projekti tegemisel on abiks olnud need leheküljed:
http://chimera.labs.oreilly.com/books/1234000001798/index.html
http://blog.xebia.com/2009/10/21/parsing-text-with-scala/
http://debasishg.blogspot.com/2008/04/external-dsls-made-easy-with-scala.html
http://underscore.io/blog/posts/2014/09/03/enumerations.html

http://stackoverflow.com/questions/6758258/running-a-maven-scala-project