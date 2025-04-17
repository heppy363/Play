package com.matteorossi.play.models;

/**
 * Modello dati per la rappresentazione completa di una domanda nel sistema.
 * Contiene tutte le informazioni necessarie per gestire domande sia a scelta multipla
 * che a risposta aperta/codice, con supporto a relazioni con altre entità del sistema.
 *
 * <p>Campi principali:</p>
 * <ul>
 *   <li>Identificativi univoci (domanda, linguaggio, tema, difficoltà)</li>
 *   <li>Testo della domanda e opzioni di risposta</li>
 *   <li>Soluzioni per entrambi i tipi di domanda</li>
 *   <li>Nome del linguaggio per visualizzazione diretta</li>
 * </ul>
 *
 * @see com.matteorossi.play.database.QueryDAO Metodi correlati per persistenza
 * @see com.matteorossi.play.controllers.AdminController Utilizzo nella gestione domande
 */

public class QuestionModel {
    private int id;
    private int languageId;
    private int themeId;
    private int difficultyId;
    private String questionType;
    private String question;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private String correctOption;
    private String codeSolution;
    private String languageName;

    public QuestionModel(int id, int languageId, int themeId, int difficultyId, String questionType, String question, String optionA, String optionB, String optionC, String optionD, String correctOption, String codeSolution) {
        this.id = id;
        this.languageId = languageId;
        this.themeId = themeId;
        this.difficultyId = difficultyId;
        this.questionType = questionType;
        this.question = question;
        this.optionA = optionA;
        this.optionB = optionB;
        this.optionC = optionC;
        this.optionD = optionD;
        this.correctOption = correctOption;
        this.codeSolution = codeSolution;
    }

    // Costruttore aggiornato
    public QuestionModel(int id, int languageId, int themeId, int difficultyId, String questionType, String question, String optionA, String optionB, String optionC, String optionD, String correctOption, String codeSolution, String languageName) {
        this.id = id;
        this.languageId = languageId;
        this.themeId = themeId;
        this.difficultyId = difficultyId;
        this.questionType = questionType;
        this.question = question;
        this.optionA = optionA;
        this.optionB = optionB;
        this.optionC = optionC;
        this.optionD = optionD;
        this.correctOption = correctOption;
        this.codeSolution = codeSolution;
        this.languageName = languageName; // Inizializza languageName
    }

    // Getter e setter per languageName
    public String getLanguageName() {
        return languageName;
    }

    public void setLanguageName(String languageName) {
        this.languageName = languageName;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLanguageId() {
        return languageId;
    }

    public void setLanguageId(int languageId) {
        this.languageId = languageId;
    }

    public int getThemeId() {
        return themeId;
    }

    public void setThemeId(int themeId) {
        this.themeId = themeId;
    }

    public int getDifficultyId() {
        return difficultyId;
    }

    public void setDifficultyId(int difficultyId) {
        this.difficultyId = difficultyId;
    }

    public String getQuestionType() {
        return questionType;
    }

    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getOptionA() {
        return optionA;
    }

    public void setOptionA(String optionA) {
        this.optionA = optionA;
    }

    public String getOptionB() {
        return optionB;
    }

    public void setOptionB(String optionB) {
        this.optionB = optionB;
    }

    public String getOptionC() {
        return optionC;
    }

    public void setOptionC(String optionC) {
        this.optionC = optionC;
    }

    public String getOptionD() {
        return optionD;
    }

    public void setOptionD(String optionD) {
        this.optionD = optionD;
    }

    public String getCorrectOption() {
        return correctOption;
    }

    public void setCorrectOption(String correctOption) {
        this.correctOption = correctOption;
    }

    public String getCodeSolution() {
        return codeSolution;
    }

    public void setCodeSolution(String codeSolution) {
        this.codeSolution = codeSolution;
    }

}
