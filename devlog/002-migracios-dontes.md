# 002 - Migracios dontes: Android -> Desktop

**Datum:** 2026-02-06

## Cel

Az Android alkalmazast asztali Java alkalmazkent futtathatova tenni.

## Dontes: Java Swing

Megvizsgalt lehetosegek:
- **JavaFX** - modern, CSS stilusozas, FXML, de kulon fuggoseg es modul-konfiguracio szukseges
- **Java Swing** - regebbi, de a JDK-ban benne van, nincs kulon fuggoseg, gyors prototipushoz idealis

**Valasztas:** Java Swing - mert ez csak prototipus, es a legegyszerubb megoldas.

## Dontes: Build rendszer

**Valasztas:** Maven (pom.xml) - a Gradle Android pluginje helyett.

## Dontes: Speech funkciok

Az Android SpeechRecognizer es TextToSpeech API-knak nincs kozvetlen asztali megfeloloje.

**Valasztas:** Kesobbre halasztva. A prototipusban szoveges beviteli mezo (JTextField + Send gomb) helyettesiti a beszedfelismerest.

## Ujrafelhasznalas elemzese

| Kategoria | Fajlok | Ujrafelhasznalas |
|-----------|--------|------------------|
| Model POJO-k | LogEvent, ChatEvent, KeyValue | 100% valtozatlan |
| Command domain | TlmiMessage, TlmiCMDInvitation, TlmiCMDInvitationAccept | 100% valtozatlan |
| Command algebra | 3 alap osztaly | 100% valtozatlan |
| Uzleti logika | WebSocket, processMessage, service hivasok | ~90% (runOnUiThread -> SwingUtilities.invokeLater) |
| AppEnvironmentVariableHandler | SharedPreferences -> HashMap | atiras szukseges |
| UI reteg | Android Views, Adapterek, Layoutok | 0% - teljes ujrairast igenyel |
| Speech | SpeechRecognizer, SpeechSynthesizer | 0% - elhagyva |
| Indikatorok | VisualIndicator csomag | egyszerusitve StatusBar-ba |

**Osszessegeben:** ~35-40% valtozatlanul athelyezheto, ~60-65% ujrairasra szorul.
