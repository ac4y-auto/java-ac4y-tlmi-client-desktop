# 001 - Eredeti Android projekt elemzese

**Datum:** 2026-02-06

## Kiindulasi allapot

A projekt egy Android nativ alkalmazas (Tolmi kommunikator kliens), amely valos ideju chat funkciot biztosit WebSocket-en keresztul, beszedfelismeressel (STT) es szovegfelolvasassal (TTS).

## Technologiai stack

- **Nyelv:** Java
- **Platform:** Android (API 25-28, Nougat - Pie)
- **Build:** Gradle + Android Gradle Plugin 3.3.1
- **Csomagban:** `tlmi.communcator.atlmiclient`
- **Verzio:** 1.20190312.1

## Fo fuggosegek

- `com.android.support:appcompat-v7:28.0.0`
- `org.java-websocket:Java-WebSocket:1.3.0`
- Sajat ac4y framework konyvtarak (ac4yEnvironment, ac4yCommandDomain, ac4yClass, stb.)
- Sajat tlmi konyvtarak (tlmiUserDomain, tlmiUserClient, tlmiClient)
- Gson (JSON)

## Projekt struktura (app/src/main/java)

```
tlmi.communcator.atlmiclient/
  MainActivity.java          - fo Activity (~1050 sor)
  MainActivityAlgebra.java   - alap osztaly, mezok osszefogasa
  AppEnvironmentVariableHandler.java - SharedPreferences-alapu konfiguracio
  model/                     - LogEvent, ChatEvent, KeyValue (POJO-k)
  command/domain/            - TlmiMessage, TlmiCMDInvitation, TlmiCMDInvitationAccept
  command/algebra/           - alap osztalyok (extends Ac4yCommand)
  control/                   - Android ListView adapterek es handlerek
  indicator/                 - vizualis allapotjelzok (szines indikatorok)
  utility/                   - SpeechRecognizer, SpeechSynthesizer, ImageHandler, ScreenMessageHandler
```

## Fo funkciok

1. Felhasznalo regisztracio/bejelentkezes (ac4y Gate service)
2. Partner lista betoltese es megjelenitese
3. Meghivas kuldese/fogadasa (TlmiCMDInvitation / TlmiCMDInvitationAccept)
4. Valos ideju chat WebSocket-en keresztul
5. Automatikus forditas (Text2Text) ha a partnerek kulonbozo nyelvuek
6. Beszedfelismeres (Android SpeechRecognizer)
7. Szovegfelolvasas (Android TextToSpeech)
8. Vizualis allapotjelzok (internet, login, websocket, STT, TTS)
