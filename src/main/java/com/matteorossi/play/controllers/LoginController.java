package com.matteorossi.play.controllers;

import com.matteorossi.play.database.QueryDAO;
import com.matteorossi.play.models.AdminModel;
import com.matteorossi.play.models.UserModel;
import com.matteorossi.play.utilitis.GlobalConfig;
import com.matteorossi.play.utilitis.WhindowUtilit;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;


/**
 * Controller per la gestione dell'autenticazione degli utenti e amministratori.
 * Gestisce il login, il reset password e la navigazione verso altre view.
 *
 * <p>Funzionalità principali:</p>
 * <ul>
 *   <li>Autenticazione per utenti e amministratori</li>
 *   <li>Gestione reset password</li>
 *   <li>Navigazione verso registrazione e reimpostazione password</li>
 *   <li>Controllo hash password con BCrypt</li>
 * </ul>
 *
 * @see UserModel Modello dati utenti
 * @see AdminModel Modello dati amministratori
 * @see GlobalConfig Configurazione globale per l'ID utente
 */


public class LoginController {
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
    private final WhindowUtilit windowUtil = new WhindowUtilit();

    @FXML
    private TextField texfildeUsername;
    @FXML
    private TextField textfildPassword;
    @FXML
    private Button resetPassword;
    @FXML
    private Button loginButton;
    @FXML
    private Button singinButton;

    private enum UserRole {USER, ADMIN, RESET_REQUIRED, INVALID}

    @FXML
    private void onLoginButtonClik(ActionEvent actionEvent) {
        String username = texfildeUsername.getText();
        String password = textfildPassword.getText();

        UserRole role = verifyUser(username, password); //da mettere in riga 39 IMPORTANTE
        switch (role) {
            case USER:
                logger.info("Login Successful: User");
                //Salvo ID utente variabile globale
                GlobalConfig.userID = QueryDAO.getIDByUsername(username);
                changePage(actionEvent, "/com/matteorossi/play/view/roulseAndInformation.fxml", "Informazioni e Regole");
                break;

            case ADMIN:
                logger.info("Login Successful: Admin");
                changePage(actionEvent, "/com/matteorossi/play/view/admin.fxml", "Pagina Admin");
                break;

            case RESET_REQUIRED:
                logger.info("Password Reset Required");
                handlePasswordReset();
                break;

            case INVALID:
            default:
                WhindowUtilit.showAlert("Errore", "Credenziali errate o utente inesistente", "Errore di login");
                break;
        }
    }

    @FXML
    public void onSinginButtonClik(ActionEvent actionEvent) {
        changePage(actionEvent, "/com/matteorossi/play/view/register.fxml", "Creazione Account");
    }

    @FXML
    public void onResetPasswordClik(ActionEvent actionEvent) {
        changePage(actionEvent, "/com/matteorossi/play/view/reinsertPassword.fxml", "Reimposta Password");
    }

    private UserRole verifyUser(String username, String password) {
        UserModel user = QueryDAO.getUser(username);

        if (user != null) {
            if (verifyPassword(username, password) && user.getIsReset()) return UserRole.RESET_REQUIRED;
            if (QueryDAO.loginUser(username, password)) return UserRole.USER;
        }

        AdminModel admin = QueryDAO.getAdminByUsername(username);
        if (admin != null) {
            if (QueryDAO.loginAdmin(username, password)) return UserRole.ADMIN;
        }

        return UserRole.INVALID;
    }

    private Boolean verifyPassword(String username, String password) {
        String storedPassword = QueryDAO.getUserPassword(username);
        return BCrypt.checkpw(password, storedPassword);
    }

    private void handlePasswordReset() {
        loginButton.setVisible(false);
        singinButton.setVisible(false);
        resetPassword.setVisible(true);

        WhindowUtilit.showAlert("Notifica",
                "Sembra che la tua password sia stata resettata. Si prega di reimpostarla.",
                "Comunicazione");
    }

    private void changePage(ActionEvent actionEvent, String fxmlPath, String title) {
        try {
            windowUtil.changeWhindow(actionEvent, fxmlPath, title);
        } catch (IOException e) {
            logger.error("Errore nel cambio pagina: {}", e.getMessage());
            WhindowUtilit.showAlert("Errore", "Errore nel caricamento della pagina. Riprova più tardi.", "Errore");
        }
    }
}