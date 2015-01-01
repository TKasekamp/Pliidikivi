Pliidikivi
==========

MTAT.03.006 projekt. Scala + maven


See siin on Scala projekt. Seda saab importida Eclipse'i. Projekti ehitamisel olen kasutanud Mavenit. 
Projektiga on kaasas ka 9 testi, aga neid on raske ilma Mavenita käivitada.



Projekti käivitamine, kui maven on olemas
`` mvn package exec:java -Dexec.mainClass=ee.tkasekamp.pliidikivi.Game ``

Kaartide sisselugemine käib läbi faili src/main/resources/paths.txt. Seal on defineeritud mõlema mängija kaardipakkide asukohad. 

## Mängust ise:
Parser on täiesti töökorras. Parseri tegemisel kasutasin üht scala librarit, mis oli varem scala keele osa. Kahjuks nad võtsid selle välja ning selle kasutamiseks pidin tegema maven projekti.
Mäng teeb kõike, mis projekti nõuetes oli ette nähtud. Ma ei suuda küll anda garantiid, et see teeb kõike 100% õigesti.

## Projekti tegemisel on abiks olnud need leheküljed:
http://chimera.labs.oreilly.com/books/1234000001798/index.html
http://blog.xebia.com/2009/10/21/parsing-text-with-scala/
http://debasishg.blogspot.com/2008/04/external-dsls-made-easy-with-scala.html
http://underscore.io/blog/posts/2014/09/03/enumerations.html

http://stackoverflow.com/questions/6758258/running-a-maven-scala-project