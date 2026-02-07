# 003 - Swing UI felepites

**Datum:** 2026-02-06

## Uj projekt struktura (src/main/java)

```
tlmi.communcator.atlmiclient/
  Main.java                   - belepesi pont (main metodus)
  MainDemo.java               - UI demo mock adatokkal (fuggoseg nelkul)
  MainController.java         - uzleti logika (MainActivity-bol kinyerve)
  AppEnvironmentVariableHandler.java - HashMap-alapu konfiguracio
  model/                      - valtozatlan POJO-k
  command/                    - valtozatlan domain/algebra osztalyok
  ui/
    MainFrame.java            - fo Swing ablak (JFrame)
    StatusBar.java            - 5 szines allapotjelzo (NET, LOGIN, WS, STT, TTS)
    PartnerListPanel.java     - partner lista (JList + PartnerInfo)
    ChatPanel.java            - chat buborekos uzenetek + beviteli mezo
    LogPanel.java             - naplo (legujabb elol)
    ImageUtil.java            - Base64 -> BufferedImage konverzio
  utility/
    ScreenMessageHandler.java - JOptionPane-alapu hibauzenet
```

## MainFrame felepites

```
+------------------------------------------+
| [NET] [LOGIN] [WS] [STT] [TTS]          |  <- StatusBar
+------------------------------------------+
| [avatar] TestUser (hu)  connected  [avatar] Partner (en) |  <- user info
+------------------------------------------+
|                                          |
|  CardLayout-tal valtogathato teruletek:  |
|    - CARD_PARTNERS: PartnerListPanel     |
|    - CARD_CHAT: ChatPanel                |
|    - CARD_LOG: LogPanel                  |
|                                          |
+------------------------------------------+
|     [Partners]  [Chat]  [Log]            |  <- navigacios gombok
+------------------------------------------+
```

## ChatPanel

- Felso resz: gorditheto chat buborekos megjelenitovel
  - Bejovo uzenet: szurke buborek, balra igazitva
  - Kimeno uzenet: kek buborek, jobbra igazitva
- Also resz: JTextField + Send gomb (Enter is kuldi)

## PartnerListPanel

- PartnerInfo belso osztaly (name, humanName, avatar) - fuggetlenitve a TlmiTranslateUser-tol
- JList custom cellrendererrel (avatar kep + nev)
- Kattintasra partner kivalasztas

## StatusBar

- 5 JLabel, szines hatterrel: zold (ok), piros (hiba), szurke (inaktiv)
- Publikus API: internetLive(), internetError(), loginLive(), stb.
