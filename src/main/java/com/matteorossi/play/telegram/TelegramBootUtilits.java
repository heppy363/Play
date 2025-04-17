package com.matteorossi.play.telegram;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.updates.DeleteWebhook;
import org.telegram.telegrambots.meta.api.methods.updates.GetWebhookInfo;
import org.telegram.telegrambots.meta.api.objects.WebhookInfo;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * Classe di utilità per la gestione avanzata dei webhook del bot Telegram.
 * Fornisce funzionalità per la cancellazione forzata dei webhook esistenti
 * e la preparazione per la modalità long polling.
 *
 * <p>Utilizzata principalmente durante l'inizializzazione del bot per garantire
 * la corretta configurazione della connessione.</p>
 *
 * @see TelegramLongPollingBot Classe base per i bot in modalità long polling
 */

public class TelegramBootUtilits {
    public static void forceWebhookDeletion(TelegramLongPollingBot bot) {
        try {
            // Verifica lo stato del webhook
            GetWebhookInfo getWebhookInfo = new GetWebhookInfo();
            WebhookInfo webhookInfo = bot.execute(getWebhookInfo);

            if(webhookInfo.getUrl() != null && !webhookInfo.getUrl().isEmpty()) {
                // Forza la cancellazione
                DeleteWebhook deleteWebhook = new DeleteWebhook();
                deleteWebhook.setDropPendingUpdates(true);
                bot.execute(deleteWebhook);

                // Attendi 1 secondo per la propagazione
                Thread.sleep(1000);
                System.out.println("Webhook cancellato forzatamente");
            }
        } catch (TelegramApiException | InterruptedException e) {
            System.err.println("Errore cancellazione webhook: " + e.getMessage());
        }
    }
}