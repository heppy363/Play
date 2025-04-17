package com.matteorossi.play;

import com.matteorossi.play.database.ClaseDLLDAO;
import com.matteorossi.play.telegram.TelegramBoot;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Classe principale dell'applicazione Play per l'apprendimento della programmazione.
 * Responsabile dell'avvio dell'applicazione e dell'inizializzazione dei componenti chiave.
 *
 * <p>Funzionalità principali:</p>
 * <ul>
 *   <li>Avvio dell'interfaccia grafica JavaFX</li>
 *   <li>Inizializzazione del database</li>
 *   <li>Configurazione del bot Telegram</li>
 *   <li>Gestione dell'uscita dall'applicazione</li>
 * </ul>
 *
 * @see Application Classe base JavaFX per le applicazioni GUI
 * @see ClaseDLLDAO Gestione dello schema del database
 * @see TelegramBoot Integrazione con Telegram
 */

public class Main extends Application{
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/com/matteorossi/play/view/intruduction.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 900, 500);
        stage.setTitle("Play");
        stage.setScene(scene);

        //Termino tutto il programma
        stage.setOnCloseRequest(event -> {
            System.exit(0);
        });

        stage.show();
    }


    public static void main(String[] args){
        //Fa partire prima il Boot no new Threed
        TelegramBoot.initBoot();


        //Eliminaizone delle tabelle
        //ClaseDLLDAO.deleteAllTables(); //DA NON DECCOMENTARE o SI PERDONO TUTTI I DATI
        //Creazione delle tabelle
        ClaseDLLDAO.generateDB();

        //Mesaggio di prova per corretto funzionamento
        TelegramBoot.sendMessageOnce(1111048197, "Sto funzionando !!!");


        //Salvo i dati anche in caso di chiusara del utente
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
           System.out.println("Shutting down...");

        }));

        //fa partire JavaFX
        launch();
    }

}
