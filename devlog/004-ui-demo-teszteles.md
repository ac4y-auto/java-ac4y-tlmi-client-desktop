# 004 - UI demo teszteles

**Datum:** 2026-02-06

## Problema: ac4y/tlmi fuggosegek nem elerhetoek

A Maven build elbukott, mert a `maven.ac4y.com` repository nem elerheto, igy az ac4y es tlmi JAR-ok nem tolthetoek le.

```
Could not transfer artifact ac4y.environment:ac4yEnvironment:pom:1.20190226.1
from/to ac4y-internal (https://maven.ac4y.com/repository/internal): maven.ac4y.com
```

A JAR-ok a lokalis Gradle cache-ben sem talalhatoak (a korabbi Android build nem hagyta hatre oket).

**Terv:** A konyvtarak GitHub Packages-kent lesznek elhetovek kesobbb.

## Megoldas: fuggosegmentes UI demo

Letrehoztam egy `MainDemo.java`-t, amely:
- Csak Swing-et hasznal (semmi ac4y/tlmi fuggoseg)
- Mock adatokkal tolti fel az UI-t
- Kozvetlenul `javac`-cal forditva es `java`-val futtatva

A `PartnerListPanel`-t atirtam, hogy ne fuggjon a `TlmiTranslateUser`-tol:
- Bevezetettem egy belso `PartnerInfo` osztaly (name, humanName, avatar)
- Kesobbb a TlmiTranslateUser konnyen mappelheto PartnerInfo-ra

## Forditas es futtas

```bash
javac -d target/classes src/main/java/tlmi/communcator/atlmiclient/.../*.java
java -cp target/classes tlmi.communcator.atlmiclient.MainDemo
```

**Eredmeny:** sikeres forditas es futtas, az ablak megjelenik.

## Demo funkciok

- StatusBar: NET, LOGIN, WS zolden
- Partner lista: 3 mock partner (Alice Johnson, Bob Smith, Carlos Garcia)
- Partnerre kattintva: atvalt chat nezetre, partner nev frissul
- Chat: szoveget irva + Enter/Send -> kek buborek jobbra, 1 mp mulva echo valasz szurkeben balra
- Log: minden esemeny megjelenik forditott sorrendben
- Navigacios gombok: Partners / Chat / Log kozott valtogatas

## Kovetkezo lepesek

1. ac4y/tlmi konyvtarak elhelyezese GitHub Packages-re
2. pom.xml frissitese a GitHub Packages repository-val
3. MainController osszekotese a valodi service-ekkel
4. Teljes build es integralt teszteles
