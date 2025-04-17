package com.matteorossi.play.database;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Classe per la gestione della connessione al database SQLite.
 * Fornisce un metodo statico per ottenere una connessione al database,
 * creando automaticamente il file e le directory necessari se non esistenti.
 *
 * <p>Caratteristiche principali:</p>
 * <ul>
 *   <li>Creazione automatica del file database se assente</li>
 *   <li>Gestione centralizzata della configurazione di connessione</li>
 *   <li>Supporto per il percorso relativo del database</li>
 * </ul>
 *
 * @see DriverManager Classe JDBC per la gestione delle connessioni
 * @see Connection Interfaccia per le operazioni sul database
 */

public class DatabaseConnection {
    public static Connection getConnection() throws SQLException {
        String userHome = System.getProperty("user.home");
        String appDir = userHome + "/.play-app/";
        String dbPath = appDir + "database.db";

        try {
            new File(appDir).mkdirs();

            if (!new File(dbPath).exists()) {
                // Usa getResourceAsStream per accedere al file dentro il JAR
                try (InputStream is = DatabaseConnection.class.getResourceAsStream("/data/database.db");
                     OutputStream os = new FileOutputStream(dbPath)) {

                    if (is == null) {
                        throw new SQLException("Database non trovato nel JAR!");
                    }

                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = is.read(buffer)) > 0) {
                        os.write(buffer, 0, length);
                    }
                }
            }
        } catch (IOException e) {
            throw new SQLException("Errore copia database: " + e.getMessage(), e);
        }

        return DriverManager.getConnection("jdbc:sqlite:" + dbPath);
    }
}
