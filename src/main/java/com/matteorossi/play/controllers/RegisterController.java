package com.matteorossi.play.controllers;

import com.matteorossi.play.database.QueryDAO;
import com.matteorossi.play.telegram.TelegramBoot;
import com.matteorossi.play.utilitis.WhindowUtilit;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

/**
 * Controller per la gestione della registrazione di nuovi utenti.
 * Gestisce l'input dei dati anagrafici, la validazione delle credenziali
 * e l'inserimento nel database, con notifica Telegram opzionale.
 *
 * <p>Funzionalità principali:</p>
 * <ul>
 *   <li>Validazione in tempo reale di username e password</li>
 *   <li>Controllo campi obbligatori</li>
 *   <li>Invio notifiche Telegram al completamento</li>
 *   <li>Gestione di username riservati (es. contenenti "admin")</li>
 *   <li>Tooltip informativi per tutti i campi</li>
 * </ul>
 */
public class RegisterController {
    private boolean isCorrect = false;
    private static final Logger logger = LoggerFactory.getLogger(RegisterController.class);
    WhindowUtilit windowUtilit = new WhindowUtilit();

    @FXML private TextField nameTexFild;
    @FXML private TextField surnameTextFild;
    @FXML private TextField telegramIdTextFild;
    @FXML private TextField usernameTextFild;
    @FXML private TextField passwordTextFild;
    @FXML private TextField reconfermPasswordTexFild;
    @FXML private Label passwordControlLable;
    @FXML private Label userNameUnicLable;

    @FXML
    public void onSubmetClik(ActionEvent actionEvent) throws SQLException {
        System.out.println("Submet Clik");
        String name = nameTexFild.getText();
        String surname = surnameTextFild.getText();
        String telegramId = telegramIdTextFild.getText();
        String username = usernameTextFild.getText();
        String password = passwordTextFild.getText();

        // Controllo username admin
        if(username.toLowerCase().contains("admin")) {
            WhindowUtilit.showAlert(
                    "Allert",
                    "Si prega di non usare admin o sue declinaizoni come username",
                    "Nome non consentito");
            return;
        }

        if (areEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
        }
        else if (!isCorrect) {
            WhindowUtilit.showAlert(
                    "Errore",
                    "Le password non coincidono. Si prega di verificarle.",
                    "Errore Password");
        }
        else {
            try {
                QueryDAO.insertuser(username, password, name, surname, telegramId);

                WhindowUtilit.showAlert(
                        "Sucesso",
                        "Il tuo account e stato creato con sucesso si prega di fare il loggin",
                        "Sucesso");

                //opzionale TelegramID
                if (telegramId.isEmpty()){
                    System.out.println(telegramId);
                    telegramId = "0";
                }else {
                    // Invio notifiche Telegram
                    TelegramBoot.sendMessageOnce(Long.parseLong(telegramId), "Benvenuto in Play il tuo occount e stato creato");
                    TelegramBoot.sendMessageOnce(Long.parseLong(telegramId), "Ti invitiamo a fare il login con le seguenti credenziuali");
                    TelegramBoot.sendMessageOnce(Long.parseLong(telegramId), "Username: " + username);
                    TelegramBoot.sendMessageOnce(Long.parseLong(telegramId), "Password: " + password);
                }
            } catch (SQLException e) {
                logger.error("Errore nella creazione del utente", e);
                WhindowUtilit.showAlert("Errore", "C'e stato un errore nella creazione del account", "Riprova in seguito");
            }
        }

        System.out.println("Name: " + name + surname + telegramId + username + password);
    }

    public void onBackToLoginClik(ActionEvent actionEvent) {
        try {
            windowUtilit.changeWhindow(actionEvent,"/com/matteorossi/play/view/login.fxml", "login");
        } catch (Exception e) {
            logger.error("Errore ", e);
            WhindowUtilit.showAlert(
                    "Errore",
                    "Si è verificato un errore nel tentativo di aprire la pagina di Login. Riprova più tardi.",
                    "Errore nel caricamento della pagina di Login");
        }
    }

    @FXML
    public void initialize() {
        // Controllo password e username
        passwordTextFild.textProperty().addListener((observable, oldValue, newValue) -> checkPassword());
        reconfermPasswordTexFild.textProperty().addListener((observable, oldValue, newValue) -> checkPassword());
        usernameTextFild.textProperty().addListener((observable, oldValue, newValue) -> userNameUnivoc());

        // Configurazione tooltip
        setupTooltips();
    }

    private void setupTooltips() {
        // Tooltip per i campi del form
        Tooltip nameTooltip = new Tooltip("Inserisci il tuo nome (campo obbligatorio)");
        nameTexFild.setTooltip(nameTooltip);

        Tooltip surnameTooltip = new Tooltip("Inserisci il tuo cognome (campo obbligatorio)");
        surnameTextFild.setTooltip(surnameTooltip);

        Tooltip telegramTooltip = new Tooltip("Inserisci il tuo ID Telegram (opzionale)\nPuoi ottenere il tuo ID con @PlayUniversita");
        telegramTooltip.setStyle("-fx-font-size: 12px;");
        telegramIdTextFild.setTooltip(telegramTooltip);

        Tooltip usernameTooltip = new Tooltip("Scegli un username unico (campo obbligatorio)\nNon può contenere 'admin' o sue varianti");
        usernameTextFild.setTooltip(usernameTooltip);

        Tooltip passwordTooltip = new Tooltip("Crea una password sicura (campo obbligatorio)\nDeve contenere almeno 8 caratteri");
        passwordTextFild.setTooltip(passwordTooltip);

        Tooltip confirmTooltip = new Tooltip("Reinserisci la password per conferma");
        reconfermPasswordTexFild.setTooltip(confirmTooltip);

        Tooltip infoTooltip = new Tooltip("Tutti i campi contrassegnati con * sono obbligatori");
    }

    private void checkPassword() {
        String password = passwordTextFild.getText();
        String confirmPassword = reconfermPasswordTexFild.getText();

        if (password.equals(confirmPassword) && !password.isEmpty()) {
            passwordControlLable.setText("✓ Password confermata");
            passwordControlLable.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
            isCorrect = true;
        } else if (!password.isEmpty() || !confirmPassword.isEmpty()) {
            passwordControlLable.setText("✗ Le password non coincidono");
            passwordControlLable.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");

            Tooltip errorTooltip = new Tooltip("Le password inserite non corrispondono\nAssicurati di aver digitato la stessa password in entrambi i campi");
            errorTooltip.setStyle("-fx-text-fill: white; -fx-background-color: #d9534f;");
            passwordControlLable.setTooltip(errorTooltip);
        } else {
            passwordControlLable.setText("");
            passwordControlLable.setTooltip(null);
        }
    }

    private void userNameUnivoc() {
        String username = usernameTextFild.getText();

        if (username.isEmpty()) {
            userNameUnicLable.setText("");
            userNameUnicLable.setTooltip(null);
            return;
        }

        if (!QueryDAO.isUsernameUnique(username)) {
            userNameUnicLable.setText("✗ Username già usato");
            userNameUnicLable.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");

            Tooltip errorTooltip = new Tooltip("Questo username è già in uso\nProva ad aggiungere numeri o caratteri speciali");
            errorTooltip.setStyle("-fx-text-fill: white; -fx-background-color: #d9534f;");
            userNameUnicLable.setTooltip(errorTooltip);
        } else if (username.toLowerCase().contains("admin")) {
            userNameUnicLable.setText("✗ Nome non consentito");
            userNameUnicLable.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");

            Tooltip errorTooltip = new Tooltip("L'username non può contenere 'admin' o sue varianti");
            errorTooltip.setStyle("-fx-text-fill: white; -fx-background-color: #d9534f;");
            userNameUnicLable.setTooltip(errorTooltip);
        } else {
            userNameUnicLable.setText("✓ Username valido");
            userNameUnicLable.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
            userNameUnicLable.setTooltip(null);
        }
    }

    private boolean areEmpty() {
        boolean hasEmptyFields = false;
        if (usernameTextFild.getText().trim().isEmpty() ||
                nameTexFild.getText().trim().isEmpty() ||
                passwordTextFild.getText().trim().isEmpty() ||
                reconfermPasswordTexFild.getText().trim().isEmpty() ||
                surnameTextFild.getText().trim().isEmpty()) {

            WhindowUtilit.showAlert(
                    "Allert",
                    "Si prega di completare tutti i campi obbligatori",
                    "Campi mancanti");
            hasEmptyFields = true;
        }
        return hasEmptyFields;
    }
}