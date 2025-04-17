package com.matteorossi.play.controllers;

import com.matteorossi.play.database.QueryDAO;
import com.matteorossi.play.models.PlayerRankingModel;
import com.matteorossi.play.utilitis.WhindowUtilit;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Controller per la visualizzazione della classifica dei giocatori.
 * Mostra una tabella con le posizioni, gli username e i punteggi totali degli utenti.
 *
 * <p>Caratteristiche principali:</p>
 * <ul>
 *   <li>Tabella ordinata per punteggio decrescente</li>
 *   <li>Collegamento diretto ai dati del database</li>
 *   <li>Navigazione verso altre view principali</li>
 * </ul>
 *
 * @see PlayerRankingModel Modello dati per la classifica
 * @see QueryDAO Accesso al database per il ranking
 */

public class RanckingPlayersControllers {

    @FXML
    private TableView<PlayerRankingModel> rankingTable;

    @FXML
    private TableColumn<PlayerRankingModel, Integer> positionColumn;

    @FXML
    private TableColumn<PlayerRankingModel, String> usernameColumn;

    @FXML
    private TableColumn<PlayerRankingModel, Integer> totalScoreColumn;


    private final WhindowUtilit windowUtilit = new WhindowUtilit();
    private static final Logger logger = LoggerFactory.getLogger(RanckingPlayersControllers.class);


    @FXML
    public void onGiocaClik(ActionEvent actionEvent) {
        try {
            windowUtilit.changeWhindow(actionEvent, "/com/matteorossi/play/view/programmingLanguages.fxml", "Linguaggi programmazione");
        }catch (IOException e){
            WhindowUtilit.showAlert("Errore", "Errore nel cambio pagina", "Errore nel cambio pagina");
            logger.error("Errore nel cambio della pagina {}", e);
        }
    }

    @FXML
    public void onRegoleClik(ActionEvent actionEvent) {
        try {
            windowUtilit.changeWhindow(actionEvent, "/com/matteorossi/play/view/roulseAndInformation.fxml", "Linguaggi programmazione");
        }catch (IOException e){
            WhindowUtilit.showAlert("Errore", "Errore nel cambio pagina", "Errore nel cambio pagina");
            logger.error("Errore nel cambio della pagina {}", e);
        }
    }


    @FXML
    public void initialize() {
        positionColumn.setCellValueFactory(new PropertyValueFactory<>("position"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        totalScoreColumn.setCellValueFactory(new PropertyValueFactory<>("totalScore"));

        loadRankingData();
    }

    private void loadRankingData() {
        ObservableList<PlayerRankingModel> rankingList = FXCollections.observableArrayList(QueryDAO.getPlayerRanking());
        rankingTable.setItems(rankingList);
    }

}
