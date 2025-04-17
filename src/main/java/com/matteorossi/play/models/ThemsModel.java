package com.matteorossi.play.models;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Modello dati per la rappresentazione di un tema/categoria di domande.
 * Utilizza JavaFX Properties per il binding con l'interfaccia utente e tiene
 * traccia dello stato di selezione del tema.
 *
 * <p>Struttura principale:</p>
 * <ul>
 *   <li>ID univoco del tema</li>
 *   <li>Nome del tema (in due formati: Property e classico)</li>
 *   <li>Stato di selezione per l'interazione UI</li>
 * </ul>
 *
 * @see javafx.beans.property.StringProperty Per il binding del nome
 * @see javafx.beans.property.BooleanProperty Per lo stato di selezione
 */

public class ThemsModel {
    private int id;
    private StringProperty themsNameProperty;
    private BooleanProperty select;
    private String name;

    public ThemsModel(int id, String name) {
        this.id = id;
        this.themsNameProperty = new SimpleStringProperty(name);
        this.select = new SimpleBooleanProperty(false);
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ThemsModel(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getThemsNameProperty() {
        return themsNameProperty.get();
    }

    public void setThemsNameProperty(String name) {
        this.themsNameProperty.set(name);
    }

    public StringProperty themsNamePropertyProperty() {
        return themsNameProperty;
    }

    public void setSelected(boolean selected) {
        this.select.set(selected);
    }

    public BooleanProperty selectedProperty() {
        return select;
    }
}
