package com.matteorossi.play.controllers;

import com.matteorossi.play.database.QueryDAO;
import com.matteorossi.play.models.DifficultyModel;
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
 * Controller per la gestione della selezione della difficoltà delle domande.
 * Presenta un'interfaccia dinamica con pulsanti posizionati casualmente che rappresentano
 * i diversi livelli di difficoltà, mostrando anche le percentuali di completamento per l'utente corrente.
 *
 * <p>Caratteristiche principali:</p>
 * <ul>
 *   <li>Generazione dinamica di pulsanti per ogni livello di difficoltà disponibile</li>
 *   <li>Visualizzazione delle percentuali di completamento per ogni difficoltà</li>
 *   <li>Navigazione verso la schermata delle domande in base alla difficoltà selezionata</li>
 * </ul>
 *
 * @see DifficultyModel Modello dati per le difficoltà
 * @see QueryDAO Classe per l'accesso al database
 * @see DataStorage Utility per la gestione dello stato dell'applicazione
 */
public class DiffiChoisController {

    @FXML
    private AnchorPane anchorPaneDifficult;

    private final WhindowUtilit windowUtilit = new WhindowUtilit();
    private static final Logger logger = LoggerFactory.getLogger(DiffiChoisController.class);

    @FXML
    private void initialize() {
        List<DifficultyModel> difficulties = QueryDAO.getDifficulty();
        generateButton(difficulties);
    }

    private void generateButton(List<DifficultyModel> difficulties) {
        Random rand = new Random();
        int width = 500;
        int height = 900;
        List<Rectangle2D> occupiedAreas = new ArrayList<>();

        // Dimensioni stimate degli elementi con margine
        double elementWidth = 160;
        double elementHeight = 80;
        int maxAttempts = 1000;

        int userId = GlobalConfig.userID;
        Map<Integer, Double> completionMap = QueryDAO.getOverallCompletionPercentageForAllDifficulties(userId);

        for (DifficultyModel diff : difficulties) {
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
                logger.warn("Impossibile trovare posizione per la difficoltà: {}", diff.getDifficulty());
                continue;
            }

            // Creazione elementi UI
            VBox difficultBox = createDifficultyBox(diff, completionMap.getOrDefault(diff.getId(), 0.0));
            difficultBox.setLayoutX(x);
            difficultBox.setLayoutY(y);
            anchorPaneDifficult.getChildren().add(difficultBox);
        }
    }

    private VBox createDifficultyBox(DifficultyModel diff, double completionPercentage) {
        Button difficultButton = new Button(diff.getDifficulty());
        difficultButton.setOnAction(event -> goToThemsScrin(diff, event));

        Label completionLabel = new Label(String.format("Completato: %.2f%%", completionPercentage));

        VBox box = new VBox(5);
        box.getChildren().addAll(difficultButton, completionLabel);

        // Stili per dimensioni consistenti
        box.setStyle("-fx-min-width: 150px; -fx-pref-width: 150px; -fx-max-width: 150px;");
        difficultButton.setStyle("-fx-min-width: 140px; -fx-max-width: 140px;");

        return box;
    }

    private void goToThemsScrin(DifficultyModel difficult, ActionEvent actionEvent) {
        try {
            System.out.println("Difficoltà ID: " + difficult.getId());
            DataStorage.setDataStorage("difficulty", difficult.getId());
            windowUtilit.changeWhindow(actionEvent, "/com/matteorossi/play/view/generateQuestions.fxml", "Domande");
        } catch (IOException e) {
            logger.error("Errore IOException", e);
            WhindowUtilit.showAlert("Errore", "Non puoi andare ora alla schermata delle difficoltà", "Informativa");
        }

        System.out.println("Difficoltà selezionata: " + difficult);
    }
}