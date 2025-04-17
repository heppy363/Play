package com.matteorossi.play.models;

/**
 * Rappresenta un modello utente con informazioni di autenticazione e stato di reset password.
 * La classe fornisce metodi per accedere e modificare le proprietà dell'utente.
 */

public class UserModel {
    private String username;
    private String telegramId;
    private String password;
    private Boolean isReset;

    public UserModel(String username, String telegramId) {
        this.username = username;
        this.telegramId = telegramId;
    }

    public UserModel(String username, String telegramId, String password, Boolean isReset) {;
        this.username = username;
        this.telegramId = telegramId;
        this.password = password;
        this.isReset = isReset;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTelegramId() {
        return telegramId;
    }


    public void setTelegramId(String telegramId) {
        this.telegramId = telegramId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getIsReset() { return isReset != null ? isReset : false ; }
    public void setIsReset(Boolean isReset) { this.isReset = isReset; }

}
