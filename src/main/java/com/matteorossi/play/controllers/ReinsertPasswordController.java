package com.matteorossi.play.controllers;

import com.matteorossi.play.database.QueryDAO;
import com.matteorossi.play.models.UserModel;
import com.matteorossi.play.telegram.TelegramBoot;
import com.matteorossi.play.utilitis.WhindowUtilit;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Controller per la reimpostazione della password utente.
 * Gestisce il processo di reset password dopo una richiesta amministrativa,
 * con validazione delle credenziali e notifica via Telegram.
 *
 * <p>Funzionalità principali:</p>
 * <ul>
 *   <li>Verifica identità utente tramite username</li>
 *   <li>Validazione corrispondenza nuove password</li>
 *   <li>Aggiornamento sicuro delle credenziali nel database</li>
 *   <li>Notifica cambio password via Telegram</li>
 * </ul>
 *
 * @see QueryDAO Accesso al database per l'aggiornamento password
 * @see TelegramBoot Invio notifiche post-reset
 */

public class ReinsertPasswordController {

    @FXML
    private Label passwordControlLable;
    @FXML
    private TextField passwordTextFild;
    @FXML
    private TextField rePasswordTextFild;
    @FXML
    private TextField userNameTextFild;
    @FXML
    private Button beckToLogin;

    private static final Logger logger = LoggerFactory.getLogger(ReinsertPasswordController.class);
    WhindowUtilit windowUtilit = new WhindowUtilit();


    public void onConfermaClik(ActionEvent actionEvent) {
        System.out.println("Conferma " + userNameTextFild.getText());
        UserModel user = QueryDAO.getUser(userNameTextFild.getText());
        System.out.println("User name "+ user.getTelegramId());

        String passwordMessage = "La tua password e stata cambiata correttamente in: " + passwordTextFild.getText();
        //Controlalre se lo username e coretto e le due password coincidono
        if (user.getUsername() != null && checkPassword()) {

            //aggiorno la password
            if(QueryDAO.updatePassword(user.getUsername(), rePasswordTextFild.getText())){
                if (QueryDAO.updateIsReset(user.getUsername(), false)){
                    WhindowUtilit.showAlert("Riuscita", "Password updated successfully torna al login", "Informativa");
                    beckToLogin.setVisible(true); //rendo il bottone per tornare al login visibile
                    TelegramBoot.sendMessageOnce(Long.parseLong(user.getTelegramId()),passwordMessage);
                }

            }
            else showAlert("Errore", "Password not updated");

        }else {
            showAlert("Errore", "Username non valido o incorretto");
        }

    }


    public void onBeckToLoginCliced(ActionEvent actionEvent) {
        try {
            windowUtilit.changeWhindow(actionEvent, "/com/matteorossi/play/view/login.fxml", "Login");
        } catch (IOException e) {
            logger.error("Errore ", e);
            showAlert("Errore", "Errore durante la segui login");
        }

    }


    @FXML
    public void initialize() {
        passwordTextFild.textProperty().addListener((observable, oldValue, newValue) -> checkPassword());
        rePasswordTextFild.textProperty().addListener((observable, oldValue, newValue) -> checkPassword());
    }


    private boolean checkPassword(){
        boolean isCorrect = false;
        String password = passwordTextFild.getText();
        String confirmPassword = rePasswordTextFild.getText();

        // Controlla se coincidono
        if (password.equals(confirmPassword) && !password.isEmpty()) {
            passwordControlLable.setText("Password confermata!");
            passwordControlLable.setStyle("-fx-text-fill: green;"); // Cambia colore del testo
            isCorrect = true;
        } else if (!password.isEmpty() || !confirmPassword.isEmpty()) {
            passwordControlLable.setText("Le password non coincidono!");
            passwordControlLable.setStyle("-fx-text-fill: red;"); // Cambia colore del testo
        } else {
            // Entrambi i campi vuoti: nessun messaggio
            passwordControlLable.setText("");
        }
        return isCorrect;
    }


    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

}
