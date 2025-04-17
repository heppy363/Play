package com.matteorossi.play.controllers;

import com.matteorossi.play.database.QueryDAO;
import com.matteorossi.play.models.ThemsModel;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Controller per la selezione dei temi di apprendimento.
 * Presenta un'interfaccia dinamica con pulsanti posizionati casualmente che rappresentano
 * i diversi temi disponibili, mostrando le percentuali di completamento per l'utente corrente.
 *
 * <p>Caratteristiche principali:</p>
 * <ul>
 *   <li>Generazione dinamica di pulsanti per ogni tema disponibile</li>
 *   <li>Visualizzazione delle percentuali di completamento per ogni tema</li>
 *   <li>Navigazione verso la schermata di selezione difficoltà</li>
 *   <li>Posizionamento casuale degli elementi nell'interfaccia</li>
 * </ul>
 *
 * @see ThemsModel Modello dati per i temi
 * @see DataStorage Utility per la gestione dello stato
 * @see GlobalConfig Configurazione globale dell'utente
 */

public class ThemsChoisController {

    @FXML
    private AnchorPane anchorPaneThems;

    private final WhindowUtilit windowUtilit = new WhindowUtilit();
    private static final Logger logger = LoggerFactory.getLogger(ThemsChoisController.class);

    @FXML
    private void initialize() {
        List<ThemsModel> thems = QueryDAO.getThemIdName();
        generateButton(thems);
    }

    /**
     * Genera elementi UI dinamici per ogni tema:
     * <pre>
     * +----------------------+
     * | [Nome tema]          |
     * | Completato: XX.XX%   |
     * +----------------------+
     * </pre>
     * @param thems Lista dei temi da visualizzare
     */
    @FXML
    private void generateButton(List<ThemsModel> thems) {
        Random rand = new Random();
        int width = 500;
        int height = 900;
        List<Rectangle2D> occupiedAreas = new ArrayList<>();

        // Dimensioni stimate degli elementi con margine
        double elementWidth = 160;
        double elementHeight = 80;
        int maxAttempts = 1000;

        int userId = GlobalConfig.userID;
        Map<Integer, Double> completionMap = QueryDAO.getOverallCompletionPercentageForAllThemes(userId);

        for (ThemsModel them : thems) {
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

            if (!positionFound) {
                logger.warn("Impossibile trovare posizione per il tema: {}", them.getThemsNameProperty());
                continue;
            }

            // Creazione elementi UI
            VBox themBox = createThemeBox(them, completionMap.getOrDefault(them.getId(), 0.0));
            themBox.setLayoutX(x);
            themBox.setLayoutY(y);
            anchorPaneThems.getChildren().add(themBox);
        }
    }

    private VBox createThemeBox(ThemsModel them, Double completionPercentage) {
        Button themsButton = new Button(them.getThemsNameProperty());
        themsButton.setOnAction(event -> goToThemsScrin(them, event));

        Label completionLabel = new Label(String.format("Completato: %.2f%%", completionPercentage));

        VBox box = new VBox(5);
        box.getChildren().addAll(themsButton, completionLabel);

        // Stili per dimensioni consistenti
        box.setStyle("-fx-min-width: 150px; -fx-pref-width: 150px; -fx-max-width: 150px;");
        themsButton.setStyle("-fx-min-width: 140px; -fx-max-width: 140px;");

        return box;
    }

    private void goToThemsScrin(ThemsModel thems, ActionEvent actionEvent) {
        try {
            System.out.println("Id del tema: " + thems.getId());
            DataStorage.setDataStorage("themeName", thems.getId());
            windowUtilit.changeWhindow(actionEvent, "/com/matteorossi/play/view/difficultChois.fxml", "Scegli difficoltà");
        } catch (IOException e) {
            logger.error("Errore IOException", e);
            WhindowUtilit.showAlert("Errore", "Non puoi andare ora alla schermata delle difficoltà", "Informativa");
        }

        System.out.println("Tema selezionato: " + thems);
    }
}
