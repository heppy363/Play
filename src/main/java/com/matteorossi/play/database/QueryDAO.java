package com.matteorossi.play.database;

import com.matteorossi.play.models.*;
import com.matteorossi.play.telegram.TelegramBoot;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * Classe DAO (Data Access Object) per la gestione di tutte le operazioni di database.
 * Fornisce metodi per:
 * <ul>
 *   <li>Autenticazione e gestione utenti/admin</li>
 *   <li>CRUD per entità: linguaggi, temi, difficoltà, domande</li>
 *   <li>Gestione progressi utente e classifiche</li>
 *   <li>Operazioni statistiche e reportistica</li>
 * </ul>
 *
 * <p>Utilizza {@link BCrypt} per l'hashing delle password e {@link TelegramBoot} per le notifiche.</p>
 *
 * @see DatabaseConnection Gestione connessioni al database
 * @see PlayerRankingModel Modello per i dati della classifica
 */
public class QueryDAO {
    private static final Logger logger = LoggerFactory.getLogger(QueryDAO.class);

    //Query per il login del utente
    public static boolean loginUser(String username, String password) {
        String query = "SELECT password FROM users WHERE username = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            // Imposta il parametro della query
            pstmt.setString(1, username);

            // Esegui la query
            ResultSet rs = pstmt.executeQuery();

            // Verifica se l'utente esiste e se la password è corretta
            if (rs.next()) {
                String storedHashedPassword  = rs.getString("password"); // PSW cifrata
                return BCrypt.checkpw(password, storedHashedPassword );
            } else {
                return false; // L'utente non esiste
            }

        } catch (SQLException e) {
            logger.error("Errore nel login del utente {}", e.getMessage());
            return false;
        }
    }

    //QUery per ottenre l'id dal nome utente
    public static int getIDByUsername(String username) {
        int id = -1;
        String query = "SELECT id FROM users WHERE username = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)){

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                id = rs.getInt("id");
            }

        }catch (SQLException e){
            logger.error(e.getMessage());
        }

        return id;
    }

    //Query per verificare se lo userName e univoco
    public static boolean isUsernameUnique(String username) {
        String query = "SELECT COUNT(*) FROM users WHERE username = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            // Controlla il risultato
            if (rs.next()) {
                int count = rs.getInt(1);
                return count == 0; // Restituisce true se lo username è unico
            }
            return false;

        } catch (SQLException e) {
            logger.error("Errore nella verifica dello username univoco: {}", e.getMessage());
            return false;
        }
    }



    //Query per la registrazione del utente
    public static boolean insertuser(String username, String password, String firstname, String lastname, String telegramId) throws SQLException {
        String hashingPassword = BCrypt.hashpw(password, BCrypt.gensalt());// cifro la psw

        String query = "INSERT INTO users (username, password, first_name, last_name, telegram_id) VALUES (?, ?, ?, ?, ?)";

        Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);

            stmt.setString(1, username);
            stmt.setString(2, hashingPassword);
            stmt.setString(3, firstname);
            stmt.setString(4, lastname);
            stmt.setString(5, telegramId);

            int rows = stmt.executeUpdate();
            return rows > 0;
    }


    //Query per eliminare un utente
    public static boolean deleteUser(String username) throws SQLException {
        String query = "DELETE FROM users WHERE username = ?";

        Connection conn = DatabaseConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(query);

        stmt.setString(1, username);

        int rows = stmt.executeUpdate();
        return rows > 0;
    }


    //Query per il login admin
    public static boolean loginAdmin(String username, String password) {
        String query = "SELECT password FROM admins WHERE username = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, username);

            ResultSet rs = pstmt.executeQuery();

            // Verifica se l'admin esiste e se la password è corretta
            if (rs.next()) {
                String storedHashedPassword = rs.getString("password"); // Password cifrata
                return BCrypt.checkpw(password, storedHashedPassword); //vero se esiste e la password e coretta
            } else {
                return false; // L'admin non esiste
            }

        } catch (SQLException e) {
            logger.error("Errore nel login dell'admin {}", e.getMessage());
            return false;
        }
    }


    //Query per registrare admin
    public static boolean insertAdmin(String username, String password, String firstname, String lastname) throws SQLException {
        String hashingPassword = BCrypt.hashpw(password, BCrypt.gensalt());// cifro la psw

        String query = "INSERT INTO admins (username, password, first_name, last_name) VALUES (?, ?, ?, ?)";

        Connection conn = DatabaseConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(query);

        stmt.setString(1, username);
        stmt.setString(2, hashingPassword);
        stmt.setString(3, firstname);
        stmt.setString(4, lastname);

        int rows = stmt.executeUpdate();
        return rows > 0;
    }


    //Query per eliminare un admin specifico
    public static boolean deleteAdmin(String username) throws SQLException {
        String query = "DELETE FROM admins WHERE username = ?";

        Connection conn = DatabaseConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(query);

        stmt.setString(1, username);

        int rows = stmt.executeUpdate();
        return rows > 0;
    }


    //Query per ottenere tutti gli admin
    public static List<AdminModel> getAdmin() {
        List<AdminModel> admin = new ArrayList<>();
        String query = "SELECT * FROM admins";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            //Aggiungo tutti gli utentei
            while (rs.next()) {
                admin.add(new AdminModel(
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("first_name"),
                        rs.getString("last_name")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return admin;
    }

    //Query per ottenere gli username e i telegramID degli user
    public static List<UserModel> getAllUsers() {
        List<UserModel> users = new ArrayList<>();
        String query = "SELECT username, telegram_id FROM users";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            //Aggiungo tutti gli utentei
            while (rs.next()) {
                users.add(new UserModel(rs.getString("username"), rs.getString("telegram_id")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    //Prendo un admin spcecifico dallo userName
    public static AdminModel getAdminByUsername(String username) {
        String query = "SELECT * FROM admins WHERE username = ?";
        AdminModel admin = null;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)){

            pstmt.setString(1, username);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    admin = new AdminModel(rs.getString("username"),rs.getString("password"),
                            rs.getString("first_name"),rs.getString("last_name"));

                    System.out.println("DB admin " + rs.getString("username"));
                }
            }

        }catch (SQLException e) {
            e.printStackTrace();
        }

        return admin;
    }

    //Prendo IDtelegram, passwore, username di un utente specifico
    public static UserModel getUser(String username) {
        UserModel user = null;
        String query = "SELECT username, telegram_id, password, isReset FROM users WHERE username = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, username);

            try (ResultSet rs = pstmt.executeQuery()) {

                if (rs.next()) {
                    user = new UserModel(rs.getString("username"), rs.getString("telegram_id"),
                            rs.getString("password"),rs.getBoolean("isReset"));
                    System.out.println("DB user " + rs.getString("username"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return user; // Restituisci l'utente (o null se non trovato)
    }

    //Aggiorna isReset
    public static boolean updateIsReset(String username, boolean isReset) {
        String query = "UPDATE users SET isReset = ? WHERE username = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setBoolean(1, isReset);
            pstmt.setString(2, username);

            System.out.println("DB user " + username);
            System.out.println(isReset);

            return pstmt.executeUpdate() > 0; // Restituisce true se almeno una riga è stata aggiornata
        } catch (SQLException e) {
            logger.error("Errore nell'aggiornamento dello stato di reset per {}: {}", username, e.getMessage());
            return false;
        }
    }


    //Query per resettare la password del user con una casuale
    public static boolean resetUserPassword(String username) {
        String tempPassword = generateTemporaryPassword();
        String hashedPassword = BCrypt.hashpw(tempPassword, BCrypt.gensalt());
        String query = "UPDATE users SET password = ? WHERE username = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, hashedPassword);
            pstmt.setString(2, username);

            boolean success = pstmt.executeUpdate() > 0;
            if (success) {
                String message = "La tua password e stata resettata in " + tempPassword;
                TelegramBoot.sendMessageOnce(Long.parseLong(getTleramIDByUsername(username)),message);
                logger.info("Password temporanea per {}: {}", username, tempPassword);
            }
            return success;
        } catch (SQLException e) {
            logger.error("Errore nel reset della password per {}: {}", username, e.getMessage());
            return false;
        }
    }

    // Metodo per generare una password casuale
    private static String generateTemporaryPassword() {
        String randomPart = Long.toHexString(Double.doubleToLongBits(Math.random())).substring(0, 8).toUpperCase();
        return "TEMP_" + randomPart;

    }

    private static String getTleramIDByUsername(String username) {
        String telegramID = null;
        String query = "SELECT telegram_id FROM users WHERE username = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)){

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                telegramID = rs.getString("telegram_id");
            }

        } catch (SQLException e) {
            logger.error("Errore SQL per teleggram_id per {}: {}", username, e.getMessage());
        }
        return telegramID;
    }

    //Query per prendere la PSW dal nome utente
    public static String getUserPassword(String username) {
        String query = "SELECT password FROM users WHERE username = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getString("password");
            }

        } catch (SQLException e) {
            logger.error("Errore nel recupero della password per {}: {}", username, e.getMessage());
        }
        return null;
    }



    //Query per reimpostare la password di un user specifico
    public static boolean updatePassword(String username, String newPassword) {
        String hashingPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());
        String query = "UPDATE users SET password = ? WHERE username = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            // Imposta i parametri della query
            pstmt.setString(1, hashingPassword);
            pstmt.setString(2, username);

            // Esegue l'aggiornamento
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0; // Ritorna true se almeno una riga è stata aggiornata

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false; // Ritorna false in caso di errore
    }


    //Query per inserire i linguaggi di programmazione
    public static boolean insertProgrammingLanguage(String programmingLanguage) throws SQLException {
        String query = "INSERT INTO languages (name) VALUES (?)";

        Connection conn = DatabaseConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(query);

        stmt.setString(1, programmingLanguage);
        int rows = stmt.executeUpdate();

        return rows > 0;
    }


    //Query per estrarre tutti i linguaggi dal DB
    public static List<LanguagesModel> getLanguages() {
        List<LanguagesModel> languages = new ArrayList<>();
        String query = "SELECT * FROM languages";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            //Aggiungo tutti gli utentei
            while (rs.next()) {
                languages.add(new LanguagesModel(rs.getString("name")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return languages;
    }


    //Query per eliminare i lingaggi di programamzione
    public static boolean deleteProggrammingLanguages(String name) throws SQLException {
        String query = "DELETE FROM languages WHERE name = ?";

        Connection conn = DatabaseConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(query);

        stmt.setString(1, name);

        int rows = stmt.executeUpdate();
        return rows > 0;
    }

    //Query per recuperare tutte le categorie
    public static List<CategoryModel> getCategories(){
        String query = "SELECT * FROM themes ";
        List<CategoryModel> categories = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                categories.add(new CategoryModel(rs.getString("name")));
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }

        return categories;
    }

    //Query per recuperare tutte le difficolta
    public static List<DifficultyModel> getDifficulty() {
        String query = "SELECT id, name FROM difficulties";
        List<DifficultyModel> difficulties = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                difficulties.add(new DifficultyModel(id, name)); // Usa il costruttore corretto
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return difficulties;
    }


    //Query per inserire una categoria
    public static boolean insertTheme(String themeName) throws SQLException {
        String query = "INSERT INTO themes (name) VALUES (?)";

        Connection conn = DatabaseConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(query);

        stmt.setString(1, themeName);
        int rows = stmt.executeUpdate();

        return rows > 0;
    }


    //Query per prendere le difficolta gia presenti
    public static List<Integer> getAllLevelDifficulties() {
        List<Integer> levels = new ArrayList<>();
        String query = "SELECT levelDifficulty FROM difficulties";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                levels.add(rs.getInt("levelDifficulty"));
            }
        } catch (SQLException e) {
            System.err.println("Errore durante il recupero dei livelli di difficoltà: " + e.getMessage());
        }

        return levels;
    }


    // Recupera il livello di difficoltà dato un difficultyId
    public static int getLevelDifficultyById(int difficultyId) {
        String query = "SELECT levelDifficulty FROM difficulties WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, difficultyId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("levelDifficulty");
                }
            }
        } catch (SQLException e) {
            System.err.println("Errore durante il recupero del livello di difficoltà: " + e.getMessage());
        }
        return -1; // Valore di default in caso di errore
    }

    // Trova l'ID della difficoltà con il livello di difficoltà immediatamente superiore
    public static Integer getNextDifficultyId(int currentLevelDifficulty) {
        String query = "SELECT id FROM difficulties WHERE levelDifficulty > ? ORDER BY levelDifficulty ASC LIMIT 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, currentLevelDifficulty);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        } catch (SQLException e) {
            System.err.println("Errore durante il recupero della difficoltà successiva: " + e.getMessage());
        }
        return null; // Nessuna difficoltà successiva trovata
    }



    //Query per eliminare una categoria
    public static boolean deleteTheme(String themeName) throws SQLException {
        String query = "DELETE FROM themes WHERE name = ?";

        Connection conn = DatabaseConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(query);

        stmt.setString(1, themeName);
        int rows = stmt.executeUpdate();

        return rows > 0;
    }

    //Query per inserire una difficlta
    public static boolean insertDifficult(String themeName, int levelDifficulty) throws SQLException {
        String query = "INSERT INTO difficulties (name,levelDifficulty) VALUES (?, ?)";

        Connection conn = DatabaseConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(query);

        stmt.setString(1, themeName);
        stmt.setInt(2, levelDifficulty);

        int rows = stmt.executeUpdate();

        return rows > 0;
    }

    //Query pe elimianre una difficlta
    public static boolean deleteDifficult(String themeName) throws SQLException {
        String query = "DELETE FROM difficulties WHERE name = ?";

        Connection conn = DatabaseConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(query);

        stmt.setString(1, themeName);
        int rows = stmt.executeUpdate();

        return rows > 0;
    }

    //Query per prendere l'ID e il nome di un lingauggio di programamzione
    public static List<LanguagesModel> getLanguagesIdName() {
        String query = "SELECT * FROM languages";
        List<LanguagesModel> languages = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                languages.add(new LanguagesModel(id, name));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return languages;
    }


    //Query per prendere il nome e l'ID della categoria
    public static List<ThemsModel> getThemIdName() {
        String query = "SELECT id, name FROM themes";
        List<ThemsModel> thems = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                thems.add(new ThemsModel(id, name));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return thems;
    }

    public static List<DifficultyModel> getDifficultIdName() {
        String query = "SELECT id, name FROM difficulties";
        List<DifficultyModel> thems = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                thems.add(new DifficultyModel(id, name));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return thems;
    }


    //Query per inserire una domanda
    public static boolean insertQuestion(
            int languageId,
            int themeId,
            int difficultyId,
            String questionType,
            String question,
            String optionA,
            String optionB,
            String optionC,
            String optionD,
            String correctOption,
            String codeSolution
    ) throws SQLException {
        String query = """
        INSERT INTO questions (
            language_id, 
            theme_id, 
            difficulty_id, 
            question_type, 
            question, 
            option_a, 
            option_b, 
            option_c, 
            option_d, 
            correct_option, 
            code_solution
        ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
    """;

        Connection conn = DatabaseConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(query);

        stmt.setInt(1, languageId);
        stmt.setInt(2, themeId);
        stmt.setInt(3, difficultyId);
        stmt.setString(4, questionType);
        stmt.setString(5, question);
        stmt.setString(6, optionA);
        stmt.setString(7, optionB);
        stmt.setString(8, optionC);
        stmt.setString(9, optionD);
        stmt.setString(10, correctOption);
        stmt.setString(11, codeSolution);

        int rows = stmt.executeUpdate();

        return rows > 0;
    }


    //metodo per aggiornare la tabella userAnsware
    public static void updateUserAnswer(int userId, int questionId, boolean isCorrect) {
        String query = """
                INSERT INTO user_answers (user_id, question_id, is_correct) 
                VALUES (?, ?, ?) 
                ON CONFLICT(user_id, question_id) 
                DO UPDATE SET is_correct = excluded.is_correct
                """;


        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, userId);
            stmt.setInt(2, questionId);
            stmt.setBoolean(3, isCorrect);

            stmt.executeUpdate();
            System.out.println("Risposta salvata nel database: " + " - Corretta? " + isCorrect);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    //Query per ottenre tutte le domande di un certo linguaggio con una specifica difficiolta di una certa categoria
    public static List<QuestionModel> getQuestions(int themeId, int languageId, int difficultyId) {
        String query = """
            SELECT 
                id, language_id, theme_id, difficulty_id, question_type, 
                question, option_a, option_b, option_c, option_d, 
                correct_option, code_solution
            FROM 
                questions
            WHERE 
                theme_id = ? 
                AND language_id = ? 
                AND difficulty_id = ?;
            """;

        List<QuestionModel> questions = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, themeId);
            stmt.setInt(2, languageId);
            stmt.setInt(3, difficultyId);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                QuestionModel question = new QuestionModel(
                        rs.getInt("id"),
                        rs.getInt("language_id"),
                        rs.getInt("theme_id"),
                        rs.getInt("difficulty_id"),
                        rs.getString("question_type"),
                        rs.getString("question"),
                        rs.getString("option_a"),
                        rs.getString("option_b"),
                        rs.getString("option_c"),
                        rs.getString("option_d"),
                        rs.getString("correct_option"),
                        rs.getString("code_solution")
                );
                questions.add(question);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return questions;
    }


    // Query per prendere la percentuale di completamento dei linguaggi
    public static double getOverallCompletionPercentage(int languageId, int userId) {
        String query = """
        SELECT 
            CASE 
                WHEN total_questions = 0 THEN 0
                ELSE (correct_answers * 100.0) / total_questions
            END AS avg_completion_percentage
        FROM (
            SELECT 
                COUNT(DISTINCT q.id) AS total_questions,
                COUNT(DISTINCT CASE WHEN ua.is_correct = 1 THEN ua.question_id END) AS correct_answers
            FROM languages l
            JOIN questions q ON l.id = q.language_id
            LEFT JOIN user_answers ua ON q.id = ua.question_id AND ua.user_id = ?
            WHERE l.id = ?
        ) AS counts;
    """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, userId);
            stmt.setInt(2, languageId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("avg_completion_percentage");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0; // Se non c'è un risultato, restituisci 0%
    }


    // Query per prendere la percentuale di completamento dei temi
    public static Map<Integer, Double> getOverallCompletionPercentageForAllThemes(int userId) {
        String query = """
        SELECT 
            q.theme_id,
            CASE 
                WHEN COUNT(DISTINCT q.id) = 0 THEN 0
                ELSE (COUNT(DISTINCT CASE WHEN ua.is_correct = 1 THEN ua.question_id END) * 100.0) 
                     / COUNT(DISTINCT q.id)
            END AS overall_completion_percentage
        FROM questions q
        LEFT JOIN user_answers ua ON q.id = ua.question_id AND ua.user_id = ?
        GROUP BY q.theme_id;
        """;

        Map<Integer, Double> completionPercentageMap = new HashMap<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int themeId = rs.getInt("theme_id");
                    double completionPercentage = rs.getDouble("overall_completion_percentage");
                    completionPercentageMap.put(themeId, completionPercentage);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return completionPercentageMap;
    }


    //query per prendere le percentuali delle difficolta
    public static Map<Integer, Double> getOverallCompletionPercentageForAllDifficulties(int userId) {
        String query = """
        SELECT 
            q.difficulty_id,
            CASE 
                WHEN COUNT(DISTINCT q.id) = 0 THEN 0
                ELSE (COUNT(DISTINCT CASE WHEN ua.is_correct = 1 THEN ua.question_id END) * 100.0) 
                     / COUNT(DISTINCT q.id)
            END AS overall_completion_percentage
        FROM questions q
        LEFT JOIN user_answers ua ON q.id = ua.question_id AND ua.user_id = ?
        GROUP BY q.difficulty_id;
        """;

        Map<Integer, Double> completionPercentageMap = new HashMap<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int difficultyId = rs.getInt("difficulty_id");
                    double completionPercentage = rs.getDouble("overall_completion_percentage");
                    completionPercentageMap.put(difficultyId, completionPercentage);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return completionPercentageMap;
    }


    public static void updateUserProgress(int userId, int themeId, int languageId, int difficultyId, int additionalScore) {
        String selectQuery = """
        SELECT score FROM user_progress
        WHERE user_id = ? AND theme_id = ? AND language_id = ? AND difficulty_id = ?;
    """;

        String insertQuery = """
        INSERT INTO user_progress (user_id, theme_id, language_id, difficulty_id, score)
        VALUES (?, ?, ?, ?, ?);
    """;

        String updateQuery = """
        UPDATE user_progress
        SET score = score + ?
        WHERE user_id = ? AND theme_id = ? AND language_id = ? AND difficulty_id = ?;
    """;

        try (Connection conn = DatabaseConnection.getConnection()) {
            // Controlla se esiste già un record per la combinazione specificata
            try (PreparedStatement stmt = conn.prepareStatement(selectQuery)) {
                stmt.setInt(1, userId);
                stmt.setInt(2, themeId);
                stmt.setInt(3, languageId);
                stmt.setInt(4, difficultyId);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    // Se esiste, esegui l'aggiornamento
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                        updateStmt.setInt(1, additionalScore);
                        updateStmt.setInt(2, userId);
                        updateStmt.setInt(3, themeId);
                        updateStmt.setInt(4, languageId);
                        updateStmt.setInt(5, difficultyId);
                        updateStmt.executeUpdate();
                        System.out.println("Punteggio aggiornato con successo!");
                    }
                } else {
                    // Se non esiste, esegui l'inserimento
                    try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
                        insertStmt.setInt(1, userId);
                        insertStmt.setInt(2, themeId);
                        insertStmt.setInt(3, languageId);
                        insertStmt.setInt(4, difficultyId);
                        insertStmt.setInt(5, additionalScore);
                        insertStmt.executeUpdate();
                        System.out.println("Nuovo punteggio inserito con successo!");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Ottieni tutte le domande
    public static List<QuestionModel> getAllQuestions() {
        List<QuestionModel> questions = new ArrayList<>();
        String query = "SELECT q.id, q.question, l.name AS language_name, q.language_id, q.theme_id, q.difficulty_id, q.question_type, q.option_a, q.option_b, q.option_c, q.option_d, q.correct_option, q.code_solution " +
                "FROM questions q " +
                "JOIN languages l ON q.language_id = l.id;";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                QuestionModel question = new QuestionModel(
                        rs.getInt("id"),
                        rs.getInt("language_id"),
                        rs.getInt("theme_id"),
                        rs.getInt("difficulty_id"),
                        rs.getString("question_type"),
                        rs.getString("question"),
                        rs.getString("option_a"),
                        rs.getString("option_b"),
                        rs.getString("option_c"),
                        rs.getString("option_d"),
                        rs.getString("correct_option"),
                        rs.getString("code_solution"),
                        rs.getString("language_name") // Mappa language_name
                );
                questions.add(question);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return questions;
    }

    // Elimina una domanda per ID
    public static boolean deleteQuestion(int questionId) throws SQLException {
        String query = "DELETE FROM questions WHERE id = ?;";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, questionId);
            return pstmt.executeUpdate() > 0;
        }
    }


    //query per avere la calssifica di tutti i giocatori
    public static List<PlayerRankingModel> getPlayerRanking() {
        List<PlayerRankingModel> rankingList = new ArrayList<>();
        String query = """
        SELECT u.username, SUM(up.score) AS total_score
        FROM user_progress up
        JOIN users u ON up.user_id = u.id
        GROUP BY u.username
        ORDER BY total_score DESC;
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            int position = 1;
            while (rs.next()) {
                String username = rs.getString("username");
                int totalScore = rs.getInt("total_score");
                rankingList.add(new PlayerRankingModel(position++, username, totalScore));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return rankingList;
    }

}