# Play – Applicazione didattica JavaFX + SQLite + Telegram Bot

[![Java](https://img.shields.io/badge/Java-17-blue.svg)](https://www.oracle.com/java/) [![License](https://img.shields.io/badge/license-MIT-green.svg)](./LICENSE)

## Indice
1. [Descrizione](#descrizione)  
2. [Caratteristiche](#caratteristiche)  
3. [Requisiti](#requisiti)  
4. [Installazione ed esecuzione](#installazione-ed-esecuzione)  
   - [Windows](#windows)  
   - [Linux (Debian-based)](#linux-debian-based)  
5. [Telegram Bot](#telegram-bot)  
6. [Risorse](#risorse)  
7. [Licenza](#licenza)  

---

## Descrizione
Applicativo JavaFX realizzato per il corso di Informatica per il Management (UNIBO), pensato per imparare le basi della programmazione tramite quiz supportati da SQLite e un’interfaccia Telegram Bot.

---

## Caratteristiche
- Quiz a scelta multipla e aperta  
- Salvataggio e recupero del progresso utente con punteggi  
- Shutdown hook per salvataggio automatico prima della chiusura  
- Telegram Bot integrato con comandi utili  

---

## Requisiti
- Java 17 o superiore  
- JavaFX 24  
- Driver JDBC per SQLite  
- (Opzionale) Token Telegram Bot  

---

## Installazione ed esecuzione

### Windows
```bash
# Build del JAR
mvn clean package

# Esecuzione
java --module-path Path\to\javafx-sdk-24\lib \
     --add-modules javafx.controls,javafx.fxml \
     -jar target/Play-1.0-SNAPSHOT.jar
```
### Linux (Debian-based)
```
sudo dpkg --add-architecture i386
sudo apt update
sudo apt install libgtk-3-0 libxslt1.1 libxxf86vm1 -y

java --module-path /usr/share/openjfx/lib \
     --add-modules javafx.controls,javafx.fxml \
     -jar target/Play-1.0-SNAPSHOT.jar
```

### Struttura finale del JAR:
```
Play-1.0-SNAPSHOT.jar
├── META-INF/
├── com/matteorossi/play/...
├── data/database.db (embedded)
└── lib/ (dipendenze JavaFX per Linux)
```
Telegram Bot

Il bot ufficiale è @PlayUniversità, offre i seguenti comandi:

 - /ID – mostra il tuo ID Telegram

 - /reset – reset della tua password

 - /help – elenco dei comandi disponibili
Nota: in questa versione pubblica non è incluso il token del bot.
Per usarlo, crea un bot con BotFather e inserisci il token nelle impostazioni dell’app.

### Risorse
Scarica JavaFX [qui](https://openjfx.io/)
