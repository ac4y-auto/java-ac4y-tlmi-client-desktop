# Session Handoff — java-ac4y-tlmi-client-desktop

Ez a fajl az egyetlen instrukció amit egy uj Claude Code session-nek meg kell adni.
Az uj session ebbol a fajlbol megtudja: hol van minden, hogyan kell beolvasni a kontextust, es hogyan kell atvenni a munkat.

---

## 1. Onboarding: olvasd be ezeket a fajlokat

### Kotelezo (mindig)

| # | Fajl | Mit tartalmaz |
|---|------|---------------|
| 1 | `pom.xml` | Maven fuggosegek, verzio, build config |
| 2 | `src/main/java/tlmi/communcator/atlmiclient/Main.java` | Fo belepesi pont (GUI/CLI router) |
| 3 | `src/main/java/tlmi/communcator/atlmiclient/MainController.java` | Uzleti logika, API hivasok, WebSocket, inicializacio |
| 4 | `src/main/java/tlmi/communcator/atlmiclient/ServerConfig.java` | Szerver URL konfiguracio (production/local) |
| 5 | `src/main/java/tlmi/communcator/atlmiclient/view/IMainView.java` | View interface (GUI es CLI kozos) |

### Ha a GUI-t modositod

| # | Fajl | Mit tartalmaz |
|---|------|---------------|
| 6 | `src/main/java/tlmi/communcator/atlmiclient/ui/MainFrame.java` | Swing foablak, CardLayout navigacio |
| 7 | `src/main/java/tlmi/communcator/atlmiclient/view/SwingMainView.java` | Swing IMainView implementacio |
| 8 | `src/main/java/tlmi/communcator/atlmiclient/ui/ChatPanel.java` | Chat buborek UI |
| 9 | `src/main/java/tlmi/communcator/atlmiclient/ui/PartnerListPanel.java` | Partner lista JList |
| 10 | `src/main/java/tlmi/communcator/atlmiclient/ui/StatusBar.java` | NET/LOGIN/WS/STT/TTS indikatorok |

### Ha a CLI modot modositod

| # | Fajl | Mit tartalmaz |
|---|------|---------------|
| 11 | `src/main/java/tlmi/communcator/atlmiclient/CliMain.java` | CLI belepesi pont |
| 12 | `src/main/java/tlmi/communcator/atlmiclient/view/CliMainView.java` | Headless IMainView (Log4j2 output) |

### Ha tesztet irsz

| # | Fajl | Mit tartalmaz |
|---|------|---------------|
| 13 | `src/test/java/tlmi/communcator/atlmiclient/CliInitializationTest.java` | 2 JUnit 5 teszt (production + local config) |
| 14 | `src/test/resources/log4j2-test.xml` | Teszt log config (TRACE, ./logs/ mappa) |

**Projekt gyokerkonyvtar:**
```
/Volumes/DevAPFS/work/by-project/ac4y-java/app/java-tolmi-client-desktop/
```

---

## 2. Projekt alapadatok

| Elem | Ertek |
|------|-------|
| **Projekt neve** | Tolmi Desktop Client |
| **GitHub repo** | `ac4y-auto/java-ac4y-tlmi-client-desktop` |
| **GitHub URL** | https://github.com/ac4y-auto/java-ac4y-tlmi-client-desktop.git |
| **Branch** | `main` |
| **GroupId** | `ac4y` |
| **ArtifactId** | `ac4y-tlmi-client-desktop` |
| **Verzio** | `1.0.20260208.2` |
| **JDK** | 11 |
| **Build rendszer** | Maven (pom.xml) |
| **Package** | `tlmi.communcator.atlmiclient` |

---

## 3. Fuggosegek

### AC4Y belso fuggosegek (GitHub Packages)

| ArtifactId | GroupId | Verzio | Cel |
|------------|---------|--------|-----|
| `ac4y-base-for-json-and-xml` | ac4y | 1.0.20260206.1 | JSON/XML serialization |
| `ac4y-command` | ac4y | 1.0.20260206.3 | Command pattern alap |
| `ac4y-gate-service` | ac4y | 1.0.20260206.2 | Gate REST kliens (login, insertuser) |
| `ac4y-message-command` | ac4y | 1.0.20260206.3 | Uzenetkuldesi command |
| `ac4y-service-command` | ac4y | 1.0.20260206.3 | Service command alap |
| `ac4y-tlmi-client` | ac4y | 1.0.20260206.1 | TLMI forditas kliens (text2text) |
| `ac4y-tlmi-user-client` | ac4y | 1.0.20260206.1 | User service kliens |
| `ac4y-tlmi-user-domain` | ac4y | 1.0.20260206.1 | User domain objektumok |

### Kulso fuggosegek

| ArtifactId | Verzio | Cel |
|------------|--------|-----|
| `Java-WebSocket` (org.java-websocket) | 1.5.4 | WebSocket kliens |
| `gson` (com.google.code.gson) | 2.10.1 | JSON parse |
| `log4j-api` + `log4j-core` | 2.8.2 | Logging |
| `junit-jupiter` | 5.10.2 | Tesztek (test scope) |

### Maven pluginek

| Plugin | Verzio | Cel |
|--------|--------|-----|
| `maven-surefire-plugin` | 3.2.5 | JUnit 5 futtatasa |
| `exec-maven-plugin` | 3.1.0 | `mvn exec:java` tamogatas |

---

## 4. Architektura

### Retegek

```
Main.java / CliMain.java          — Entry point (CLI/GUI valasztas)
    |
MainController                     — Uzleti logika, inicializacio
    |
    +-- IMainView (interface)      — View absztrakcio
    |       |
    |       +-- SwingMainView      — GUI implementacio (Swing)
    |       +-- CliMainView        — Headless implementacio (Log4j2)
    |
    +-- ServerConfig               — URL konfiguracio (production/local)
    +-- AppEnvironmentVariableHandler — In-memory session state (userId, partnerId, stb.)
    +-- ScreenMessageHandler       — Hibajelzesek (JOptionPane / LOG)
    +-- WebSocketClient            — Valos ideju kommunikacio
```

### UI felépítés (Swing)

```
MainFrame (JFrame, 500x700)
    |
    +-- [NORTH] StatusBar          — NET | LOGIN | WS | STT | TTS indikatorok
    |            UserInfoPanel      — selfAvatar + selfName | connection | partnerName + partnerAvatar
    |
    +-- [CENTER] CardLayout
    |       +-- "partners"  → PartnerListPanel (JList<PartnerInfo>)
    |       +-- "chat"      → ChatPanel (buborekos chat, inputField + sendButton)
    |       +-- "log"       → LogPanel (monospace JList, max 500 sor)
    |
    +-- [SOUTH] NavPanel           — [Partners] [Chat] [Log] gombok
```

### Command tipus (WebSocket uzenetkezeles)

| commandName | Osztaly | Leiras |
|-------------|---------|--------|
| `TLMICMDINVITATION` | TlmiCMDInvitation | Chat meghivas kuldes |
| `TLMICMDINVITATIONACCEPT` | TlmiCMDInvitationAccept | Meghivas elfogadas |
| `TLMIMESSAGE` | TlmiMessage | Chat uzenet |
| `MESSAGE` | Ac4yCMDMessage (ac4y-message-command) | Generikus uzenet wrapper |
| `SERVICERESPONSE` | Ac4yCMDServiceResponse (ac4y-service-command) | Service valasz |

### Forrasfajl terkep

| Csomag | Fajlok | Cel |
|--------|--------|-----|
| `tlmi.communcator.atlmiclient` | Main, CliMain, MainController, MainDemo, ServerConfig, AppEnvironmentVariableHandler | Fo logika |
| `tlmi.communcator.atlmiclient.command.algebra` | TlmiCMDInvitationAlgebra, TlmiCMDInvitationAcceptAlgebra, TlmiMessageAlgebra | Command alap osztalyok (extends Ac4yCommand) |
| `tlmi.communcator.atlmiclient.command.domain` | TlmiCMDInvitation, TlmiCMDInvitationAccept, TlmiMessage | Konkret command implementaciok |
| `tlmi.communcator.atlmiclient.model` | ChatEvent, KeyValue, LogEvent | Adat modellek |
| `tlmi.communcator.atlmiclient.ui` | MainFrame, ChatPanel, PartnerListPanel, StatusBar, LogPanel, ImageUtil | Swing UI komponensek |
| `tlmi.communcator.atlmiclient.utility` | ScreenMessageHandler | Seged |
| `tlmi.communcator.atlmiclient.view` | IMainView, SwingMainView, CliMainView | View reteg |

**Statisztika:** 26 Java fajl, ~2837 sor (src/main + src/test)

---

## 5. Inicializacios flow (MainController.initialize())

```
1. checkInternet()              — HTTP GET gate/user (3s timeout)
2. userId generálás             — UUID.randomUUID()
3. user ellenorzes/letrehozas   — tryGetTranslateUserByName → tryInsertUser → tryGateInsertUser
4. login                        — tryLogin (GateLoginRequest)
5. WebSocket csatlakozas        — ws(s)://{host}:{port}/{userId}
6. partner lista betoltes       — getAllTranslateUsers (graceful: null list → skip)
7. onnan sajat info betoltes    — getTranslateUserByName
8. chat send action regisztracio
```

**Fontos:** a user service (3. + 6. + 7. lepes) nem blokkolja az inicializaciot ha nem erheto el (graceful degradation).

---

## 6. ServerConfig profilok

| Profil | Gate REST | User Service | Translation | WebSocket |
|--------|-----------|--------------|-------------|-----------|
| `production()` | https://gate.ac4y.com | https://client.ac4y.com | https://api.ac4y.com | wss://www.ac4y.com:2222 |
| `local()` | http://localhost:3000 | http://localhost:3002 | http://localhost:3000 | ws://localhost:2222 |

---

## 7. Inditasi modok

```bash
# GUI + production
mvn exec:java

# GUI + localhost
mvn exec:java -Dexec.args="--local"

# CLI + production
mvn exec:java -Dexec.args="--cli"

# CLI + localhost
mvn exec:java -Dexec.args="--cli --local"

# UI Demo (mock data, nincs szerver fuggoseg)
mvn exec:java -Dexec.mainClass="tlmi.communcator.atlmiclient.MainDemo"

# JUnit tesztek (TRACE log → ./logs/tlmi-client-test.log)
mvn test

# Compile only
mvn clean compile
```

### ac4y-gate szerver inditasa (localhost fejleszteshez)

```bash
cd /Volumes/DevAPFS/work/by-project/ac4y-js/ac4y-gate
node index.js &
# REST: http://localhost:3000/gate/* (login, insertuser, user, statistics, stb.)
# WebSocket: ws://localhost:2222/{userId}
```

---

## 8. Logging konfiguracio

| Kontextus | Config fajl | Log szint | Kimenet |
|-----------|-------------|-----------|---------|
| Production/Runtime | `src/main/resources/log4j2.xml` | TRACE | Console + `./log/tlmi-client.log` |
| JUnit tesztek | `src/test/resources/log4j2-test.xml` | TRACE | Console + `./logs/tlmi-client-test.log` |

---

## 9. Tesztek

| Teszt osztaly | Tesztek szama | Mit tesztel |
|---------------|---------------|-------------|
| `CliInitializationTest` | 2 | CLI inicializacio production es local config-gal |

**Teszt strategia:** a tesztek az egesz inicializacios flow-t lefuttatjak CliMainView-val. A local teszt csak akkor sikeres teljesen, ha az ac4y-gate szerver fut localhost-on.

---

## 10. Projekt tortenete (git log)

| Commit | Leiras |
|--------|--------|
| `63256fc` | feat: initial commit — Tolmi Desktop Client |
| `6e421d9` | feat: add Log4j2 logging and API calls documentation |
| `73ea705` | feat: add CLI mode, IMainView interface, and JUnit 5 test |
| `1b2e4e7` | feat: add ServerConfig for localhost dev and graceful degradation |
| `b1cc441` | docs: update API_CALLS.md with ServerConfig profiles and localhost support |
| `b3e0d0e` | fix: null-check partner name, user service on port 3002, log config tuning |
| `62b79b1` | 1.0.20260208.1 |
| `dcdc5b0` | 1.0.20260208.2 |

**Eredeti forras:** Android alkalmazas (Android Studio projekt), desktop-ra (Swing) migrálva.

---

## 11. Meglevo dokumentacio

| Fajl | Tartalom |
|------|----------|
| `docs/API_CALLS.md` | Osszes REST es WebSocket API hivas dokumentacioja |
| `docs/ARCHITECTURE_DIAGRAM.md` | Reszletes architektura diagram |
| `docs/DEPENDENCIES.md` | Fuggosegek es verziok |
| `docs/FUNCTIONS_DOCUMENTATION.md` | Fuggvenyek dokumentacioja |
| `devlog/001-android-eredeti.md` | Eredeti Android projekt leirasa |
| `devlog/002-migracios-dontes.md` | Desktop migracio dontesei |
| `devlog/003-swing-ui-felepites.md` | Swing UI felepites |
| `devlog/004-ui-demo-teszteles.md` | UI demo teszteles |

---

## 12. Ismert anomaliak es megjegyzesek

| Anomalia | Megjegyzes |
|----------|------------|
| Package neve `tlmi.communcator` (typo: communicator) | Legacy, ne valtoztasd meg (minden fugg tole) |
| `KeyValue` es `LogEvent` model osztalyok | Jelenleg nem hasznaltak aktivan a kodbazisban |
| `MainDemo.java` | Swing UI demo mock adatokkal, szerver fuggoseg nelkul |
| `build.gradle` + `settings.gradle` + `.gradle/` + `.idea/` | Legacy Android Studio maradek, a build most Maven-nel tortenik |
| `app/` mappa | Legacy Android Studio mappastruktura maradek |
| `log4j2.xml` TRACE szintre allitva | Production hasznalathoz INFO-ra kell allitani |
| STT + TTS indikatorok a StatusBar-ban | Nincsenek bekotve (placeholder a jovohoz) |

---

## 13. Kovetkezo lehetseges lepesek

- Ket kliens (GUI + CLI) kozotti chat tesztelese WebSocket-en
- User service implementalasa a gate szerveren (/user/* vegpontok)
- Translation service integracio
- GUI mod tesztelese `--local` flag-gel
- Tovabbi tesztek irasa (WebSocket uzenetküldes, meghivas flow)
- STT/TTS integracio
- Production log szint atallitas (TRACE → INFO)

---

## 14. Hasznalat

### Uj session inditasa — masold be ezt:

```
Olvasd be ezt a handoff fajlt es kovessd az utasitasait:
/Volumes/DevAPFS/work/by-project/ac4y-java/app/java-tolmi-client-desktop/docs/session-handoff/SESSION-HANDOFF.md

[ITT IRD LE A KOVETKEZO FELADATOT]
```

### Pelda

```
Olvasd be ezt a handoff fajlt es kovessd az utasitasait:
/Volumes/DevAPFS/work/by-project/ac4y-java/app/java-tolmi-client-desktop/docs/session-handoff/SESSION-HANDOFF.md

Feladat: adjunk hozza reconnect logikát a WebSocket kapcsolathoz, hogy automatikusan ujracsatlakozzon ha megszakad.
```

---

## 15. AC4Y Mission Control hivatkozas

Ha az egesz ac4y ökoszisztemara vonatkozo informacio kell (osszes repo, dependency tree, konvenciok):

```
/Volumes/DevAPFS/work/by-project/ac4y-java/java-ac4y-mission-control/docs/session-handoff/SESSION-HANDOFF.md
```

---

**Utolso frissites:** 2026-02-08
