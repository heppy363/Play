package com.matteorossi.play.models;
/**
 * Modello dati per rappresentare la percentuale di completamento di un linguaggio di programmazione.
 * Utilizzato per tracciare i progressi degli utenti in base al linguaggio selezionato.
 *
 * <p>Campi principali:</p>
 * <ul>
 *   <li>Identificativo univoco del linguaggio</li>
 *   <li>Valore percentuale del completamento (0-100)</li>
 * </ul>
 *
 * @see com.matteorossi.play.database.QueryDAO#getOverallCompletionPercentage(int, int) Metodo correlato per il calcolo
 */
public class CompletionPercentageModel {
    private int languageId;
    private double completionPercentage;

    public CompletionPercentageModel(int languageId, double completionPercentage) {
        this.languageId = languageId;
        this.completionPercentage = completionPercentage;
    }

    public int getLanguageId() { return languageId; }
    public double getCompletionPercentage() { return completionPercentage; }

    @Override
    public String toString() {
        return "Language ID: " + languageId + " - Completion: " + completionPercentage + "%";
    }

}
