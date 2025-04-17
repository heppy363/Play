package com.matteorossi.play.models;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.awt.*;

/**
 * Modello dati per la rappresentazione di un linguaggio di programmazione.
 * Utilizza JavaFX Properties per l'integrazione con l'interfaccia utente.
 *
 * <p>Struttura principale:</p>
 * <ul>
 *   <li>ID univoco del linguaggio</li>
 *   <li>Nome del linguaggio</li>
 *   <li>Stato di selezione per l'interfaccia</li>
 * </ul>
 *
 * @see javafx.beans.property.StringProperty Per il binding del nome
 * @see java.awt.Checkbox Componente UI per la selezione (da rivalutare)
 */

public class LanguagesModel {
    private int id;
    private String languageName;
    private StringProperty languageNameProperty;
    private Checkbox select;

    public LanguagesModel(int id, String name) {
        this.id = id;
        this.languageNameProperty = new SimpleStringProperty(name);
        this.select = new Checkbox();
    }

    public LanguagesModel(String languageName) {
        this.languageName = languageName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLanguageNameProperty() {
        return languageNameProperty.get();
    }

    public StringProperty languageNamePropertyProperty() {
        return languageNameProperty;
    }

    public void setLanguageNameProperty(String languageNameProperty) {
        this.languageNameProperty.set(languageNameProperty);
    }

    public void setSelect(Checkbox select) {
        this.select = select;
    }



    public String getLanguageName() {
        return languageName;
    }

    public void setLanguageName(String languageName) {
        this.languageName = languageName;
    }

    public Checkbox getSelect() {
        return select;
    }

}

