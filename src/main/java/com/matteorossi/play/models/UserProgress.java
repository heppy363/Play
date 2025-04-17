package com.matteorossi.play.models;

/**
 * Rappresenta i progressi di un utente in una specifica categoria di apprendimento.
 * Tiene traccia della percentuale di completamento per la categoria specificata.
 */

public class UserProgress {
    private int userId;
    private String category;
    private double completionPercentage;

    public UserProgress(int userId, String category, double completionPercentage) {
        this.userId = userId;
        this.category = category;
        this.completionPercentage = completionPercentage;
    }

    public int getUserId() {
        return userId;
    }

    public String getCategory() {
        return category;
    }

    public double getCompletionPercentage() {
        return completionPercentage;
    }


}
