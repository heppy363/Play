package com.matteorossi.play.controllers;

import com.matteorossi.play.database.QueryDAO;
import com.matteorossi.play.models.LanguagesModel;
import com.matteorossi.play.utilitis.DataStorage;
import com.matteorossi.play.utilitis.GlobalConfig;
import com.matteorossi.play.utilitis.WhindowUtilit;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

/**
 * Controller per la selezione dei linguaggi di programmazione.
 * Presenta un'interfaccia dinamica con pulsanti posizionati casualmente che rappresentano
 * i diversi linguaggi disponibili, mostrando le percentuali di completamento per l'utente corrente.
 *
 * <p>Caratteristiche principali:</p>
 * <ul>
 *   <li>Generazione dinamica di pulsanti per ogni linguaggio disponibile</li>
 *   <li>Visualizzazione delle percentuali di completamento per ogni linguaggio</li>
 *   <li>Navigazione verso la schermata di selezione temi</li>
 *   <li>Posizionamento casuale degli elementi nell'interfaccia</li>
 * </ul>
 *
 * @see LanguagesModel Modello dati per i linguaggi
 * @see DataStorage Utility per la gestione dello stato
 * @see GlobalConfig Configurazione globale dell'utente
 */


public class LinguaggiProgrammazioneController {

    @FXML
    private AnchorPane languagesArchorPane;

    private final WhindowUtilit windowUtilit = new WhindowUtilit();
    private static final Logger logger = LoggerFactory.getLogger(IntruductionController.class);

    public void onLoginButtonClik(ActionEvent actionEvent) {
        try {
            windowUtilit.changeWhindow(actionEvent, "/com/matteorossi/play/view/login.fxml", "Login");
        } catch (IOException e) {
            logger.error("Errore nel cambio pagina {}", e);
        }
    }

    @FXML
    public void initialize() {
        List<LanguagesModel> languages = QueryDAO.getLanguagesIdName();
        generateButton(languages);
    }


    /**
     * Genera elementi UI dinamici per ogni linguaggio:
     * <pre>
     * +------------------------+
     * | [Nome linguaggio]      |
     * | Completato: XX.XX%     |
     * +------------------------+
     * </pre>
     * @param languages Lista dei linguaggi da visualizzare
     */
    private void generateButton(List<LanguagesModel> languages) {
        Random rand = new Random();
        int width = 500;
        int height = 900;
        List<Rectangle2D> occupiedAreas = new ArrayList<>();

        // Dimensioni stimate degli elementi con margine
        double elementWidth = 160;
        double elementHeight = 80;
        int maxAttempts = 1000;

        int userId = GlobalConfig.userID;
        Map<Integer, Double> completionMap = new HashMap<>();
        for (LanguagesModel lang : languages) {
            Double completionPercentage = QueryDAO.getOverallCompletionPercentage(lang.getId(), userId);
            completionMap.put(lang.getId(), completionPercentage != null ? completionPercentage : 0.0);
        }

        for (LanguagesModel lang : languages) {
            boolean positionFound = false;
            int attempts = 0;
            double x = 0;
            double y = 0;

            while (!positionFound && attempts < maxAttempts) {
                x = rand.nextDouble() * (width - elementWidth);
                y = rand.nextDouble() * (height - elementHeight);
                Rectangle2D newArea = new Rectangle2D(x, y, elementWidth, elementHeight);

                boolean overlaps = occupiedAreas.stream()
                        .anyMatch(area -> area.intersects(newArea));

                if (!overlaps) {
                    occupiedAreas.add(newArea);
                    positionFound = true;
                }
                attempts++;
            }

            // Creazione elementi UI
            VBox languageBox = createLanguageBox(lang, completionMap.get(lang.getId()));
            languageBox.setLayoutX(x);
            languageBox.setLayoutY(y);
            languagesArchorPane.getChildren().add(languageBox);
        }
    }

    private VBox createLanguageBox(LanguagesModel lang, Double completionPercentage) {
        Button languageButton = new Button(lang.getLanguageNameProperty());
        languageButton.setOnAction(event -> goToThemsScrin(lang, event));

        Label completionLabel = new Label(String.format("Completato: %.2f%%", completionPercentage));

        VBox box = new VBox(5);
        box.getChildren().addAll(languageButton, completionLabel);

        // Aggiungi stili per mantenere dimensioni consistenti
        box.setStyle("-fx-min-width: 150px; -fx-pref-width: 150px; -fx-max-width: 150px;");
        languageButton.setStyle("-fx-min-width: 140px; -fx-max-width: 140px;");

        return box;
    }

    private void goToThemsScrin(LanguagesModel languages, ActionEvent actionEvent) {
        try {
            System.out.println("Linguaggio ID " + languages.getId());
            DataStorage.setDataStorage("languageName", languages.getId());
            windowUtilit.changeWhindow(actionEvent, "/com/matteorossi/play/view/themsChois.fxml", "Scegli tema");
        } catch (IOException e) {
            logger.error("Errore IOException", e);
            WhindowUtilit.showAlert("Errore", "Non puoi andare ora alla schermata dei temi", "Informativa");
        }
        System.out.println("Linguaggio selezionato " + languages);
    }
}
