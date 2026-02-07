# TLMI Tolmács Alkalmazás - Funkciók Dokumentáció

## 📋 Projekt Áttekintés

**Projekt neve**: an-tolmi-client (Android TLMI Client)
**Verzió**: 1.20190312.1 (versionCode: 7)
**Utolsó módosítás**: 2019. március 13.
**Cél**: Real-time, kétirányú automatikus tolmács/fordító alkalmazás WebSocket alapú kommunikációval

---

## 🎯 Az Alkalmazás Fő Célja

A **TLMI (Translation/Interpretation) Communicator** egy Android alapú real-time tolmács alkalmazás, amely lehetővé teszi két különböző nyelvű felhasználó közötti **zökkenőmentes beszélgetést** a következő technológiák kombinálásával:

1. **Speech-to-Text** (Beszédfelismerés) - Az egyik fél beszédének szöveggé alakítása
2. **Text-to-Text Translation** (Automatikus fordítás) - A szöveg lefordítása a másik fél nyelvére
3. **Text-to-Speech** (Beszédszintézis) - A lefordított szöveg felolvasása a másik félnek
4. **WebSocket Real-time Communication** - Azonnali üzenetküldés a felek között

---

## 🏗️ Architektúra & Funkcionális Komponensek

### 1. **Core Activity - MainActivity.java** (1051 sor)

#### Felelősségek:
- Az alkalmazás központi vezérlőegysége
- UI komponensek inicializálása és kezelése
- WebSocket kapcsolat menedzselése
- Beszédfelismerés és szintézis koordinálása
- Felhasználói interakciók kezelése

#### Főbb funkciók:
```java
onCreate() - Alkalmazás inicializálás
- ScreenSupport létrehozása (UI binding)
- VisualIndicatorBar inicializálás (5 állapotjelző)
- SpeechRecognizer és Synthesizer létrehozása
- WebSocket kliens setup
- Engedélyek kérése (RECORD_AUDIO)
- Network állapot ellenőrzés
```

#### Függőségek:
- **Android SDK**:
  - `SpeechRecognizer` - Beszédfelismerés
  - `TextToSpeech` - Beszédszintézis
  - `WebSocketClient` - Real-time kommunikáció
  - `ConnectivityManager` - Hálózat állapot
  - `TelephonyManager` - Eszköz azonosítás

- **AC4Y Proprietary Libraries**:
  - `ac4y.gate:ac4yGateService` - Felhasználó regisztráció és login
  - `ac4y.service:ac4yServiceDomain` - Szolgáltatás domain modellek
  - `tlmi.user:tlmiUserDomain` - TLMI felhasználó modellek
  - `tlmi.user:tlmiUserClient` - Felhasználó szolgáltatás kliens
  - `tlmi.client:tlmiClient` - TLMI core kliens

---

### 2. **Visual Indicator System** (indicator/ package)

Az alkalmazás **állapotjelző rendszere**, amely vizuális feedback-et ad a felhasználónak a különböző komponensek állapotáról.

#### 2.1 **TlmiVisualIndicatorBar.java**
Központi indikátor manager, amely 5 különböző állapotot jelenít meg:

| Indikátor | Cél | Értékek |
|-----------|-----|---------|
| **Recognition** | Beszédfelismerés állapota | Waiting, Under Speech, After Speech, Processing, End |
| **Synthesizer** | TTS állapot | Active/Inactive |
| **Internet** | Internet kapcsolat | Connected/Disconnected |
| **WebSocket** | WebSocket kapcsolat | Connected/Disconnected |
| **Login** | Felhasználó bejelentkezve | Logged In/Logged Out |

**Függőségek**:
- `ScreenSupport` - UI elemek referenciái
- `VisualIndicator` - Általános indikátor implementáció
- `RecognitionVisualIndicator` - Speciális beszédfelismerés indikátor

#### 2.2 **RecognitionVisualIndicator.java**
Beszédfelismerés állapotának vizualizálása színkódokkal:

```java
WAITING_4_SPEECH = #2196F3 (kék)    // Várakozás beszédre
UNDER_SPEECH     = #4CAF50 (zöld)   // Beszéd folyamatban
AFTER_SPEECH     = #F7C329 (sárga)  // Beszéd után
PROCESS_SPEECH   = #2196F3 (kék)    // Feldolgozás alatt
END_SPEECH       = #FF9800 (narancs) // Vége
```

**Speciális funkciók**:
- `sinceLastChange()` - Utolsó állapotváltás óta eltelt idő
- `earlyChange()` - < 300ms változás (spam szűrés)
- `stabilChange()` - > 500ms stabil állapot

**Cél**: Felhasználói élmény javítása - látható visszajelzés, hogy az app éppen mit csinál.

---

### 3. **Speech Components** (utility/ package)

#### 3.1 **Ac4ySpeechRecognizer.java**
Beszédfelismerés wrapper osztály, amely egyszerűsíti az Android SpeechRecognizer API használatát.

**Funkciók**:
```java
start()  - Beszédfelismerés indítása
stop()   - Beszédfelismerés leállítása
onResults() - Felismert szöveg fogadása
onError() - Hibakezelés (auto-restart ERROR_NO_MATCH esetén)
```

**Automatikus nyelvfelismerés**:
```java
Locale locale = ConfigurationCompat.getLocales(Resources.getSystem()
    .getConfiguration()).get(0);
String myLanguage = locale.getLanguage() + "_" + locale.getCountry();
```

**Konfiguráció**:
- `EXTRA_LANGUAGE_MODEL` - FREE_FORM (természetes beszéd)
- `EXTRA_MAX_RESULTS` - 1 (legjobb találat)
- `EXTRA_LANGUAGE` - Dinamikus nyelvbeállítás

**Függőségek**:
- Android `SpeechRecognizer` API
- `RecognizerIntent` - Felismerési szándék

**Cél**: Automatikus, folyamatos beszédfelismerés nyelvi agnosztikus módon.

#### 3.2 **Ac4ySpeechSynthesizer.java**
Text-to-Speech wrapper osztály a lefordított szövegek felolvasásához.

**Funkciók**:
```java
speak(String text) - Szöveg felolvasása
onCreateSuccess()  - TTS inicializálás sikeres
onCreateError()    - TTS inicializálás hiba
```

**Nyelvkezelés**:
```java
// Konstruktor opcionális nyelv paraméterrel
Ac4ySpeechSynthesizer(Context, String language, String country)
```

**Audio beállítások**:
- `AudioManager.STREAM_MUSIC` használata
- `QUEUE_FLUSH` - Előző beszéd megszakítása

**Függőségek**:
- Android `TextToSpeech` API
- `AudioManager` - Hang vezérlés

**Cél**: Partner nyelvén való felolvasás automatizálása.

#### 3.3 **Ac4yScreenMessageHandler.java**
Felhasználói üzenetek megjelenítése (Toast).

**Funkciók**:
```java
message(String)        - Normál üzenet (hosszú)
errorNotifying(String) - Piros háttérrel (hiba jelzés)
```

**Cél**: Konzisztens UI feedback a felhasználónak.

---

### 4. **UI Control Components** (control/ package)

#### 4.1 **ScreenSupport.java** (251 sor)
UI elemek centralizált referencia tárolója - minden view egy helyen.

**Managed Views**:
```java
// User interface elements
ListView partnerList        // Partner lista
ListView chatHistory        // Chat történet
ListView log               // Debug log
ListView keyValueListView  // Környezeti változók

// User information
TextView selfName          // Saját név
ImageView selfAvatar       // Saját avatar
TextView partnerName       // Partner neve
ImageView partnerAvatar    // Partner avatar
ImageView connection       // Kapcsolat ikon

// Visual indicators
TextView recognition       // Beszédfelismerés indikátor
TextView synthesizer       // TTS indikátor
TextView internet         // Internet indikátor
TextView websocket        // WebSocket indikátor
TextView login            // Login indikátor
```

**Függőségek**: Csak Android View komponensek

**Cél**: Separation of concerns - UI binding elkülönítése az üzleti logikától.

#### 4.2 **ChatHistoryViewHandler.java**
Chat üzenetek megjelenítésének kezelése.

**Funkciók**:
- Bejövő/kimenő üzenetek külön megjelenítése
- Avatar kezelés (Base64 dekódolás)
- Időbélyeg megjelenítés

**Layout-ok**:
- `incoming_chat_row.xml` - Partner üzenetei
- `outgoing_chat_row.xml` - Saját üzeneteim

**Függőségek**:
- `AppEnvironmentVariableHandler` - Felhasználó info
- `ChatEvent` model
- `Ac4yImageHandler` - Base64 képkezelés

#### 4.3 **ObjectListViewHandler.java**
Partner lista kezelése (elérhető felhasználók).

**Funkciók**:
- Felhasználók listázása
- Avatar megjelenítés
- Partner kiválasztás kezelése

**Layout**: `partner_list_item.xml`

**Függőségek**:
- `TlmiTranslateUser` - Felhasználó model

#### 4.4 **ReverseLogAdapter.java**
Debug log fordított sorrendben (legújabb felül).

**Cél**: Fejlesztői debugging, valós idejű esemény követés.

---

### 5. **Command Pattern Implementation** (command/ package)

Az alkalmazás **Command Pattern**-t használ az üzenetek kezelésére - minden üzenettípus külön command objektum.

#### 5.1 **TlmiMessage.java**
Fordított szöveges üzenet parancs.

```java
commandName: "TLMIMESSAGE"
message: String  // Lefordított üzenet szövege
```

**Cél**: Partner által küldött, lefordított üzenet fogadása.

#### 5.2 **TlmiCMDInvitation.java**
Meghívás küldése egy partnernek.

```java
commandName: "TLMICMDINVITATION"
partner: String   // Partner neve
language: String  // Saját nyelv
```

**Cél**: Beszélgetés kezdeményezése.

**Workflow**:
1. Felhasználó kiválaszt egy partnert a listából
2. TlmiCMDInvitation parancs küldése WebSocket-en
3. Partner fogadja és dönthet az elfogadásról

#### 5.3 **TlmiCMDInvitationAccept.java**
Meghívás elfogadása.

```java
commandName: "TLMICMDINVITATIONACCEPT"
partner: String
language: String
```

**Cél**: Partner elfogadta a meghívást, beszélgetés indulhat.

**Workflow**:
1. Partner fogadja a TlmiCMDInvitation-t
2. Elfogadás esetén TlmiCMDInvitationAccept válasz
3. Kapcsolat létrejön, beszédfelismerés aktiválódik

---

### 6. **Data Models** (model/ package)

#### 6.1 **ChatEvent.java**
Chat üzenet modell.

```java
String message      // Üzenet szövege
String sender       // Küldő neve
String avatar       // Base64 encoded avatar
long timestamp      // Időbélyeg
boolean outgoing    // Saját üzenet-e
```

#### 6.2 **LogEvent.java**
Debug log bejegyzés.

```java
String message
long timestamp
```

#### 6.3 **KeyValue.java**
Környezeti változó.

```java
String key
String value
```

---

### 7. **Configuration & Environment**

#### 7.1 **AppEnvironmentVariableHandler.java**
SharedPreferences alapú konfiguráció kezelő.

**Tárolt értékek**:
```java
"self_name"         // Saját felhasználónév
"self_language"     // Saját nyelv
"self_avatar"       // Saját avatar (Base64)
"partner_name"      // Partner neve
"partner_language"  // Partner nyelve
"partner_avatar"    // Partner avatar
"device_id"         // Eszköz azonosító (UUID)
```

**Funkciók**:
```java
getValue(String key)
setValue(String key, String value)
getKeyValues() - Összes változó listázása
```

**Függőségek**:
- Android `SharedPreferences`

**Cél**: Perzisztens konfiguráció mentése, alkalmazás újraindításkor visszatöltés.

---

## 🔄 Működési Folyamat (User Flow)

### 1. **Alkalmazás Indítása**
```
1. MainActivity.onCreate()
2. Engedélyek kérése (RECORD_AUDIO)
3. Internet kapcsolat ellenőrzés
4. Visual Indicators inicializálás
5. SpeechRecognizer & Synthesizer létrehozása
6. AppEnvironmentVariableHandler betöltés
7. UI elemek setup (ScreenSupport)
```

### 2. **Felhasználó Regisztráció/Login**
```
1. Eszköz UUID generálás (vagy betöltés)
2. GateInsertUserRequest → Ac4yGateServiceClient
3. GateLoginRequest → Ac4yGateServiceClient
4. WebSocket kapcsolat nyitása: wss://www.ac4y.com:2222/{userId}
5. Login indikátor ZÖLD
```

**Függőségek**:
- `Ac4yGateServiceClient` (ac4y.gate:ac4yGateService:1.20190311.2)
- Backend: `https://gate.ac4y.com` (feltételezett)

### 3. **Partner Lista Betöltése**
```
1. GetAllTranslateUsersResponse → TlmiUserServiceClient
2. Partner lista ListView populálás
3. Avatar-ok Base64 dekódolása és megjelenítése
```

**Függőségek**:
- `TlmiUserServiceClient` (tlmi.user:tlmiUserClient:1.20190309.1)
- Backend: `https://client.ac4y.com` (feltételezett)

### 4. **Meghívás Küldése**
```
1. Felhasználó kiválaszt egy partnert
2. TlmiCMDInvitation létrehozása (partner, language)
3. Ac4yCMDMessage wrapper-be csomagolás
4. WebSocket send (JSON)
5. Várakozás TlmiCMDInvitationAccept-re
```

### 5. **Beszélgetés Folyamata**
```
┌─────────────────────────────────────────────────┐
│  Felhasználó A (magyar)                         │
├─────────────────────────────────────────────────┤
│ 1. SpeechRecognizer.start()                     │
│ 2. "Szia, hogy vagy?" → onResults()             │
│ 3. Recognition Indikátor: UNDER_SPEECH → AFTER  │
│ 4. Text2TextRequest → TlmiServiceClient         │
│    - sourceLanguage: "hu_HU"                    │
│    - targetLanguage: "en_US"                    │
│    - text: "Szia, hogy vagy?"                   │
│ 5. Text2TextResponse: "Hi, how are you?"       │
│ 6. TlmiMessage("Hi, how are you?") → WebSocket │
└─────────────────────────────────────────────────┘
                     ↓ WebSocket
┌─────────────────────────────────────────────────┐
│  Felhasználó B (angol)                          │
├─────────────────────────────────────────────────┤
│ 7. WebSocket onMessage()                        │
│ 8. TlmiMessage.process()                        │
│ 9. ChatEvent hozzáadása (incoming)             │
│10. TextToSpeech.speak("Hi, how are you?")      │
│11. Synthesizer Indikátor: ACTIVE               │
└─────────────────────────────────────────────────┘
```

**Függőségek**:
- `TlmiServiceClient` (tlmi.client:tlmiClient:1.20190301.1)
- Backend: `https://api.ac4y.com` (feltételezett fordítási API)

### 6. **Válasz Folyamata**
Ugyanez visszafelé: B beszél → fordítás → A hallja.

---

## 🌐 Backend Service Függőségek

Az alkalmazás **3 fő backend szolgáltatásra** támaszkodik:

### 1. **Gate Service** (ac4y.gate:ac4yGateService:1.20190311.2)
**URL**: `https://gate.ac4y.com` (feltételezett)
**Funkciók**:
- Felhasználó regisztráció (`GateInsertUserRequest`)
- Felhasználó login (`GateLoginRequest`)
- Eszköz azonosítás

**Request/Response modellek**:
```java
GateInsertUserRequest {
    String name
    String deviceId
    String language
}

GateLoginRequest {
    String deviceId
}

GateLoginResponse {
    String userId
    String token (?)
}
```

### 2. **User Service** (tlmi.user:tlmiUserClient:1.20190309.1)
**URL**: `https://client.ac4y.com` (feltételezett)
**Funkciók**:
- Összes aktív felhasználó lekérése
- Felhasználó keresés név alapján
- Felhasználó beszúrás

**Request/Response modellek**:
```java
GetAllTranslateUsersResponse {
    List<TlmiTranslateUser> users
}

TlmiTranslateUser {
    String id
    String name
    String language
    String avatar (Base64)
    boolean online
}
```

### 3. **Translation Service** (tlmi.client:tlmiClient:1.20190301.1)
**URL**: `https://api.ac4y.com` (feltételezett)
**Funkciók**:
- Szöveg fordítás (Text2Text)
- Nyelv detektálás (?)

**Request/Response modellek**:
```java
Text2TextRequest {
    String sourceLanguage  // pl. "hu_HU"
    String targetLanguage  // pl. "en_US"
    String text
}

Text2TextResponse {
    String translatedText
    float confidence (?)
}
```

### 4. **WebSocket Server**
**URL**: `wss://www.ac4y.com:2222/{userId}`
**Protocol**: WebSocket (secure)
**Format**: JSON

**Üzenettípusok**:
```json
// MESSAGE wrapper
{
  "commandName": "MESSAGE",
  "payload": {
    "commandName": "TLMIMESSAGE",
    "message": "Hi, how are you?"
  }
}

// INVITATION
{
  "commandName": "TLMICMDINVITATION",
  "partner": "user123",
  "language": "hu_HU"
}

// INVITATION ACCEPT
{
  "commandName": "TLMICMDINVITATIONACCEPT",
  "partner": "user456",
  "language": "en_US"
}
```

---

## 🔒 Engedélyek és Biztonság

### Android Engedélyek (AndroidManifest.xml)
```xml
<uses-permission android:name="android.permission.INTERNET"/>
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
<uses-permission android:name="android.permission.RECORD_AUDIO"/>
<uses-permission android:name="android.permission.MODIFY_AUDIO_SETTINGS"/>
```

### Futásidejű Engedélyek
- **RECORD_AUDIO** - Kérés `ActivityCompat.requestPermissions()` via

### Biztonsági Megjegyzések
- ⚠️ **StrictMode.permitAll()** használata - NEM production-ready (hálózati műveletek main thread-en)
- ⚠️ Nincs token-based authentication (csak userId alapú WebSocket)
- ⚠️ WebSocket SSL tanúsítvány validálás (?)
- ✅ Eszköz UUID alapú azonosítás

---

## 📊 Statisztikák

| Metrika | Érték |
|---------|-------|
| **Java fájlok** | 37 |
| **Összes kódsor** | ~3034 |
| **MainActivity** | 1051 sor |
| **MainActivityAlgebra** | 181 sor |
| **Layout fájlok** | 7 |
| **Dependencies** | 19 (10 AC4Y + 9 standard) |
| **Min SDK** | 25 (Android 7.1 Nougat) |
| **Target SDK** | 28 (Android 9.0 Pie) |
| **Version Code** | 7 |

---

## 🎨 UI Layouts

| Layout File | Cél |
|-------------|-----|
| `activity_main.xml` | Fő képernyő |
| `partner_list_item.xml` | Partner lista elem (avatar + név) |
| `incoming_chat_row.xml` | Bejövő chat üzenet bubble |
| `outgoing_chat_row.xml` | Kimenő chat üzenet bubble |
| `log_row.xml` | Debug log bejegyzés |
| `key_value_row.xml` | Környezeti változó sor |
| `key_value_listview_bottom_sheet.xml` | Bottom sheet környezeti változókhoz |

---

## 🐛 Ismert Problémák és Hiányosságok

### Kód Minőség
1. **StrictMode.permitAll()** - Hálózati műveletek main thread-en (rossz gyakorlat)
   - **Megoldás**: AsyncTask, Coroutines, vagy RxJava használata
2. **Nincs error handling** - WebSocket disconnection esetén nincs auto-reconnect
3. **Hardcoded URL-ek** - Backend endpoint-ok hardcoded a kódban
4. **Nincs offline mód** - Internet nélkül nem használható
5. **Memory leak veszély** - Activity context használata callback-ekben

### Backend Függőségek
- AC4Y proprietary library-k elérhetetlensége
- Backend szolgáltatások (gate, client, api) működésének bizonytalansága
- WebSocket szerver státusz ismeretlen

### Technológiai Elavultság
- Android Support Library (AndroidX-re kellene migrálni)
- Gradle 4.10.1 (2018-as)
- SDK 28 (2018-as, jelenlegi: 34+)

---

## 🚀 Továbbfejlesztési Lehetőségek

### Architekturális
1. **MVVM Pattern** implementálása (ViewModel + LiveData)
2. **Repository Pattern** a data layer-hez
3. **Dependency Injection** (Hilt/Dagger)
4. **Coroutines** használata async műveletekhez

### Funkcionális
1. **Offline mód** - Cached translation, queue
2. **Group chat** - Több mint 2 fél
3. **Message history** - Room database
4. **Push notifications** - Firebase Cloud Messaging
5. **Voice activity detection** - Automatikus beszéd kezdet/vég detektálás
6. **Noise cancellation** - Audio preprocessing

### UI/UX
1. **Material Design 3** frissítés
2. **Dark mode** támogatás
3. **Accessibility** javítás (TalkBack, stb.)
4. **Haptic feedback** beszéd detektáláskor

---

## 📚 Összefoglalás

Az **an-tolmi-client** egy **ambiciózus és jól strukturált** real-time tolmács alkalmazás, amely kombinál több komplex technológiát:

✅ **Erősségek**:
- Átgondolt architektúra (Command Pattern, Separation of Concerns)
- Visual Indicator System (kiváló UX)
- Modularizált komponensek
- Nyelvfüggetlen implementáció

❌ **Gyengeségek**:
- Proprietary backend függőségek
- Elavult technológiák
- Hiányzó error handling
- Production-readiness hiányosságok

🎯 **Cél**: Nyelvi határok lebontása real-time kommunikációval.

---

**Dokumentáció készült**: 2026-02-03
**Projekt dátuma**: 2019-03-04 - 2019-03-13
**Projekt státusz**: Legacy / Archivált
**Teljes funkcionalitás**: Backend függőségtől függ
