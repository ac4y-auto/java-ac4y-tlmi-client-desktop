# TLMI Desktop Client - API Hivások Dokumentáció

**Projekt**: ac4y-tlmi-client-desktop v1.0.20260207.1
**Dokumentáció dátuma**: 2026-02-07

---

## Szerver Környezet

| Szerver | URL | Protokoll | Kliens Osztály |
|---------|-----|-----------|----------------|
| User Service | `https://client.ac4y.com` | HTTPS REST | `TlmiUserServiceClient` |
| Gate Service | `https://gate.ac4y.com` | HTTPS REST | `Ac4yGateServiceClient` |
| Translation API | `https://api.ac4y.com` | HTTPS REST | `TlmiServiceClient` |
| WebSocket | `wss://www.ac4y.com:2222/{userId}` | WSS | `WebSocketClient` |

---

## 1. Internet Ellenőrzés

**Hol**: `MainController.checkInternet()` (sor: 134-146)
**Mikor**: Az `initialize()` legelső lépése

```
HEAD https://client.ac4y.com
Timeout: 3000ms
```

**Cél**: Egyszerű elérhetőségi próba — ha a client.ac4y.com nem érhető el, az app hibát jelez és a StatusBar `internetError()` állapotba kerül. A többi lépés is lefut, de sikertelen lesz.

**Eredmény**:
- Sikeres: StatusBar `internetLive()`
- Sikertelen: StatusBar `internetError()` + JOptionPane hibaüzenet

---

## 2. Felhasználó Lekérdezés Név Alapján

**Hol**: `MainController.tryGetTranslateUserByName()` (sor: 402-408)
**Kliens**: `TlmiUserServiceClient("https://client.ac4y.com")`
**Metódus**: `getTranslateUserByName(GetTranslateUserByNameRequest)`

**Mikor hívódik** (4 helyen):
1. `initialize()` sor 86-87 — Saját felhasználó létezésének ellenőrzése induláskor
2. `loadSelfInfo()` sor 182-183 — Saját profil (humanName, avatar) betöltése
3. `handleInvitation()` sor 341-342 — Meghívó partner adatainak (avatar) betöltése
4. `handleInvitationAccept()` sor 364-365 — Meghívást elfogadó partner adatainak betöltése

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

**Hol**: `MainController.tryInsertUser()` (sor: 410-416)
**Kliens**: `TlmiUserServiceClient("https://client.ac4y.com")`
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

**Hol**: `MainController.tryGateInsertUser()` (sor: 418-424)
**Kliens**: `Ac4yGateServiceClient("https://gate.ac4y.com")`
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

**Hol**: `MainController.tryLogin()` (sor: 434-440)
**Kliens**: `Ac4yGateServiceClient("https://gate.ac4y.com")`
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

**Hol**: `MainController.connectWebSocket()` (sor: 201-237)
**Protokoll**: WSS (WebSocket Secure)
**URL**: `wss://www.ac4y.com:2222/{userId}`

**Mikor hívódik**:
- `initialize()` sor 113 — Sikeres login után

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

**Hol**: `MainController.loadPartners()` (sor: 148-178)
**Kliens**: `TlmiUserServiceClient("https://client.ac4y.com")`
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

**Hol**: `MainController.tryText2Text()` (sor: 426-432)
**Kliens**: `TlmiServiceClient("https://api.ac4y.com")`
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
App indítás
│
├─ 1. HEAD https://client.ac4y.com                    [Internet ellenőrzés]
│
├─ 2. GET  https://client.ac4y.com                    [Felhasználó létezik?]
│      └─ getTranslateUserByName(userId)
│
├─ 3. POST https://client.ac4y.com                    [Ha nem létezik: user insert]
│      └─ insertUser(TlmiTranslateUser)
│
├─ 4. POST https://gate.ac4y.com                      [Gate regisztráció]
│      └─ insertUser(name, password)                   (ha nem létezik + mindig)
│
├─ 5. POST https://gate.ac4y.com                      [Login]
│      └─ login(name, password)
│      └─ HA SIKERTELEN → STOP
│
├─ 6. WSS  wss://www.ac4y.com:2222/{userId}           [WebSocket csatlakozás]
│
├─ 7. GET  https://client.ac4y.com                    [Partner lista betöltés]
│      └─ getAllTranslateUsers()
│
└─ 8. GET  https://client.ac4y.com                    [Saját profil betöltés]
       └─ getTranslateUserByName(userId)


Chat közben
│
├─ Üzenet küldés (ha eltérő nyelv):
│   └─ POST https://api.ac4y.com                      [Fordítás]
│          └─ text2text(text, targetLang, sourceLang)
│   └─ WSS  send(TlmiMessage)                         [Küldés partnernerk]
│
├─ Meghívás fogadásakor:
│   └─ GET  https://client.ac4y.com                   [Partner avatar betöltés]
│          └─ getTranslateUserByName(partnerName)
│
└─ Meghívás elfogadásakor:
    └─ GET  https://client.ac4y.com                   [Partner avatar betöltés]
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
2. **Dupla gate insert**: A `tryGateInsertUser()` kétszer hívódik (sor 95 + 98) — egyszer az új felhasználó folyamaton belül, és egyszer feltétel nélkül
3. **Hardcoded URL-ek**: Minden szerver URL a kódban van megadva, nincs konfigurációs fájl
4. **Szinkron hívások**: Minden REST hívás szinkron (blokkoló) — háttérszálról fut (`new Thread()` a `Main.java`-ban)
