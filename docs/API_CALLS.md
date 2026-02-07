# TLMI Desktop Client - API Hivások Dokumentáció

**Projekt**: ac4y-tlmi-client-desktop v1.0.20260207.1
**Dokumentáció dátuma**: 2026-02-07
**Utolsó frissítés**: 2026-02-07 (ServerConfig, localhost támogatás)

---

## Szerver Környezet

Az alkalmazás a `ServerConfig` osztályon keresztül kezeli a szerver URL-eket. Két profil érhető el:

### Production profil (`ServerConfig.production()`)

| Szerver | URL | Protokoll | Kliens Osztály |
|---------|-----|-----------|----------------|
| User Service | `https://client.ac4y.com` | HTTPS REST | `TlmiUserServiceClient` |
| Gate Service | `https://gate.ac4y.com` | HTTPS REST | `Ac4yGateServiceClient` |
| Translation API | `https://api.ac4y.com` | HTTPS REST | `TlmiServiceClient` |
| WebSocket | `wss://www.ac4y.com:2222/{userId}` | WSS | `WebSocketClient` |

### Local profil (`ServerConfig.local()`) — `--local` flag

| Szerver | URL | Protokoll | Megjegyzés |
|---------|-----|-----------|------------|
| Gate Service | `http://localhost:3000` | HTTP REST | ac4y-gate Node.js |
| User Service | `http://localhost:3000` | HTTP REST | Csak `/gate/*` végpontok |
| Translation API | `http://localhost:3000` | HTTP REST | Nem elérhető |
| WebSocket | `ws://localhost:2222/{userId}` | WS | ac4y-gate WS szerver |

**Megjegyzés**: A local profilban a User Service (`/user/*`) és Translation API (`/api/*`) végpontok nem érhetők el, mert az ac4y-gate szerver csak `/gate/*` útvonalakat implementál. Az alkalmazás graceful degradation-nel kezeli ezt: a partner lista és saját profil betöltés nem blokkol, ha a user service nem elérhető.

### ServerConfig használata

```java
// Indítás: --local flag → ServerConfig.local()
// Alapértelmezett: ServerConfig.production()
ServerConfig config = useLocal ? ServerConfig.local() : ServerConfig.production();
MainController controller = new MainController(view, cliMode, config);
```

---

## 1. Internet Ellenőrzés

**Hol**: `MainController.checkInternet()`
**Mikor**: Az `initialize()` legelső lépése

```
GET {serverConfig.getHealthCheckUrl()}
    Production: GET https://gate.ac4y.com/gate/user
    Local:      GET http://localhost:3000/gate/user
Timeout: 3000ms
```

**Cél**: Egyszerű elérhetőségi próba — ha a szerver nem érhető el, az app hibát jelez és a StatusBar `internetError()` állapotba kerül. A többi lépés is lefut, de sikertelen lesz.

**Eredmény**:
- Sikeres (HTTP 2xx/3xx): `view.setInternetStatus(true)`
- Sikertelen: `view.setInternetStatus(false)` + hibaüzenet

---

## 2. Felhasználó Lekérdezés Név Alapján

**Hol**: `MainController.tryGetTranslateUserByName()`
**Kliens**: `TlmiUserServiceClient(serverConfig.getUserServiceUrl())`
**Metódus**: `getTranslateUserByName(GetTranslateUserByNameRequest)`

**Mikor hívódik** (4 helyen):
1. `initialize()` step 3 — Saját felhasználó létezésének ellenőrzése induláskor
2. `loadSelfInfo()` step 7 — Saját profil (humanName, avatar) betöltése
3. `handleInvitation()` — Meghívó partner adatainak (avatar) betöltése
4. `handleInvitationAccept()` — Meghívást elfogadó partner adatainak betöltése

**Graceful degradation**: Ha a user service nem elérhető (pl. local profilban), a hívás kivételt dob, amit a `loadSelfInfo()` és `loadPartners()` elfog és logol.

**Request**:
```java
GetTranslateUserByNameRequest {
    name: String  // UUID-alapú felhasználó azonosító
}
```

**Response**:
```java
GetTranslateUserByNameResponse {
    result: Result     // sikeres/sikertelen
    object: TlmiTranslateUser {
        name: String
        humanName: String
        avatar: String  // Base64 kódolt kép
        password: String
    }
}
```

**Cél**: Felhasználó profil adatainak (név, avatar, nyelv) lekérése a szerveren tárolt adatbázisból.

---

## 3. Felhasználó Létrehozás (User Service)

**Hol**: `MainController.tryInsertUser()`
**Kliens**: `TlmiUserServiceClient(serverConfig.getUserServiceUrl())`
**Metódus**: `insertUser(InsertUserRequest)`

**Mikor hívódik**:
- `initialize()` sor 94 — Csak ha `tryGetTranslateUserByName()` sikertelen volt (az app első indítása, a felhasználó még nem létezik)

**Request**:
```java
InsertUserRequest {
    object: TlmiTranslateUser {
        name: String      // UUID.randomUUID()
        password: "1"     // statikus jelszó
    }
}
```

**Response**:
```java
InsertUserResponse {
    result: Result
}
```

**Cél**: Új felhasználói profil létrehozása a TLMI User Service adatbázisban. Ez a felhasználói szintű regisztráció.

---

## 4. Felhasználó Regisztráció (Gate Service)

**Hol**: `MainController.tryGateInsertUser()`
**Kliens**: `Ac4yGateServiceClient(serverConfig.getGateServiceUrl())`
**Metódus**: `insertUser(GateInsertUserRequest)`

**Mikor hívódik** (2 helyen):
1. `initialize()` sor 95 — Ha a felhasználó nem létezik (első indítás), a User Service insert után
2. `initialize()` sor 98 — **Minden indításkor**, feltétel nélkül (biztosítja, hogy a gate ismeri a felhasználót)

**Request**:
```java
GateInsertUserRequest {
    name: String      // UUID-alapú userId
    password: "1"     // statikus jelszó
}
```

**Response**:
```java
GateInsertUserResponse {
    result: Result
}
```

**Cél**: A Gate Service az autentikációs kapurendszer. A felhasználót itt is regisztrálni kell, hogy aztán be tudjon lépni. A dupla hívás (sor 95 + sor 98) biztosítja, hogy a gate mindig friss bejegyzéssel rendelkezik.

---

## 5. Bejelentkezés (Gate Service)

**Hol**: `MainController.tryLogin()`
**Kliens**: `Ac4yGateServiceClient(serverConfig.getGateServiceUrl())`
**Metódus**: `login(GateLoginRequest)`

**Mikon hívódik**:
- `initialize()` sor 101 — A gate regisztráció után, minden indításkor

**Request**:
```java
GateLoginRequest {
    name: String      // UUID-alapú userId
    password: "1"     // statikus jelszó
}
```

**Response**:
```java
GateLoginResponse {
    result: Result    // .itWasSuccessful() vagy .itWasFailed()
}
```

**Cél**: Sikeres login nélkül az app leáll (`return` a sor 109-en). Ha sikertelen, a StatusBar `loginError()` állapotba kerül, és az `initialize()` nem folytatódik (nincs WebSocket, nincs partner lista, nincs chat).

**Eredmény**:
- Sikeres: StatusBar `loginLive()`, folytatás a WebSocket csatlakozással
- Sikertelen: StatusBar `loginError()`, `initialize()` megáll

---

## 6. WebSocket Csatlakozás

**Hol**: `MainController.connectWebSocket()`
**Protokoll**: WSS (production) / WS (local)
**URL**: `serverConfig.getWebsocketUri(userId)`
- Production: `wss://www.ac4y.com:2222/{userId}`
- Local: `ws://localhost:2222/{userId}`

**Mikor hívódik**:
- `initialize()` step 5 — Sikeres login után

**Események**:
```java
onOpen()    → StatusBar.websocketLive()
onMessage() → SwingUtilities.invokeLater(() -> processMessage(json))
onClose()   → trace("WS closed: " + reason)
onError()   → StatusBar.websocketError() + JOptionPane hibaüzenet
```

**Cél**: Valós idejű, kétirányú kommunikáció a felhasználók között. Minden üzenet (meghívás, elfogadás, chat) ezen a csatornán megy.

### WebSocket Üzenet Küldés

**Hol**: `MainController.sendAc4yObjectAsMessage()` (sor: 241-251)

```java
Ac4yCMDMessage message = new Ac4yCMDMessage();
message.getRequest().setSender(userId);
message.getRequest().setAddressee(partnerId);
message.getRequest().setBody(ac4yObject.getAsJson());
webSocketClient.send(message.getAsJson());
```

**Cél**: Bármilyen Ac4y objektumot (TlmiMessage, TlmiCMDInvitation, TlmiCMDInvitationAccept) Ac4yCMDMessage wrapper-be csomagol és elküldi a WebSocket-en.

### WebSocket Üzenet Fogadás

**Hol**: `MainController.processMessage()` (sor: 279-322)

Bejövő JSON diszpécselése `commandName` alapján:

| commandName | Handler metódus | Leírás |
|-------------|----------------|--------|
| `MESSAGE` | (wrapper feldolgozás) | Ac4yCMDMessage body kicsomagolása |
| `TLMICMDINVITATION` | `handleInvitation()` | Partner meghívást küld |
| `TLMICMDINVITATIONACCEPT` | `handleInvitationAccept()` | Partner elfogadta a meghívást |
| `SERVICERESPONSE` | `handleServiceResponse()` | Szerver oldali válasz/hiba |
| `TLMIMESSAGE` | `handleTlmiMessage()` | Chat üzenet érkezett |

---

## 7. Partner Lista Betöltése

**Hol**: `MainController.loadPartners()`
**Kliens**: `TlmiUserServiceClient(serverConfig.getUserServiceUrl())`

**Graceful degradation**: Ha a user service nem elérhető (pl. local profil), a partner lista üres marad, de az alkalmazás folytatja a működést (gate + websocket normálisan üzemel).
**Metódus**: `getAllTranslateUsers()`

**Mikor hívódik**:
- `initialize()` sor 116 — Sikeres WebSocket csatlakozás után

**Response**:
```java
GetAllTranslateUsersResponse {
    list: List<TlmiTranslateUser>  // összes regisztrált felhasználó
}
```

**Szűrés**: Kiszűri a saját felhasználót (`!partner.getName().equals(userId)`) és azokat, akiknek nincs avatar-ja (`partner.getAvatar() != null`).

**Cél**: A bal oldali partner lista populálása. Minden partner kattintható — kattintáskor meghívás (TlmiCMDInvitation) küldése és átváltás a chat nézetre.

---

## 8. Szöveg Fordítás (Text2Text)

**Hol**: `MainController.tryText2Text()`
**Kliens**: `TlmiServiceClient(serverConfig.getTranslationServiceUrl())`
**Metódus**: `text2text(Text2TextRequest)`

**Mikor hívódik**:
- `sendTlmiMessage()` sor 261-266 — Csak ha a felhasználó és a partner nyelve eltér

**Request**:
```java
Text2TextRequest {
    text: String            // eredeti szöveg
    targetLanguage: String  // partner nyelve (pl. "en")
    sourceLanguage: String  // felhasználó nyelve (pl. "hu")
}
```

**Response**:
```java
Text2TextResponse {
    result: Result
    object: String  // lefordított szöveg
}
```

**Cél**: Az üzenet automatikus fordítása a partner nyelvére, mielőtt WebSocket-en elküldésre kerülne. Ha a nyelv megegyezik, a fordítás nem hívódik meg.

---

## API Hívások Szekvencia Diagramja

```
App indítás                                    Production URL          Local URL
│
├─ 1. GET {healthCheckUrl}                     gate.ac4y.com/gate/user localhost:3000/gate/user
│      [Internet/szerver ellenőrzés]
│
├─ 2. GET {userServiceUrl}                     client.ac4y.com        localhost:3000 (*)
│      └─ getTranslateUserByName(userId)       [Felhasználó létezik?]
│
├─ 3. POST {userServiceUrl}                    client.ac4y.com        localhost:3000 (*)
│      └─ insertUser(TlmiTranslateUser)        [Ha nem létezik: user insert]
│
├─ 4. POST {gateServiceUrl}                    gate.ac4y.com          localhost:3000
│      └─ insertUser(name, password)           [Gate regisztráció] (ha nem létezik + mindig)
│
├─ 5. POST {gateServiceUrl}                    gate.ac4y.com          localhost:3000
│      └─ login(name, password)                [Login]
│      └─ HA SIKERTELEN → STOP
│
├─ 6. WS {websocketUri}/{userId}               wss://ac4y.com:2222    ws://localhost:2222
│      [WebSocket csatlakozás]
│
├─ 7. GET {userServiceUrl}                     client.ac4y.com        localhost:3000 (*)
│      └─ getAllTranslateUsers()               [Partner lista betöltés]
│
└─ 8. GET {userServiceUrl}                     client.ac4y.com        localhost:3000 (*)
       └─ getTranslateUserByName(userId)       [Saját profil betöltés]

(*) = Graceful degradation: ha a user service nem elérhető, nem blokkol

Chat közben
│
├─ Üzenet küldés (ha eltérő nyelv):
│   └─ POST {translationServiceUrl}            api.ac4y.com           localhost:3000 (*)
│          └─ text2text(text, targetLang, sourceLang)
│   └─ WS send(TlmiMessage)                   [Küldés partnernek]
│
├─ Meghívás fogadásakor:
│   └─ GET {userServiceUrl}                    client.ac4y.com        localhost:3000 (*)
│          └─ getTranslateUserByName(partnerName)
│
└─ Meghívás elfogadásakor:
    └─ GET {userServiceUrl}                    client.ac4y.com        localhost:3000 (*)
           └─ getTranslateUserByName(partnerName)
```

---

## Hibakezelés

Minden `try*()` metódus azonos mintát követ:

```java
public XxxResponse tryXxx(XxxRequest request) {
    XxxResponse response = client.xxx(request);
    if (response.itWasFailed())
        screenMessageHandler.errorNotifying(response.getResult().getDescription());
    return response;
}
```

- Sikertelen hívás: `ScreenMessageHandler.errorNotifying()` → JOptionPane hibaüzenet
- A hívás **nem dob** kivételt, hanem a response `itWasFailed()` flagjét vizsgálja
- A `checkInternet()` az egyetlen hívás, ami try-catch-et használ (java.net.HttpURLConnection)

---

## Megjegyzések

1. **Statikus jelszó**: Minden felhasználó jelszava `"1"` — ez nem valódi autentikáció, hanem a Gate Service elvárásához igazodó placeholder
2. **Dupla gate insert**: A `tryGateInsertUser()` kétszer hívódik — egyszer az új felhasználó folyamaton belül, és egyszer feltétel nélkül
3. **ServerConfig**: A szerver URL-ek a `ServerConfig` osztályban vannak definiálva, két profillal: `production()` és `local()`. A `--local` CLI flag aktiválja a localhost profilt
4. **Szinkron hívások**: Minden REST hívás szinkron (blokkoló) — háttérszálról fut (`new Thread()` a `Main.java`-ban)
5. **Graceful degradation**: A `loadPartners()` és `loadSelfInfo()` metódusok try-catch-ben futnak — ha a user service nem elérhető (pl. local profilban), az alkalmazás folytatja a működést a gate + websocket funkciókkal
6. **CLI mód**: `--cli` flag-gel GUI nélkül indul (CliMainView), `--local` flag-gel a localhost szerverhez csatlakozik

## Indítási módok

```bash
# GUI + production szerver
mvn exec:java

# GUI + localhost szerver
mvn exec:java -Dexec.args="--local"

# CLI + production szerver
mvn exec:java -Dexec.args="--cli"

# CLI + localhost szerver
mvn exec:java -Dexec.args="--cli --local"

# Teszt (TRACE loglevel, auto-detected log4j2-test.xml)
mvn test
```
