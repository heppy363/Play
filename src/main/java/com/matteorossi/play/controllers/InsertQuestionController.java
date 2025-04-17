package com.matteorossi.play.controllers;

import com.matteorossi.play.database.QueryDAO;
import com.matteorossi.play.models.DifficultyModel;
import com.matteorossi.play.models.LanguagesModel;
import com.matteorossi.play.models.ThemsModel;
import com.matteorossi.play.utilitis.WhindowUtilit;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.List;
/**
 * Controller per l'inserimento di nuove domande nel sistema.
 * Gestisce la selezione di linguaggi, categorie e difficoltà tramite tabelle interattive
 * con checkbox, permettendo l'inserimento di domande sia a scelta multipla che aperte.
 *
 * <p>Struttura principale:</p>
 * <ul>
 *   <li>Tabelle di selezione con checkbox per linguaggi, categorie e difficoltà</li>
 *   <li>Form dinamico che cambia in base al tipo di domanda selezionato</li>
 *   <li>Validazione degli input e gestione degli errori</li>
 * </ul>
 *
 * @see LanguagesModel Modello dati per i linguaggi
 * @see ThemsModel Modello dati per le categorie
 * @see DifficultyModel Modello dati per le difficoltà
 */

public class InsertQuestionController{

    @FXML
    private TableView<LanguagesModel> languageTable;
    @FXML
    private TableColumn<LanguagesModel, String> languageColumn;
    @FXML
    private TableColumn<LanguagesModel, CheckBox> languageSelectColumn;


    @FXML
    private TableView<ThemsModel> categoryTable;
    @FXML
    private TableColumn<ThemsModel, String> categoryColumn;
    @FXML
    private TableColumn<ThemsModel, CheckBox> categorySelectColumn;

    @FXML
    private TableView<DifficultyModel> difficultyTable;
    @FXML
    private TableColumn<DifficultyModel, String> difficultyColumn;
    @FXML
    private TableColumn<DifficultyModel, CheckBox> difficultySelectColumn;

    @FXML
    private TextArea questionTextArea1;
    @FXML
    private TextArea questionTextArea2;
    @FXML
    private TextArea answerTextArea;

    @FXML
    private CheckBox multipleChoiceCheckbox;
    @FXML
    private CheckBox writtenQuestionCheckbox;

    @FXML
    private CheckBox correctACheckbox;
    @FXML
    private CheckBox correctBCheckbox;
    @FXML
    private CheckBox correctCCheckbox;
    @FXML
    private CheckBox correctDCheckbox;

    @FXML
    private TextField answerATextField;
    @FXML
    private TextField answerBTextField;
    @FXML
    private TextField answerCTextField;
    @FXML
    private TextField answerDTextField;



    private LanguagesModel selectedLanguage = null;
    private ThemsModel selectedCategory = null;
    private DifficultyModel selectedDifficulty = null;


    private final WhindowUtilit windowUtilit = new WhindowUtilit();
    private static final Logger logger = LoggerFactory.getLogger(InsertQuestionController.class);


    public void onCheckQuestionClick(ActionEvent actionEvent) {
    }


    public void onInsertButton(ActionEvent actionEvent) {
        if (multipleChoiceCheckbox.isSelected()) {
            // Recupera la domanda dal TextArea
            String question = questionTextArea1.getText();

            // Crea una lista con tutti i CheckBox
            List<CheckBox> checkBoxes = List.of(correctACheckbox, correctBCheckbox, correctCCheckbox, correctDCheckbox);

            // Trova il CheckBox selezionato (se esiste)
            CheckBox selectedCheckbox = checkBoxes.stream()
                    .filter(CheckBox::isSelected)
                    .findFirst()
                    .orElse(null);

            //Controllo se tutte le risposte sono piene
            if(answerATextField != null && answerBTextField != null && answerCTextField != null && answerDTextField != null) {
                //Controllo che la risposta coretta sia stata inserita
                if (selectedCheckbox != null) {
                    //Inserisco i dati nel DB
                    try {
                        QueryDAO.insertQuestion(
                                selectedLanguage.getId(),
                                selectedCategory.getId(),
                                selectedDifficulty.getId(),
                                "multiple_choice",
                                question,
                                answerATextField.getText(),
                                answerBTextField.getText(),
                                answerCTextField.getText(),
                                answerDTextField.getText(),
                                selectedCheckbox.getText(),
                                null
                        );

                        WhindowUtilit.showAlert("Sucesso", "Domanda inserita corettamente", "Informativa");

                    }catch (SQLException e){
                        WhindowUtilit.showAlert("Errore", "Domanda NON inserita corettamente", "Informativa");
                        logger.error("Errore SQL{}", e.getMessage());
                    }
                }
            }else {
                WhindowUtilit.showAlert("Errore", "Inserire tutte le possibili risposte", "Errore");
            }

        } else if (writtenQuestionCheckbox.isSelected()) {
            // Gestione della domanda scritta
            String question = questionTextArea2.getText();
            String answer = answerTextArea.getText();

            if (question != null && answer != null) {

                try {
                    QueryDAO.insertQuestion(
                            selectedLanguage.getId(),
                            selectedCategory.getId(),
                            selectedDifficulty.getId(),
                            "code",
                            question,
                            null,
                            null,
                            null,
                            null,
                            null,
                            answer

                    );

                    WhindowUtilit.showAlert("Sucesso", "Domanda inserita corettamente", "Informativa");

                } catch (SQLException e) {
                    WhindowUtilit.showAlert("Errore", "Domanda NON inserita corettamente", "Informativa");
                    logger.error("Errore SQL{}", e.getMessage());
                }

            }else{
                WhindowUtilit.showAlert("Errore", "Si prega di riempire tutte le caselle", "Informativa");
            }

            System.out.println("Domanda con scrittura di codice");
            System.out.println("Domanda: " + question);
            System.out.println("Risposta: " + answer);
        }
    }




    public void onAdminButton(ActionEvent actionEvent) {
        try {
            windowUtilit.changeWhindow(actionEvent,"/com/matteorossi/play/view/admin.fxml","Admin page");
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }




    @FXML
    public void initialize() {
        languageColumn.setCellValueFactory(new PropertyValueFactory<>("languageNameProperty"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("themsNameProperty"));
        difficultyColumn.setCellValueFactory(new PropertyValueFactory<>("difficultyNameProperty"));


        configureCheckBoxColumn(languageSelectColumn, this::handleCheckBoxLanguages, "language");
        configureCheckBoxColumn(categorySelectColumn, this::handleCheckBoxThems, "category");
        configureCheckBoxColumn(difficultySelectColumn, this::handleCheckBoxDifficult, "difficulty");


        // Gestione esclusività dei checkbox delle risposte
        configureSingleSelection(correctACheckbox, List.of(correctBCheckbox, correctCCheckbox, correctDCheckbox));
        configureSingleSelection(correctBCheckbox, List.of(correctACheckbox, correctCCheckbox, correctDCheckbox));
        configureSingleSelection(correctCCheckbox, List.of(correctACheckbox, correctBCheckbox, correctDCheckbox));
        configureSingleSelection(correctDCheckbox, List.of(correctACheckbox, correctBCheckbox, correctCCheckbox));

        // Gestione esclusività tra tipi di domande
        configureSingleSelection(multipleChoiceCheckbox, List.of(writtenQuestionCheckbox));
        configureSingleSelection(writtenQuestionCheckbox, List.of(multipleChoiceCheckbox));


        loadLanguage();
        loadThemes();
        loadDifficulty();
    }

    private void configureSingleSelection(CheckBox source, List<CheckBox> others) {
        source.setOnAction(event -> {
            if (source.isSelected()) {
                others.forEach(cb -> cb.setSelected(false));
            }
        });
    }


    private <T> void configureCheckBoxColumn(TableColumn<T, CheckBox> column, CheckBoxAction<T> action, String type) {
        column.setCellFactory(col -> new TableCell<>() {
            private final CheckBox checkBox = new CheckBox();

            {
                checkBox.setOnAction(event -> {
                    T item = getTableView().getItems().get(getIndex());
                    action.execute(item, checkBox.isSelected());
                });
            }

            @Override
            protected void updateItem(CheckBox item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(checkBox);

                    // Gestisce lo stato del CheckBox in base al tipo
                    if (type.equals("language")) {
                        checkBox.setSelected(getTableRow().getItem() == selectedLanguage);
                    } else if (type.equals("category")) {
                        checkBox.setSelected(getTableRow().getItem() == selectedCategory);
                    } else if (type.equals("difficulty")) {
                        checkBox.setSelected(getTableRow().getItem() == selectedDifficulty);
                    }
                }
            }
        });
    }


    private void loadLanguage() {
        List<LanguagesModel> languages = QueryDAO.getLanguagesIdName();
        ObservableList<LanguagesModel> observableList = FXCollections.observableList(languages);
        languageTable.setItems(observableList);
    }

    private void loadThemes() {
        List<ThemsModel> thems = QueryDAO.getThemIdName();
        ObservableList<ThemsModel> observableList = FXCollections.observableList(thems);
        categoryTable.setItems(observableList);
    }

    private void loadDifficulty() {
        List<DifficultyModel> difficult = QueryDAO.getDifficultIdName();
        ObservableList<DifficultyModel> observableList = FXCollections.observableList(difficult);
        difficultyTable.setItems(observableList);
    }

    private void handleCheckBoxLanguages(LanguagesModel language, boolean isSelected) {

        if (isSelected) {
            selectedLanguage = language;
            System.out.println("Linguaggio " + selectedLanguage.getId());
        } else {
            if (selectedLanguage == language) {
                selectedLanguage = null;
            }
        }
       languageTable.refresh();
    }

    private void handleCheckBoxThems(ThemsModel thems, boolean isSelected) {

        if (isSelected) {
            selectedCategory = thems;
            System.out.println("Categorai "+ this.selectedCategory.getId());
        } else {
            if (selectedCategory == thems) {
                selectedCategory = null;
            }
        }
        categoryTable.refresh();
    }

    private void handleCheckBoxDifficult(DifficultyModel difficult, boolean isSelected) {
        if (isSelected) {
            selectedDifficulty = difficult;
            System.out.println("Difficolta " + this.selectedDifficulty.getId());
        } else {
            if (selectedDifficulty == difficult) {
                selectedDifficulty = null;
            }
        }
        difficultyTable.refresh();
    }

    //Mi porta alla pagina di eliminazione delle DOMANDE
    public void onDelateQuestionsClick(ActionEvent actionEvent) {
        try {
            windowUtilit.changeWhindow(actionEvent,"/com/matteorossi/play/view/delateQuestions.fxml","Delate questions");
        }catch (Exception e) {
            WhindowUtilit.showAlert("Errore", "Errore nel cambio pagina", "errore");
            logger.error("Errore nel cambio pagfina {}", e);
        }
    }


    // Interfaccia funzionale per gestire l'azione del CheckBox
    @FunctionalInterface
    private interface CheckBoxAction<T> {
        void execute(T item, boolean isSelected);
    }
}
