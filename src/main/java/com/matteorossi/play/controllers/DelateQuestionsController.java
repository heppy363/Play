package com.matteorossi.play.controllers;

import com.matteorossi.play.database.QueryDAO;
import com.matteorossi.play.models.QuestionModel;
import com.matteorossi.play.utilitis.WhindowUtilit;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
/**
 * Controller per la gestione dell'interfaccia di eliminazione domande.
 * Permette la visualizzazione e cancellazione delle domande memorizzate nel database,
 * con possibilità di tornare alla schermata amministrativa principale.
 *
 * <p>Utilizza una {@link TableView} JavaFX con colonne per:
 * <ul>
 *   <li>Testo della domanda</li>
 *   <li>Linguaggio di programmazione associato</li>
 *   <li>Azione di eliminazione tramite pulsante</li>
 * </ul></p>
 *
 * @see QuestionModel Modello dati per le domande
 * @see QueryDAO Classe per l'accesso al database
 * @see WhindowUtilit Utility per la gestione delle finestre
 */
public class DelateQuestionsController implements Initializable {

    @FXML
    private TableView<QuestionModel> questionsTable;

    @FXML
    private TableColumn<QuestionModel, String> questionColumn;

    @FXML
    private TableColumn<QuestionModel, String> languageColumn;

    @FXML
    private TableColumn<QuestionModel, Void> actionColumn;

    private final WhindowUtilit windowUtilit = new WhindowUtilit();
    private static final Logger logger = LoggerFactory.getLogger(DelateQuestionsController.class);

    private ObservableList<QuestionModel> questionsList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Configura le colonne
        questionColumn.setCellValueFactory(new PropertyValueFactory<>("question"));
        languageColumn.setCellValueFactory(new PropertyValueFactory<>("languageName"));

        // Configura la colonna con il pulsante di eliminazione
        configureButtonColumn(actionColumn, "Elimina", this::deleteQuestion);

        // Carica le domande dal database
        loadQuestions();
    }

    /**
     * Configura una colonna con un pulsante.
     *
     * @param column      La colonna da configurare.
     * @param buttonText  Il testo del pulsante.
     * @param action      L'azione da eseguire quando il pulsante viene cliccato.
     */
    private <T> void configureButtonColumn(TableColumn<T, Void> column, String buttonText, ButtonAction<T> action) {
        column.setCellFactory(col -> new TableCell<>() {
            private final Button button = new Button(buttonText);

            {
                button.setOnAction(event -> {
                    T item = getTableView().getItems().get(getIndex());
                    action.execute(item);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(button);
                }
            }
        });
    }

    /**
     * Carica le domande dal database e le visualizza nella tabella.
     */
    private void loadQuestions() {
        List<QuestionModel> questions = QueryDAO.getAllQuestions();
        questionsList.setAll(questions);
        questionsTable.setItems(questionsList);
    }

    /**
     * Elimina una domanda dal database.
     *
     * @param question La domanda da eliminare.
     */
    private void deleteQuestion(QuestionModel question) {
        try {
            if (QueryDAO.deleteQuestion(question.getId())) {
                questionsList.remove(question);
                WhindowUtilit.showAlert("Successo", "Domanda eliminata", "Operazione completata con successo.");
            }
        } catch (SQLException e) {
            logger.error("Errore durante l'eliminazione della domanda", e);
        }
    }

    @FXML
    public void anAdminButtonClik(ActionEvent actionEvent) {
        try {
            windowUtilit.changeWhindow(actionEvent, "/com/matteorossi/play/view/admin.fxml", "Pagina Admin");
        } catch (IOException e) {
            logger.error("Errore IOException", e);
        }
    }

    /**
     * Interfaccia funzionale per eseguire azioni sui pulsanti.
     */
    @FunctionalInterface
    private interface ButtonAction<T> {
        void execute(T item);
    }
}