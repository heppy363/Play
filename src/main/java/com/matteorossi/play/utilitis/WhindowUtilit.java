package com.matteorossi.play.utilitis;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Classe utilitaria per la gestione delle finestre e degli alert nell'applicazione JavaFX.
 * Fornisce metodi per cambiare scena e mostrare messaggi di errore standardizzati.
 *
 * <p>Responsabilità principali:</p>
 * <ul>
 *   <li>Navigazione tra view FXML</li>
 *   <li>Visualizzazione alert di errore preconfigurati</li>
 *   <li>Centralizzazione della logica di gestione UI</li>
 * </ul>
 *
 * @see FXMLLoader Per il caricamento delle view FXML
 * @see Alert Per la creazione di dialoghi informativi
 */

public class WhindowUtilit {


    public void changeWhindow(ActionEvent actionEvent, String fileToLoad, String title) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fileToLoad));
        Parent root = loader.load();

        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.setTitle(title);

        stage.setScene(new Scene(root));
        stage.show();
    }

    public void changeWhindow(Stage stage, String path, String title) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource(path));
        stage.setTitle(title);
        stage.setScene(new Scene(root));
        stage.show();
    }


    public static void showAlert(String title, String content, String headerText) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(content);
        alert.showAndWait();
    }

}
