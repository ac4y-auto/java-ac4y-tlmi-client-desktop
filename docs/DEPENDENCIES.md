# TLMI Tolmács App - Függőségek Dokumentáció

## 📦 Összes Függőség Áttekintés

**Összesen**: 19 függőség
- **Android Standard**: 5 db
- **Külső Open Source**: 1 db (WebSocket)
- **AC4Y Proprietary**: 10 db
- **Test Libraries**: 3 db

---

## 🔧 Build Konfiguráció

### Gradle Version
- **Android Gradle Plugin**: 3.3.1 (2019. január)
- **Gradle Wrapper**: 4.10.1 (gradle/wrapper/gradle-wrapper.properties)

### SDK Verziók
```gradle
compileSdkVersion 28    // Android 9.0 Pie (2018)
minSdkVersion 25        // Android 7.1 Nougat (2016)
targetSdkVersion 28     // Android 9.0 Pie
```

### Repository-k
```gradle
repositories {
    google()                                          // Google Maven (Android libs)
    jcenter()                                         // JCenter (deprecated since 2021)
    maven {
        url "https://maven.ac4y.com/repository/internal"  // AC4Y proprietary
    }
}
```

---

## 📚 Android Standard Libraries (5 db)

### 1. **AppCompat v7** - `com.android.support:appcompat-v7:28.0.0`
**Cél**: Backward compatibility Android verziók között
**Használat**:
- `AppCompatActivity` - MainActivity ősosztály
- Material Design komponensek
- Toolbar, ActionBar támogatás

**Alternatíva (modern)**: `androidx.appcompat:appcompat:1.6.1`

---

### 2. **Support Compat** - `com.android.support:support-compat:28.0.0`
**Cél**: Core compatibility features
**Használat**:
- `ConfigurationCompat` - Locale detection (nyelvfelismerés)
  ```java
  Locale locale = ConfigurationCompat.getLocales(
      Resources.getSystem().getConfiguration()).get(0);
  ```
- Permission handling
- Notification compat

**Megjegyzés**: Kommentált alpha verzió (`28.0.0-alpha1`) helyett stable használata

---

### 3. **Design Library** - `com.android.support:design:28.0.0`
**Cél**: Material Design komponensek
**Használat**:
- `BottomNavigationView` - Alsó navigációs sáv
  ```java
  BottomNavigationView navigation = findViewById(R.id.navigation);
  navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
  ```
- FloatingActionButton (ha használva van)
- Snackbar, TabLayout

**Alternatíva (modern)**: `com.google.android.material:material:1.11.0`

---

### 4. **ConstraintLayout** - `com.android.support.constraint:constraint-layout:1.1.3`
**Cél**: Responsive, flat view hierarchy layout
**Használat**:
- `activity_main.xml` layout rendszer
- Komplex UI-k egyszerűbb kóddal
- Jobb performance mint nested LinearLayout-ok

**Előnyök**:
- Flat hierarchy → Gyorsabb rendering
- Visual Layout Editor támogatás
- Responsive design egyszerűen

**Alternatíva (modern)**: `androidx.constraintlayout:constraintlayout:2.1.4`

---

### 5. **Local libs** - `fileTree(dir: 'libs', include: ['*.jar'])`
**Cél**: Lokális JAR fájlok betöltése
**Használat**: `app/libs/` mappában lévő JAR-ok (jelenleg valószínűleg üres)

---

## 🌐 Külső Open Source Library (1 db)

### **Java-WebSocket** - `org.java-websocket:Java-WebSocket:1.3.0`
**Verzió**: 1.3.0 (2016. május 24.)
**GitHub**: https://github.com/TooTallNate/Java-WebSocket
**License**: MIT

**Cél**: WebSocket client implementáció
**Használat**:
```java
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

WebSocketClient mWebSocketClient = new WebSocketClient(new URI(url)) {
    @Override
    public void onOpen(ServerHandshake handshakedata) {
        // Kapcsolat nyitva
    }

    @Override
    public void onMessage(String message) {
        // Üzenet fogadása
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        // Kapcsolat lezárva
    }

    @Override
    public void onError(Exception ex) {
        // Hiba
    }
};
```

**Használat az appban**:
- `wss://www.ac4y.com:2222/{userId}` - Secure WebSocket kapcsolat
- Real-time üzenetküldés a felek között
- JSON alapú kommunikáció (Gson-nal)

**Jelenlegi verzió**: 1.5.4 (2023) - Frissítés ajánlott!

---

## 🏢 AC4Y Proprietary Libraries (10 db)

Ezek a library-k a **maven.ac4y.com/repository/internal** repository-ból töltődnek le.

### AC4Y Core Infrastructure (6 db)

#### 1. **ac4yEnvironment** - `ac4y.environment:ac4yEnvironment:1.20190226.1`
**Verzió dátum**: 2019. február 26.
**Cél**: Környezeti változók és konfiguráció kezelés
**Feltételezett funkciók**:
- Environment variable management
- Configuration storage
- Application settings

---

#### 2. **ac4yClass** - `ac4y.base:ac4yClass:1.20190127.1`
**Verzió dátum**: 2019. január 27.
**Cél**: Alap domain osztályok
**Használat**:
```java
import ac4y.base.domain.Ac4y;
```
**Feltételezett funkciók**:
- Base domain model
- Common utility classes
- Shared constants

---

#### 3. **ac4yCommandDomain** - `ac4y.command:ac4yCommandDomain:1.20190202.1`
**Verzió dátum**: 2019. február 2.
**Cél**: Command Pattern base implementation
**Használat**:
```java
import ac4y.command.domain.Ac4yCommand;
```
**Funkciók**:
- Command interface/abstract class
- Command execution framework
- Command registry

---

#### 4. **ac4yMessageCommandDomain** - `ac4y.command:ac4yMessageCommandDomain:1.20190202.1`
**Verzió dátum**: 2019. február 2.
**Cél**: Message command specific domain
**Használat**:
```java
import ac4y.command.message.domain.Ac4yCMDMessage;
```
**Funkciók**:
- Message wrapper command
- Payload encapsulation
- Message routing

**Példa szerkezet**:
```json
{
  "commandName": "MESSAGE",
  "payload": { ... }
}
```

---

#### 5. **ac4yServiceDomain** - `ac4y.service:ac4yServiceDomain:1.20190228.1`
**Verzió dátum**: 2019. február 28.
**Cél**: Service layer domain modellek
**Funkciók**:
- Service request/response models
- DTO objects
- Service contracts

---

#### 6. **ac4yServiceCommand** - `ac4y.service:ac4yServiceCommand:1.20190228.1`
**Verzió dátum**: 2019. február 28.
**Cél**: Service command objects
**Használat**:
```java
import ac4y.command.service.domain.Ac4yCMDServiceResponse;
```
**Funkciók**:
- Service response commands
- Service error handling
- Service result wrapping

---

### AC4Y Gate Service (1 db) - ⭐ ÚJ az an-tolmi-client-ben!

#### 7. **ac4yGateService** - `ac4y.gate:ac4yGateService:1.20190311.2`
**Verzió dátum**: 2019. március 11. (második release)
**Cél**: User registration és authentication
**Használat**:
```java
import ac4y.gate.service.client.Ac4yGateServiceClient;
import ac4y.gate.service.domain.GateInsertUserRequest;
import ac4y.gate.service.domain.GateInsertUserResponse;
import ac4y.gate.service.domain.GateLoginRequest;
import ac4y.gate.service.domain.GateLoginResponse;
```

**Funkciók**:
- **GateInsertUserRequest**: Új felhasználó regisztráció
  ```java
  {
    name: "User123",
    deviceId: "uuid",
    language: "hu_HU"
  }
  ```
- **GateLoginRequest**: Felhasználó bejelentkezés
  ```java
  {
    deviceId: "uuid"
  }
  ```
- **Ac4yGateServiceClient**: HTTP client a Gate Service-hez
- Device UUID alapú azonosítás

**Backend endpoint** (feltételezett): `https://gate.ac4y.com`

---

### TLMI Specific Libraries (3 db)

#### 8. **tlmiUserDomain** - `tlmi.user:tlmiUserDomain:1.20190311.1`
**Verzió dátum**: 2019. március 11.
**Cél**: TLMI felhasználó domain modellek
**Használat**:
```java
import tlmi.user.domain.TlmiTranslateUser;
```

**TlmiTranslateUser modell**:
```java
{
    String id;              // User ID
    String name;            // Felhasználó név
    String language;        // Nyelv (pl. "hu_HU")
    String avatar;          // Base64 encoded image
    boolean online;         // Online státusz
}
```

---

#### 9. **tlmiUserClient** - `tlmi.user:tlmiUserClient:1.20190309.1`
**Verzió dátum**: 2019. március 9.
**Cél**: User service HTTP client
**Használat**:
```java
import tlmi.user.service.client.TlmiUserServiceClient;
import tlmi.user.service.domain.GetAllTranslateUsersResponse;
import tlmi.user.service.domain.GetTranslateUserByNameRequest;
import tlmi.user.service.domain.GetTranslateUserByNameResponse;
import tlmi.user.service.domain.InsertUserRequest;
import tlmi.user.service.domain.InsertUserResponse;
```

**API végpontok**:
- **GetAllTranslateUsers**: Összes aktív felhasználó listázása
  ```java
  GetAllTranslateUsersResponse {
      List<TlmiTranslateUser> users;
  }
  ```
- **GetTranslateUserByName**: Felhasználó keresés név alapján
- **InsertUser**: Új felhasználó beszúrás

**Backend endpoint** (feltételezett): `https://client.ac4y.com`

---

#### 10. **tlmiClient** - `tlmi.client:tlmiClient:1.20190301.1`
**Verzió dátum**: 2019. március 1.
**Cél**: Translation service client
**Használat**:
```java
import tlmi.service.client.TlmiServiceClient;
import tlmi.service.domain.Text2TextRequest;
import tlmi.service.domain.Text2TextResponse;
```

**Text2Text API**:
```java
Text2TextRequest {
    String sourceLanguage;      // "hu_HU"
    String targetLanguage;      // "en_US"
    String text;                // "Szia, hogy vagy?"
}

Text2TextResponse {
    String translatedText;      // "Hi, how are you?"
}
```

**Backend endpoint** (feltételezett): `https://api.ac4y.com`

---

## 🧪 Test Libraries (3 db)

### 1. **JUnit** - `junit:junit:4.12`
**Cél**: Unit testing framework
**Scope**: `testImplementation` (csak unit test-ekhez)

### 2. **Android Test Runner** - `com.android.support.test:runner:1.0.2`
**Cél**: Android instrumentation tests
**Scope**: `androidTestImplementation` (csak eszköz tesztekhez)

### 3. **Espresso** - `com.android.support.test.espresso:espresso-core:3.0.2`
**Cél**: UI testing framework
**Scope**: `androidTestImplementation`

---

## 📊 Függőségi Gráf - Hierarchia

```
Application (tlmi.communcator.atlmiclient)
│
├─── Android Support Libraries (Presentation layer)
│    ├── appcompat-v7:28.0.0
│    ├── support-compat:28.0.0
│    ├── design:28.0.0
│    └── constraint-layout:1.1.3
│
├─── Network Layer
│    └── Java-WebSocket:1.3.0
│
├─── AC4Y Core (Business logic)
│    ├── ac4yEnvironment:1.20190226.1
│    ├── ac4yClass:1.20190127.1
│    ├── ac4yCommandDomain:1.20190202.1
│    ├── ac4yMessageCommandDomain:1.20190202.1
│    ├── ac4yServiceDomain:1.20190228.1
│    └── ac4yServiceCommand:1.20190228.1
│
├─── AC4Y Services (External integrations)
│    ├── ac4yGateService:1.20190311.2      [User auth]
│    ├── tlmiUserDomain:1.20190311.1       [User models]
│    ├── tlmiUserClient:1.20190309.1       [User API]
│    └── tlmiClient:1.20190301.1           [Translation API]
│
└─── Testing
     ├── junit:4.12
     ├── android.test.runner:1.0.2
     └── espresso-core:3.0.2
```

---

## ⚠️ Függőségi Problémák és Kockázatok

### 1. **Elavult verziók**
| Library | Projekt verzió | Jelenlegi verzió | Évek lemaradás |
|---------|---------------|------------------|----------------|
| Android Support | 28.0.0 (2018) | **AndroidX** (2018+) | 7 év |
| Java-WebSocket | 1.3.0 (2016) | 1.5.4 (2023) | 7 év |
| Gradle Plugin | 3.3.1 (2019) | 8.x (2024) | 5 év |
| JUnit | 4.12 (2014) | 5.10.1 (2023) | 9 év |

### 2. **AC4Y Proprietary Dependencies**
⚠️ **Kritikus kockázat**: 10 library a `maven.ac4y.com` repository-ból
- Repository elérhetősége **ismeretlen**
- Nincs nyilvános dokumentáció
- Forrás kód nem elérhető
- Build sikeres **csak ha a maven.ac4y.com elérhető**

### 3. **JCenter Deprecation**
⚠️ JCenter repository **2021-ben megszűnt**
```gradle
jcenter()  // ❌ DEPRECATED - 2021. május 1-től read-only
```
**Megoldás**: Migrálás `mavenCentral()`-ra

### 4. **Android Support → AndroidX**
⚠️ Support Library **2018 óta deprecated**
```gradle
// Régi (deprecated)
com.android.support:appcompat-v7:28.0.0

// Új (2018+)
androidx.appcompat:appcompat:1.6.1
```

---

## 🚀 Modernizációs Javaslatok

### 1. **AndroidX Migráció**
```gradle
// Előtte
implementation 'com.android.support:appcompat-v7:28.0.0'
implementation 'com.android.support:design:28.0.0'

// Utána
implementation 'androidx.appcompat:appcompat:1.6.1'
implementation 'com.google.android.material:material:1.11.0'
```

### 2. **Gradle Frissítés**
```gradle
// build.gradle (project)
classpath 'com.android.tools.build:gradle:8.2.2'

// gradle-wrapper.properties
distributionUrl=https://services.gradle.org/distributions/gradle-8.2-all.zip
```

### 3. **Repository Frissítés**
```gradle
repositories {
    google()
    mavenCentral()  // jcenter() helyett
    maven {
        url "https://maven.ac4y.com/repository/internal"
    }
}
```

### 4. **Dependency Verziók**
```gradle
implementation 'org.java-websocket:Java-WebSocket:1.5.4'
testImplementation 'junit:junit:5.10.1'
androidTestImplementation 'androidx.test:runner:1.5.2'
androidTestImplementation 'androidx.test.espresso:espresso-core:3.5.1'
```

### 5. **AC4Y Libraries Kezelés**
**Opciók**:
- ✅ **Local JARs**: AC4Y library-kat `libs/` mappába másolni
- ✅ **Maven Local**: Ha van lokális Maven repository
- ✅ **Reverse Engineering**: Decompile és újra implementálás
- ❌ **Remote Maven**: Csak ha `maven.ac4y.com` elérhető

---

## 📝 Összefoglalás

### Erősségek:
✅ Jól strukturált dependency management
✅ Világos separation of concerns (Core, Service, Domain)
✅ WebSocket modern real-time kommunikációhoz

### Gyengeségek:
❌ **7 éves technológiai lemaradás**
❌ **10 proprietary dependency** kritikus kockázat
❌ **JCenter deprecated** repository
❌ **Android Support deprecated** (AndroidX kell)

### Build Sikeres Feltételek:
1. ✅ Android SDK 28 telepítve
2. ✅ Gradle 4.10.1+ wrapper
3. ⚠️ **maven.ac4y.com elérhető** (KRITIKUS!)
4. ✅ Internet kapcsolat

---

**Dokumentáció készült**: 2026-02-03
**Projekt verzió**: 1.20190312.1
**Függőségek száma**: 19 (10 proprietary)
**Státusz**: Legacy, modernizáció ajánlott
