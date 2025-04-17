package com.matteorossi.play.models;

/**
 * Modello dati per la rappresentazione di una categoria di domande/argomenti.
 * Utilizzato per organizzare le domande per aree tematiche nell'applicazione.
 *
 * <p>Responsabilità principali:</p>
 * <ul>
 *   <li>Identificazione univoca di categorie tematiche</li>
 *   <li>Strutturazione gerarchica dei contenuti</li>
 * </ul>
 *
 * @see com.matteorossi.play.database.QueryDAO#getCategories() Metodo correlato per il recupero dal database
 */

public class CategoryModel {
    private String categoryName;

    public CategoryModel(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}
