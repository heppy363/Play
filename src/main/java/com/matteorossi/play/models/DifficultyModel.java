package com.matteorossi.play.models;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Modello dati per la rappresentazione dei livelli di difficoltà delle domande.
 * Utilizza JavaFX Properties per il binding con l'interfaccia utente.
 *
 * <p>Struttura principale:</p>
 * <ul>
 *   <li>ID univoco della difficoltà</li>
 *   <li>Nome descrittivo del livello</li>
 *   <li>Stato di selezione per l'interazione UI</li>
 * </ul>
 *
 * @see javafx.beans.property.BooleanProperty Gestione stato selezione
 * @see javafx.beans.property.StringProperty Binding nome della difficoltà
 */

public class DifficultyModel {
    private int id;
    private int difficultsLevel;
    private final StringProperty difficultyNameProperty;
    private final BooleanProperty select;

    // Costruttore principale
    public DifficultyModel(int id, String name) {
        this.id = id;
        this.difficultyNameProperty = new SimpleStringProperty(name);
        this.select = new SimpleBooleanProperty(false);
    }

    // Costruttore alternativo: ora chiama il primo costruttore
    public DifficultyModel(String difficulty, int id) {
        this(id, difficulty);
    }


    // Getter e Setter corretti
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDifficulty() {
        return difficultyNameProperty.get();
    }

    public void setDifficulty(String difficulty) {
        this.difficultyNameProperty.set(difficulty);
    }

    // Fixed the method name here
    public String getDifficultyNameProperty() {
        return difficultyNameProperty.get();
    }

    public boolean isSelect() {
        return select.get();
    }

    public void setSelect(boolean select) {
        this.select.set(select);
    }

    public BooleanProperty selectProperty() {
        return select;
    }
}
