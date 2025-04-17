package com.matteorossi.play.controllers;

import com.matteorossi.play.database.QueryDAO;
import com.matteorossi.play.models.QuestionModel;
import com.matteorossi.play.utilitis.DataStorage;
import com.matteorossi.play.utilitis.WhindowUtilit;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
//import org.glassfish.jersey.process.internal.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.*;

import static com.matteorossi.play.utilitis.GlobalConfig.SCORE_QUESTIONS;
import static com.matteorossi.play.utilitis.GlobalConfig.userID;
/**
 * Controller per la gestione del test di programmazione interattivo.
 * Gestisce il flusso delle domande, la verifica delle risposte e il salvataggio del progresso utente.
 *
 * <p>Caratteristiche principali:</p>
 * <ul>
 *   <li>Visualizzazione domande a scelta multipla o aperte</li>
 *   <li>Navigazione tra domande con pulsanti Next/Previous</li>
 *   <li>Salvataggio automatico progressi allo shutdown</li>
 *   <li>Sblocco difficoltà superiori al superamento del test</li>
 *   <li>Calcolo punteggio in tempo reale</li>
 * </ul>
 *
 * @see QuestionModel Modello dati per le domande
 * @see DataStorage Gestione dello stato dell'applicazione
 * @see QueryDAO Accesso al database
 */

public class GenerateQuestionsController {

    @FXML
    private Label titleLabel;
    @FXML
    private Button nextButton, previousButton, confermButton;
    @FXML
    private TextField questionsTextField;
    @FXML
    private TextArea insertQuestionsTextArea;
    @FXML
    private CheckBox CorrettaA, CorrettaB, CorrettaC, CorrettaD;
    @FXML
    private Label QuestionsA1, QuestionsB1, QuestionsC1, QuestionsD1;

    private List<QuestionModel> questions;
    private int currentIndex = 0;
    private Map<Integer, Boolean> userCorrectAnswers = new HashMap<>();
    private final WhindowUtilit windowUtilit = new WhindowUtilit();
    private static final Logger logger = LoggerFactory.getLogger(GenerateQuestionsController.class);

    // Aggiunto per salvare lo stato al shutdown
    private boolean testCompleted = false;
    private Integer languageId;
    private Integer themeId;
    private Integer difficultyId;

    @FXML
    public void initialize() {
        languageId = DataStorage.getDataAs("languageName", Integer.class);
        themeId = DataStorage.getDataAs("themeName", Integer.class);
        difficultyId = DataStorage.getDataAs("difficulty", Integer.class);
        System.out.println("Difficulty ID: " + difficultyId);
        questions = QueryDAO.getQuestions(themeId, languageId, difficultyId);
        System.out.println("Questions: " + questions.size());

        currentIndex = 0;


        // Torno ai linguaggi se non ci sono domande
        if (questions.isEmpty()) {
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Nessuna domanda trovata");
                alert.setHeaderText(null);
                alert.setContentText("Non ci sono domande disponibili per la categoria selezionata.");

                alert.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        try {
                            Stage stage = (Stage) titleLabel.getScene().getWindow();
                            windowUtilit.changeWhindow(stage, "/com/matteorossi/play/view/programmingLanguages.fxml", "Linguaggi di programmazione");
                        } catch (IOException e) {
                            WhindowUtilit.showAlert("Errore", "Impossibile tornare alla schermata dei linguaggi", e.getMessage());
                        }
                    }
                });
            });
        }



        if (!questions.isEmpty()) {
            Collections.shuffle(questions);
            showQuestion();
        }


        titleLabel.setText("Domanda numero " + (currentIndex + 1) + " di " + questions.size());

        configureCheckBoxBehavior();

        // Aggiunto shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(this::saveProgressOnShutdown));
    }



    // Aggiunto metodo per salvataggio al shutdown
    private void saveProgressOnShutdown() {
        if (testCompleted || questions == null || questions.isEmpty()) {
            return;
        }

        // Salva l'ultima risposta corrente
        if (currentIndex >= 0 && currentIndex < questions.size()) {
            checkUserAnswer();
        }

        long correctCount = userCorrectAnswers.values().stream().filter(Boolean::booleanValue).count();
        if (correctCount > 0) {
            int totalScore = (int) (correctCount * SCORE_QUESTIONS);
            QueryDAO.updateUserProgress(userID, themeId, languageId, difficultyId, totalScore);
            System.out.println("Progressi salvati allo shutdown: " + totalScore + " punti");
        }
    }

    // Resto del codice rimane ESATTAMENTE uguale...
    private void configureCheckBoxBehavior() {
        // Rendi i checkbox mutualmente esclusivi
        List<CheckBox> checkBoxes = Arrays.asList(CorrettaA, CorrettaB, CorrettaC, CorrettaD);
        for (CheckBox checkBox : checkBoxes) {
            checkBox.setOnAction(event -> {
                if (checkBox.isSelected()) {
                    checkBoxes.forEach(cb -> {
                        if (cb != checkBox) cb.setSelected(false);
                    });
                }
            });
        }
    }

    private void showQuestion() {
        if (questions.isEmpty() || currentIndex < 0 || currentIndex >= questions.size()) {
            System.err.println("Errore: indice non valido o lista vuota.");
            return;
        }

        QuestionModel currentQuestion = questions.get(currentIndex);
        questionsTextField.setText(currentQuestion.getQuestion());

        boolean isMultipleChoice = "multiple_choice".equals(currentQuestion.getQuestionType());
        insertQuestionsTextArea.setVisible(!isMultipleChoice);
        QuestionsA1.setVisible(isMultipleChoice);
        QuestionsB1.setVisible(isMultipleChoice);
        QuestionsC1.setVisible(isMultipleChoice);
        QuestionsD1.setVisible(isMultipleChoice);

        CorrettaA.setVisible(isMultipleChoice);
        CorrettaB.setVisible(isMultipleChoice);
        CorrettaC.setVisible(isMultipleChoice);
        CorrettaD.setVisible(isMultipleChoice);

        if (isMultipleChoice) {
            QuestionsA1.setText(currentQuestion.getOptionA());
            QuestionsB1.setText(currentQuestion.getOptionB());
            QuestionsC1.setText(currentQuestion.getOptionC());
            QuestionsD1.setText(currentQuestion.getOptionD());
            CorrettaA.setSelected(false);
            CorrettaB.setSelected(false);
            CorrettaC.setSelected(false);
            CorrettaD.setSelected(false);
        } else {
            CorrettaA.setVisible(false);
            CorrettaB.setVisible(false);
            CorrettaC.setVisible(false);
            CorrettaD.setVisible(false);
        }
    }

    @FXML
    public void onPreviousQuestionButtonClick(ActionEvent actionEvent) {
        checkUserAnswer();
        if (currentIndex > 0) {
            currentIndex--;
            showQuestion();
            titleLabel.setText("Domanda numero " + (currentIndex + 1) + " di " + questions.size());
        }
    }

    private void checkUserAnswer() {
        QuestionModel currentQuestion = questions.get(currentIndex);
        boolean isCorrect = false;
        String userAnswer = "";

        System.out.println("Verifica risposta per domanda: " + currentQuestion.getQuestion());

        if ("multiple_choice".equals(currentQuestion.getQuestionType())) {
            String correctOption = currentQuestion.getCorrectOption().trim().replaceAll("\\s+", "");
            System.out.println(correctOption);
            System.out.println("Risposta corretta dal DB: " + correctOption.trim());

            if (CorrettaA.isSelected()) System.out.println("Utente ha selezionato: " + CorrettaA.getId());
            if (CorrettaB.isSelected()) System.out.println("Utente ha selezionato: " + CorrettaB.getId());
            if (CorrettaC.isSelected()) System.out.println("Utente ha selezionato: " + CorrettaC.getId());
            if (CorrettaD.isSelected()) System.out.println("Utente ha selezionato: " + CorrettaD.getId());

            if (CorrettaA.isSelected() && CorrettaA.getId().equals(correctOption.trim())) isCorrect = true;
            if (CorrettaB.isSelected() && CorrettaB.getId().equals(correctOption.trim())) isCorrect = true;
            if (CorrettaC.isSelected() && CorrettaC.getId().equals(correctOption.trim())) isCorrect = true;
            if (CorrettaD.isSelected() && CorrettaD.getId().equals(correctOption.trim())) isCorrect = true;
        } else {
            userAnswer = insertQuestionsTextArea.getText().trim().toLowerCase();
            String correctAnswer = currentQuestion.getCodeSolution().trim().toLowerCase();

            System.out.println("Risposta utente (scritta): " + userAnswer);
            System.out.println("Risposta corretta dal DB (scritta): " + correctAnswer);

            isCorrect = userAnswer.equals(correctAnswer);
        }

        userCorrectAnswers.put(currentIndex, isCorrect);
        System.out.println("Risposta dell'utente è corretta? " + isCorrect);
        QueryDAO.updateUserAnswer(userID, currentQuestion.getId(), isCorrect);
    }

    @FXML
    public void onNextQuestionButtonClick(ActionEvent actionEvent) {
        System.out.println("Passaggio alla domanda successiva...");
        checkUserAnswer();
        if (currentIndex < questions.size() - 1) {
            currentIndex++;
            showQuestion();
            titleLabel.setText("Domanda numero " + (currentIndex + 1) + " di " + questions.size());
            System.out.println("Nuova domanda: " + questions.get(currentIndex).getQuestion());
        }
        if (currentIndex == questions.size() - 1) confermButton.setVisible(true);
    }

    @FXML
    public void onConfermaClick(ActionEvent actionEvent) {
        testCompleted = true; // Aggiunto per marcare il test come completato
        System.out.println("Conferma del test...");

        long correctCount = userCorrectAnswers.values().stream().filter(Boolean::booleanValue).count();
        double percentage = (double) correctCount / questions.size();

        System.out.println("Numero di risposte corrette: " + correctCount);
        System.out.println("Percentuale di correttezza: " + (percentage * 100) + "%");

        if (percentage >= 0.5) {
            int totalScore = (int) (correctCount * SCORE_QUESTIONS);
            System.out.println("Salvataggio del punteggio: " + totalScore);
            QueryDAO.updateUserProgress(userID, themeId, languageId, difficultyId, totalScore);

            int currentLevelDifficulty = QueryDAO.getLevelDifficultyById(difficultyId);
            Integer nextDifficultyId = QueryDAO.getNextDifficultyId(currentLevelDifficulty);

            if (nextDifficultyId != null) {
                List<QuestionModel> nextDifficultyQuestions = QueryDAO.getQuestions(themeId, languageId, nextDifficultyId);

                if (!nextDifficultyQuestions.isEmpty()) {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Test completato");
                    alert.setHeaderText("Hai superato il test!");
                    alert.setContentText("Vuoi continuare con una difficoltà maggiore?");

                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.isPresent() && result.get() == ButtonType.OK) {
                        DataStorage.setDataStorage("difficulty", nextDifficultyId);
                        difficultyId = nextDifficultyId; // Aggiorna la difficoltà corrente
                        questions = nextDifficultyQuestions;
                        currentIndex = 0;
                        testCompleted = false; // Resetta per il nuovo test
                        userCorrectAnswers.clear();
                        titleLabel.setText("Domanda numero " + (currentIndex + 1) + " di " + questions.size());
                        showQuestion();
                    } else {
                        Alert infoAlert = new Alert(Alert.AlertType.INFORMATION);
                        infoAlert.setTitle("Test completato");
                        infoAlert.setHeaderText(null);
                        infoAlert.setContentText("Hai completato il test! Il tuo punteggio è stato aggiornato. Tornerai ai linguaggi");
                        infoAlert.showAndWait();
                        try {
                            windowUtilit.changeWhindow(actionEvent, "/com/matteorossi/play/view/programmingLanguages.fxml", "Linguaggi programmazione");
                        }catch (IOException e){
                            WhindowUtilit.showAlert("Errore", "Errore nel cambio pagina", "Errore nel cambio pagina");
                            logger.error("Errore nel cambio della pagina {}", e);
                        }
                    }
                } else {
                    Alert infoAlert = new Alert(Alert.AlertType.INFORMATION);
                    infoAlert.setTitle("Test completato");
                    infoAlert.setHeaderText(null);
                    infoAlert.setContentText("Hai completato tutte le domande per questa categoria e difficoltà!");
                    infoAlert.showAndWait();
                    try {
                        windowUtilit.changeWhindow(actionEvent, "/com/matteorossi/play/view/programmingLanguages.fxml", "Linguaggi programmazione");
                    }catch (IOException e){
                        WhindowUtilit.showAlert("Errore", "Errore nel cambio pagina", "Errore nel cambio pagina");
                        logger.error("Errore nel cambio della pagina {}", e);
                    }
                }
            } else {
                Alert infoAlert = new Alert(Alert.AlertType.INFORMATION);
                infoAlert.setTitle("Test completato");
                infoAlert.setHeaderText(null);
                infoAlert.setContentText("Hai completato tutte le difficoltà per questa categoria e linguaggio!");
                infoAlert.showAndWait();
            }
        } else {
            System.out.println("Punteggio insufficiente per il salvataggio.");

            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Test non superato");
            alert.setHeaderText(null);
            alert.setContentText("Mi dispiace, non hai superato il test.");
            alert.showAndWait();

            try {
                windowUtilit.changeWhindow(actionEvent, "/com/matteorossi/play/view/programmingLanguages.fxml", "Linguaggi programmazione");
            }catch (IOException e){
                WhindowUtilit.showAlert("Errore", "Errore nel cambio pagina", "Errore nel cambio pagina");
                logger.error("Errore nel cambio della pagina {}", e);
            }
        }
    }
}