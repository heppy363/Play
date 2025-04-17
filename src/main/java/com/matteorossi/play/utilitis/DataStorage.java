package com.matteorossi.play.utilitis;

import java.util.HashMap;
import java.util.Map;

/**
 * Classe utilitaria per la gestione centralizzata di dati temporanei nell'applicazione.
 * Utilizza una mappa in-memory per memorizzare coppie chiave-valore in modo statico.
 *
 * <p>Caratteristiche principali:</p>
 * <ul>
 *   <li>Archiviazione temporanea di oggetti durante l'esecuzione</li>
 *   <li>Accesso globale tramite metodi statici</li>
 *   <li>Supporto per tipizzazione forte con generics</li>
 * </ul>
 *
 * <p>Avvertenze:</p>
 * <ul>
 *   <li>Non thread-safe - L'accesso concorrente può causare inconsistenza</li>
 *   <li>Dati volatili - I dati persistono solo durante l'esecuzione dell'applicazione</li>
 *   <li>Gestione manuale - Richiede pulizia esplicita dei dati non più necessari</li>
 * </ul>
 */

public class DataStorage {
    private static Map<String, Object> dataStorage = new HashMap<String, Object>();


    public static Boolean containsData(String key) {
        return dataStorage.containsKey(key);
    }


    public static Object getData(String key) {
        return dataStorage.get(key);
    }


    public static void setDataStorage(String key, Object value) {
        dataStorage.put(key, value);
    }


    public static <T> T getDataAs(String key, Class<T> clazz) {
        Object value = dataStorage.get(key);
        if (clazz.isInstance(value)) {
            return clazz.cast(value);
        }
        return null;
    }
}
