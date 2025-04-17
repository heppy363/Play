package com.matteorossi.play.controllers;

import com.matteorossi.play.database.QueryDAO;
import com.matteorossi.play.models.*;
import com.matteorossi.play.telegram.TelegramBoot;
import com.matteorossi.play.utilitis.WhindowUtilit;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
/**
 * Controller per la gestione dell'interfaccia amministrativa dell'applicazione.
 * Gestisce le operazioni CRUD per utenti, amministratori, linguaggi di programmazione,
 * categorie e livelli di difficoltà, oltre alla navigazione tra le varie view.
 *
 * <p>Utilizza {@link TableView} JavaFX per visualizzare i dati e bottoni per le azioni specifiche.</p>
 *
 * <h3>Componenti principali:</h3>
 * <ul>
 *   <li>Tabella utenti ({@link #userTable}) con pulsanti per reset password ed eliminazione</li>
 *   <li>Tabella amministratori ({@link #adminTable}) con pulsante eliminazione</li>
 *   <li>Tabella linguaggi di programmazione ({@link #languagesTable})</li>
 *   <li>Tabella categorie ({@link #categoryTable})</li>
 *   <li>Tabella difficoltà ({@link #difficultyTable})</li>
 * </ul>
 *
 * <h3>Funzionalità principali:</h3>
 * <ul>
 *   <li>Inserimento nuovi elementi (linguaggi, categorie, difficoltà, amministratori)</li>
 *   <li>Gestione reset password utenti con notifica Telegram</li>
 *   <li>Eliminazione record da tutte le tabelle</li>
 *   <li>Navigazione tra le diverse view dell'applicazione</li>
 * </ul>
 *
 * <p>Interagisce con il database tramite {@link QueryDAO} e invia notifiche tramite {@link TelegramBoot}.</p>
 *
 * @see UserModel Modello dati per gli utenti
 * @see AdminModel Modello dati per gli amministratori
 * @see LanguagesModel Modello dati per i linguaggi
 * @see CategoryModel Modello dati per le categorie
 * @see DifficultyModel Modello dati per le difficoltà
 * @see WhindowUtilit Utility per la gestione delle finestre
 */
public class AdminController {

    @FXML
    private TableView<UserModel> userTable;
    @FXML
    private TableView<AdminModel> adminTable;
    @FXML
    private TableView<LanguagesModel> languagesTable;
    @FXML
    private TableView<CategoryModel> categoryTable;
    @FXML
    private TableView<DifficultyModel>difficultyTable;
    @FXML
    private TableColumn<UserModel, String> usernameColumn;
    @FXML
    private TableColumn<UserModel, Button> resetColumn;
    @FXML
    private TableColumn<UserModel, Button> deleteColumn;
    @FXML
    private TableColumn<AdminModel, String> adminColumn;
    @FXML
    private TableColumn<AdminModel, Button> deleteAdminColumn;
    @FXML
    private TableColumn<LanguagesModel, String> languageColumn;
    @FXML
    private TableColumn<LanguagesModel, Button> deleteLanguageColumn;
    @FXML
    private TableColumn<CategoryModel, String> categoryColumn;
    @FXML
    private TableColumn<CategoryModel, Button> deleteCategoryColumn;
    @FXML
    private TableColumn<DifficultyModel, String> difficultyColumn;
    @FXML
    private TableColumn<DifficultyModel, Button> deleteDifficultyColumn;
    @FXML
    private TextField usernameAdminTextField;
    @FXML
    private TextField passwordAdminTextField;
    @FXML
    private TextField firstNameAdminTextField;
    @FXML
    private TextField lastNameAdminTextField;
    @FXML
    private TextField insertLanguageTextField;
    @FXML
    private TextField insertThemsTextField;
    @FXML
    private TextField insertDifficultTextField;
    @FXML
    private TextField insertLevelDIffTextField;

    private final WhindowUtilit windowUtilit = new WhindowUtilit();
    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    @FXML
    public void onInsertLanguageClick(ActionEvent actionEvent) {
        String language = insertLanguageTextField.getText();

        try {
            QueryDAO.insertProgrammingLanguage(language.toLowerCase());
            WhindowUtilit.showAlert("Successo", "Linguaggio inserito correttamente", "Successo");
        } catch (SQLException e) {
            logger.error(e.getMessage());
            WhindowUtilit.showAlert("Errore", "Errore nell'inserimento di un linguaggio", "Errore");
        }
        loadLanguages();
    }

    @FXML
    public void onAddAdminClick(ActionEvent actionEvent) {
        String username = usernameAdminTextField.getText();
        String password = passwordAdminTextField.getText();
        String firstName = firstNameAdminTextField.getText();
        String lastName = lastNameAdminTextField.getText();

        if (username.isEmpty() || password.isEmpty() || firstName.isEmpty() || lastName.isEmpty()) {
            WhindowUtilit.showAlert("Errore", "Campi vuoti", "Compila tutti i campi.");
            return;
        }

        try {
            if (QueryDAO.insertAdmin(username, password, firstName, lastName)) {
                WhindowUtilit.showAlert("Successo", "Admin inserito con successo", "Successo");
            }
        } catch (SQLException e) {
            logger.error("Errore SQL durante l'aggiunta dell'admin: " + username, e);
            WhindowUtilit.showAlert("Errore", "Errore SQL", "Si è verificato un errore durante l'aggiunta dell'admin.");
            throw new RuntimeException(e);
        }
        loadAdmins();
    }

    @FXML
    public void onLoginClick(ActionEvent actionEvent) {
        try {
            windowUtilit.changeWhindow(actionEvent, "/com/matteorossi/play/view/login.fxml", "Login");
        } catch (IOException e) {
            logger.error("Errore IOException", e);
            WhindowUtilit.showAlert("Errore", "Non puoi tornare al login ora, riprova più tardi", "Informativa");
        }
    }

    @FXML
    public void onHandleQuestionClick(ActionEvent actionEvent) {
        try {
            windowUtilit.changeWhindow(actionEvent, "/com/matteorossi/play/view/insertQuestion.fxml", "Gestisci domande");
        } catch (IOException e) {
            logger.error("Errore IOException", e);
            WhindowUtilit.showAlert("Errore", "Non è possibile cambiare pagina ora", "Informativa");
        }
    }

    @FXML
    public void onInsertThemsClik(ActionEvent actionEvent) {
        String categories = insertThemsTextField.getText();

        if (categories.isEmpty()) {
            WhindowUtilit.showAlert("Errore", "Campi vuoti", "Compila tutti i campi.");
        }

        try {
            QueryDAO.insertTheme(categories.toLowerCase());
            WhindowUtilit.showAlert("Successo", "Categoria inserita con successo", "Successo");
        }catch (SQLException e){
            logger.error("Errore SQL", e);
            WhindowUtilit.showAlert("Errore", "Errore SQL", "Si è verificato un errore durante l'aggiunta della categoria.");
        }
        loadCategories();
    }

    @FXML
    public void onInsertDifficultClik(ActionEvent actionEvent) {
        // Recupera il nome della difficoltà
        String difficultName = insertDifficultTextField.getText().trim(); // Rimuovi spazi bianchi
        System.out.println("Nome difficoltà: '" + difficultName + "'"); // Debug: stampa il nome della difficoltà

        // Recupera il livello di difficoltà
        String levelDiffText = insertLevelDIffTextField.getText().trim(); // Rimuovi spazi bianchi
        System.out.println("Livello di difficoltà inserito: '" + levelDiffText + "'"); // Debug: stampa il livello di difficoltà

        // Controllo se i campi sono vuoti
        if (difficultName.isEmpty() || levelDiffText.isEmpty()) {
            WhindowUtilit.showAlert("Errore", "Campi vuoti", "Compila tutti i campi.");
            return;
        }

        // Controllo se il livello di difficoltà è un numero intero valido
        Integer levelDiff;
        try {
            levelDiff = Integer.parseInt(levelDiffText); // Conversione da stringa a intero
            System.out.println("Livello di difficoltà convertito: " + levelDiff); // Debug: stampa il valore convertito
        } catch (NumberFormatException e) {
            WhindowUtilit.showAlert("Errore", "Formato non valido", "Il livello di difficoltà deve essere un numero intero.");
            return;
        }

        // Controllo se il livello di difficoltà è già presente nel database
        List<Integer> difficultsLevelDB = QueryDAO.getAllLevelDifficulties();
        if (difficultsLevelDB.contains(levelDiff)) {
            WhindowUtilit.showAlert("Errore", "Livello già presente", "Il livello di difficoltà " + levelDiff + " è già presente nel database.");
            return;
        }

        // Se tutto è corretto, procedi con l'inserimento
        try {
            boolean isInserted = QueryDAO.insertDifficult(difficultName.toLowerCase(), levelDiff.intValue());
            if (isInserted) {
                WhindowUtilit.showAlert("Successo", "Difficoltà inserita con successo", "La difficoltà è stata aggiunta correttamente.");
            } else {
                WhindowUtilit.showAlert("Errore", "Inserimento fallito", "Non è stato possibile inserire la difficoltà.");
            }
        } catch (SQLException e) {
            WhindowUtilit.showAlert("Errore", "Errore SQL", "Si è verificato un errore durante l'aggiunta della difficoltà.");
            logger.error("Errore SQL: " + e.getMessage(), e); // Log dell'errore SQL
        }

        // Ricarica la lista delle difficoltà
        loadDifficults();
    }

    @FXML
    public void initialize() {
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        adminColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        languageColumn.setCellValueFactory(new PropertyValueFactory<>("languageName"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("categoryName"));
        difficultyColumn.setCellValueFactory(new PropertyValueFactory<>("difficulty"));

        configureButtonColumn(resetColumn, "Reset", this::resetPassword);
        configureButtonColumn(deleteColumn, "Elimina", this::deleteUser);
        configureButtonColumn(deleteAdminColumn, "Elimina", this::deleteAdmin);
        configureButtonColumn(deleteLanguageColumn, "Elimina", this::deleteLanguage);
        configureButtonColumn(deleteCategoryColumn, "Elimina", this::delateCategories);
        configureButtonColumn(deleteDifficultyColumn, "Elimina", this::delateDifficults);


        loadUsers();
        loadAdmins();
        loadLanguages();
        loadCategories();
        loadDifficults();
    }

    private <T> void configureButtonColumn(TableColumn<T, Button> column, String buttonText, ButtonAction<T> action) {
        column.setCellFactory(col -> new TableCell<>() {
            private final Button button = new Button(buttonText);

            {
                button.setOnAction(event -> {
                    T item = getTableView().getItems().get(getIndex());
                    action.execute(item);
                });
            }

            @Override
            protected void updateItem(Button item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(button);
                }
            }
        });
    }

    private void loadUsers() {
        List<UserModel> users = QueryDAO.getAllUsers();
        ObservableList<UserModel> userList = FXCollections.observableArrayList(users);
        userTable.setItems(userList);
    }

    private void loadAdmins() {
        List<AdminModel> admins = QueryDAO.getAdmin();
        ObservableList<AdminModel> adminList = FXCollections.observableArrayList(admins);
        adminTable.setItems(adminList);
    }

    private void loadLanguages() {
        List<LanguagesModel> languages = QueryDAO.getLanguages();
        ObservableList<LanguagesModel> languageList = FXCollections.observableArrayList(languages);
        languagesTable.setItems(languageList);
    }

    private void loadCategories() {
        List<CategoryModel> categories = QueryDAO.getCategories();
        ObservableList<CategoryModel> categoriesList = FXCollections.observableArrayList(categories);
        categoryTable.setItems(categoriesList);
    }

    private void loadDifficults(){
        List<DifficultyModel> difficults = QueryDAO.getDifficulty();
        ObservableList<DifficultyModel> difficultsList = FXCollections.observableArrayList(difficults);
        difficultyTable.setItems(difficultsList);
    }

    private void resetPassword(UserModel user) {
            Boolean temp = QueryDAO.updateIsReset(user.getUsername(),true);
            System.out.println("SONO ADMIN "+ temp);
            if (QueryDAO.resetUserPassword(user.getUsername()) && temp) {
                WhindowUtilit.showAlert("Successo", "Password resettata", "Operazione completata con successo.");
                System.out.println("Password resettata "+ user.getPassword());
                // Invio notifica su Telegram
                String telegraMessag = "Ciao " + user.getUsername() + " la tua password e stata resettata";
                TelegramBoot.sendMessageOnce(Long.parseLong(user.getTelegramId()), telegraMessag);
            }
    }

    private void deleteUser(UserModel user) {
        try {
            if (QueryDAO.deleteUser(user.getUsername())) {
                userTable.getItems().remove(user);
                WhindowUtilit.showAlert("Successo", "Utente eliminato", "Operazione completata con successo.");
            }
        } catch (SQLException e) {
            logger.error("Errore durante l'eliminazione dell'utente", e);
        }
    }

    private void deleteAdmin(AdminModel admin) {
        try {
            if (QueryDAO.deleteAdmin(admin.getUsername())) {
                adminTable.getItems().remove(admin);
                WhindowUtilit.showAlert("Successo", "Admin eliminato", "Operazione completata con successo.");
            }
        } catch (SQLException e) {
            logger.error("Errore durante l'eliminazione dell'admin", e);
        }
    }

    private void deleteLanguage(LanguagesModel language) {
        try {
            if (QueryDAO.deleteProggrammingLanguages(language.getLanguageName())) {
                languagesTable.getItems().remove(language);
                WhindowUtilit.showAlert("Successo", "Linguaggio eliminato", "Operazione completata con successo.");
            }
        } catch (SQLException e) {
            logger.error("Errore durante l'eliminazione del linguaggio", e);
        }
    }

    private void delateCategories(CategoryModel categoty){
        try {
            if (QueryDAO.deleteTheme(categoty.getCategoryName()));
            categoryTable.getItems().remove(categoty);
            WhindowUtilit.showAlert("Successo", "Categoria eliminata", "Operazione completata con successo.");
        }catch (SQLException e){
            logger.error("Errore durante l'eliminazione della categoria", e);
        }
    }

    private void delateDifficults(DifficultyModel difficult){
        try {
            if (QueryDAO.deleteDifficult(difficult.getDifficulty()));{
                difficultyTable.getItems().remove(difficult);
                WhindowUtilit.showAlert("Successo", "Difficolta eliminata", "Operazione completata con successo.");
            }
        }catch (SQLException e){
            logger.error("Errore durante l'eliminazione della difficolta", e);
        }
    }



    @FunctionalInterface
    private interface ButtonAction<T> {
        void execute(T item);
    }
}
