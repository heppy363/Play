package com.matteorossi.play.controllers;

import com.matteorossi.play.utilitis.WhindowUtilit;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller per la schermata introduttiva dell'applicazione.
 * Gestisce la navigazione iniziale verso la schermata di login.
 *
 * <p>Funzionalità principale:</p>
 * <ul>
 *   <li>Transizione alla schermata di autenticazione</li>
 *   <li>Gestione base degli errori di navigazione</li>
 * </ul>
 *
 * @see WhindowUtilit Classe utility per la gestione delle finestre
 */


public class IntruductionController {

    private final WhindowUtilit windowUtilit = new WhindowUtilit();
    private static final Logger logger = LoggerFactory.getLogger(IntruductionController.class);


    @FXML
    public void onButtonGiocaClik(ActionEvent actionEvent) {
        try {
            windowUtilit.changeWhindow(actionEvent, "/com/matteorossi/play/view/login.fxml", "Login");
        }catch (Exception e) {
            WhindowUtilit.showAlert("Errore", "Errore nel cambio pagina", "errore");
            logger.error("Errore nel cambio pagfina {}", e);
        }

    }
}
