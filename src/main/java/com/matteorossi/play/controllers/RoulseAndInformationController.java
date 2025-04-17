package com.matteorossi.play.controllers;

import com.matteorossi.play.utilitis.WhindowUtilit;
import javafx.event.ActionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Controller per la schermata delle regole e informazioni.
 * Gestisce la navigazione verso le sezioni principali dell'applicazione:
 * <ul>
 *   <li>Selezione linguaggi di programmazione</li>
 *   <li>Visualizzazione classifica giocatori</li>
 * </ul>
 *
 * <p>Funzionalità principali:</p>
 * <ul>
 *   <li>Navigazione diretta tra le view principali</li>
 *   <li>Gestione centralizzata degli errori di navigazione</li>
 *   <li>Logging degli eventi critici</li>
 * </ul>
 *
 * @see WhindowUtilit Classe utility per la gestione delle finestre
 */

public class RoulseAndInformationController {

    private final WhindowUtilit windowUtilit = new WhindowUtilit();
    private static final Logger logger = LoggerFactory.getLogger(RoulseAndInformationController.class);

    //Porta al gioco
    public void onGiocoClik(ActionEvent actionEvent) {
        try {
            windowUtilit.changeWhindow(actionEvent, "/com/matteorossi/play/view/programmingLanguages.fxml", "Linguaggi programmazione");
        }catch (IOException e){
            WhindowUtilit.showAlert("Errore", "Errore nel cambio pagina", "Errore nel cambio pagina");
            logger.error("Errore nel cambio della pagina {}", e);
        }
    }

    //Porta alla calssifica
    public void onClassificaClik(ActionEvent actionEvent) {
        try {
            windowUtilit.changeWhindow(actionEvent, "/com/matteorossi/play/view/ranckingPlayers.fxml", "Classifica Giocatori");
        }catch (IOException e){
            WhindowUtilit.showAlert("Errore", "Errore nel cambio pagina", "Errore nel cambio pagina");
            logger.error("Errore nel cambio della pagina {}", e);
        }
    }
}
