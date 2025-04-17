package com.matteorossi.play.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Classe per la gestione delle operazioni DDL (Data Definition Language) del database.
 * Contiene metodi per la creazione ed eliminazione di tabelle, utilizzati principalmente
 * durante l'inizializzazione dell'applicazione.
 *
 * <p>Responsabilità principali:</p>
 * <ul>
 *   <li>Creazione dello schema del database con tutte le tabelle necessarie</li>
 *   <li>Eliminazione completa dello schema per ripristino iniziale</li>
 *   <li>Gestione delle relazioni tra tabelle tramite foreign key</li>
 * </ul>
 *
 * @see DatabaseConnection Classe per la gestione della connessione al database
 */
public class ClaseDLLDAO {

    // Gestione degli errori
    private static final Logger logger = LoggerFactory.getLogger(ClaseDLLDAO.class);

    /**
     * Crea la tabella 'users' con i campi:
     * <ul>
     *   <li>id (PK autoincrement)</li>
     *   <li>username (univoco)</li>
     *   <li>password</li>
     *   <li>Dati anagrafici</li>
     *   <li>telegram_id</li>
     *   <li>flag reset password</li>
     * </ul>
     */
    public static void createTableUser() {
        String query = """
                CREATE TABLE IF NOT EXISTS users (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    username TEXT NOT NULL UNIQUE,
                    password TEXT NOT NULL,
                    first_name TEXT NOT NULL,
                    last_name TEXT NOT NULL,
                    telegram_id TEXT NOT NULL,
                    isReset BOOLEAN NOT NULL DEFAULT 0
                );
                """;
        executeQuery(query, "Utente");
    }

    /**
     * Crea la tabella 'admins' per gli amministratori:
     * <ul>
     *   <li>id (PK autoincrement)</li>
     *   <li>username (univoco)</li>
     *   <li>password</li>
     *   <li>Dati anagrafici</li>
     * </ul>
     */
    public static void createTableAdmin() {
        String query = """
                CREATE TABLE IF NOT EXISTS admins (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    username TEXT NOT NULL UNIQUE,
                    password TEXT NOT NULL,
                    first_name TEXT NOT NULL,
                    last_name TEXT NOT NULL
                );
                """;
        executeQuery(query, "Admin");
    }

    /**
     * Crea la tabella 'languages' per i linguaggi di programmazione:
     * <ul>
     *   <li>id (PK autoincrement)</li>
     *   <li>name (univoco)</li>
     * </ul>
     */
    public static void createTableLanguages() {
        String query = """
                CREATE TABLE IF NOT EXISTS languages (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL UNIQUE
                );
                """;
        executeQuery(query, "Languages");
    }

    /**
     * Crea la tabella 'themes' per le categorie di domande:
     * <ul>
     *   <li>id (PK autoincrement)</li>
     *   <li>name (univoco)</li>
     * </ul>
     */
    public static void createTableThemes() {
        String query = """
                CREATE TABLE IF NOT EXISTS themes (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL UNIQUE
                );
                """;
        executeQuery(query, "Themes");
    }

    /**
     * Crea la tabella 'difficulties' per i livelli di difficoltà:
     * <ul>
     *   <li>id (PK autoincrement)</li>
     *   <li>name (univoco)</li>
     *   <li>levelDifficulty (univoco)</li>
     * </ul>
     */
    public static void createTableDifficulties() {
        String query = """
                CREATE TABLE IF NOT EXISTS difficulties (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL UNIQUE,
                levelDifficulty INTEGER NOT NULL UNIQUE
                );
                """;
        executeQuery(query, "Difficulties");
    }

    /**
     * Crea la tabella 'questions' per le domande:
     * <ul>
     *   <li>Collegamenti a language, theme e difficulty</li>
     *   <li>Supporto per domande multiple e a codice</li>
     *   <li>Vincoli CHECK sul tipo di domanda</li>
     *   <li>Foreign key su tutte le entità correlate</li>
     * </ul>
     */
    public static void createTableQuestions() {
        String query = """
                CREATE TABLE IF NOT EXISTS questions (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    language_id INTEGER NOT NULL, -- Riferimento al linguaggio
                    theme_id INTEGER NOT NULL, -- Riferimento al tema
                    difficulty_id INTEGER NOT NULL, -- Riferimento alla difficoltà
                    question_type TEXT NOT NULL CHECK (question_type IN ('multiple_choice', 'code')), -- Tipo di domanda
                    question TEXT NOT NULL,
                    option_a TEXT, -- Solo per scelta multipla
                    option_b TEXT,
                    option_c TEXT,
                    option_d TEXT,
                    correct_option TEXT, -- Solo per scelta multipla
                    code_solution TEXT, -- Solo per domande con codice
                    FOREIGN KEY (language_id) REFERENCES languages (id),
                    FOREIGN KEY (theme_id) REFERENCES themes (id),
                    FOREIGN KEY (difficulty_id) REFERENCES difficulties (id)
                );
                """;
        executeQuery(query, "Questions");
    }

    /**
     * Crea la tabella 'user_progress' per tracciare i progressi:
     * <ul>
     *   <li>Collegamento all'utente</li>
     *   <li>Riferimenti a language, theme e difficulty</li>
     *   <li>Punteggio accumulato</li>
     *   <li>Vincolo unique su combinazione parametri</li>
     * </ul>
     */
    public static void createTableUserProgress() {
        String query = """
                CREATE TABLE IF NOT EXISTS user_progress (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    user_id INTEGER NOT NULL, -- Riferimento all'utente
                    language_id INTEGER NOT NULL, -- Riferimento al linguaggio
                    theme_id INTEGER NOT NULL, -- Riferimento alla categoria
                    difficulty_id INTEGER NOT NULL, -- Riferimento alla difficoltà
                    score INTEGER DEFAULT 0, -- Punteggio accumulato
                    FOREIGN KEY (user_id) REFERENCES users (id),
                    FOREIGN KEY (language_id) REFERENCES languages (id),
                    FOREIGN KEY (theme_id) REFERENCES themes (id),
                    FOREIGN KEY (difficulty_id) REFERENCES difficulties (id),
                    CONSTRAINT unique_progress UNIQUE (user_id, theme_id, language_id, difficulty_id)
                );
                """;
        executeQuery(query, "User Progress");
    }

    /**
     * Crea la tabella 'user_answers' per le risposte degli utenti:
     * <ul>
     *   <li>Registrazione risposte corrette/errate</li>
     *   <li>Vincolo unique su utente+domanda</li>
     * </ul>
     */
    public static void userAnswers() {
        String query = """
                CREATE TABLE IF NOT EXISTS user_answers (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    user_id INTEGER NOT NULL, -- Riferimento all'utente
                    question_id INTEGER NOT NULL, -- Riferimento alla domanda
                    is_correct BOOLEAN NOT NULL, -- Indica se la risposta è corretta
                    FOREIGN KEY (user_id) REFERENCES users (id),
                    FOREIGN KEY (question_id) REFERENCES questions (id),
                    CONSTRAINT unique_answer UNIQUE (user_id, question_id)
                );
                """;
        executeQuery(query, "User Answers");
    }


    /**
     * Elimina tutte le tabelle del database in ordine sicuro:
     * <ol>
     *   <li>Disabilita i vincoli di foreign key</li>
     *   <li>Elimina tabelle in ordine di dipendenza</li>
     *   <li>Riabilita i vincoli</li>
     * </ol>
     */
    public static void deleteAllTables() {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            // Disattivo i vincoli per evitare errori durante l'eliminazione delle tabelle con chiavi esterne
            stmt.executeUpdate("PRAGMA foreign_keys = OFF;");

            // Elimino le tabelle in ordine
            stmt.executeUpdate("DROP TABLE IF EXISTS user_progress;");
            stmt.executeUpdate("DROP TABLE IF EXISTS questions;");
            stmt.executeUpdate("DROP TABLE IF EXISTS difficulties;");
            stmt.executeUpdate("DROP TABLE IF EXISTS themes;");
            stmt.executeUpdate("DROP TABLE IF EXISTS languages;");
            stmt.executeUpdate("DROP TABLE IF EXISTS admins;");
            stmt.executeUpdate("DROP TABLE IF EXISTS users;");

            // Riattivo i vincoli
            stmt.executeUpdate("PRAGMA foreign_keys = ON;");

            System.out.println("Tutte le tabelle sono state eliminate con successo.");
        } catch (SQLException e) {
            logger.error("Errore durante l'eliminazione delle tabelle: {}", e.getMessage());
        }
    }

    // Metodo per creare tutte le tabelle
    public static void generateDB() {
        try {
            createTableUser();
            createTableAdmin();
            createTableLanguages();
            createTableThemes();
            createTableDifficulties();
            createTableQuestions();
            createTableUserProgress();
            userAnswers();

            System.out.println("Tutte le tabelle sono state create con successo.");
        } catch (Exception e) {
            logger.error("Errore durante la creazione delle tabelle: {}", e.getMessage());
        }
    }

    // Metodo di utilità per eseguire le query di creazione delle tabelle
    private static void executeQuery(String query, String tableName) {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate(query);
            System.out.println("Table " + tableName + " created successfully.");
        } catch (SQLException e) {
            logger.error("Errore nella creazione della tabella " + tableName + ": {}", e.getMessage());
        }
    }
}
