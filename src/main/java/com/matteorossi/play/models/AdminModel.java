package com.matteorossi.play.models;

/**
 * Modello dati per la rappresentazione di un amministratore dell'applicazione.
 * Contiene le informazioni essenziali per l'autenticazione e l'identificazione degli amministratori.
 *
 * <p>Campi principali:</p>
 * <ul>
 *   <li>Credenziali di accesso (username/password)</li>
 *   <li>Dati anagrafici (nome e cognome)</li>
 * </ul>
 *
 * @see com.matteorossi.play.database.QueryDAO Per le operazioni di persistenza
 * @see com.matteorossi.play.controllers.AdminController Per la gestione UI
 */

public class AdminModel {

    private String username;
    private String password;
    private String firstName;
    private String lastName;

    public AdminModel(String username, String password, String firstName, String lastName) {
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
    }




    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}

