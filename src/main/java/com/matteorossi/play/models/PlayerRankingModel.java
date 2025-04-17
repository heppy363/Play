package com.matteorossi.play.models;


import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Modello dati per la rappresentazione di un giocatore nella classifica.
 * Utilizza JavaFX Properties per permettere il binding diretto con componenti UI
 * come TableView. Mantiene informazioni sulla posizione in classifica, username
 * e punteggio totale accumulato.
 *
 * <p>Struttura principale:</p>
 * <ul>
 *   <li>Posizione in classifica (intero)</li>
 *   <li>Username del giocatore</li>
 *   <li>Punteggio totale accumulato</li>
 * </ul>
 *
 * @see com.matteorossi.play.database.QueryDAO#getPlayerRanking() Metodo correlato per il recupero dati
 */

public class PlayerRankingModel {
    private final IntegerProperty position;
    private final StringProperty username;
    private final IntegerProperty totalScore;

    public PlayerRankingModel(int position, String username, int totalScore) {
        this.position = new SimpleIntegerProperty(position);
        this.username = new SimpleStringProperty(username);
        this.totalScore = new SimpleIntegerProperty(totalScore);
    }

    public int getPosition() {
        return position.get();
    }

    public String getUsername() {
        return username.get();
    }

    public int getTotalScore() {
        return totalScore.get();
    }

    public IntegerProperty positionProperty() {
        return position;
    }

    public StringProperty usernameProperty() {
        return username;
    }

    public IntegerProperty totalScoreProperty() {
        return totalScore;
    }
}
