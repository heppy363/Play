package com.matteorossi.play.telegram;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.BotSession;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class TelegramBoot extends TelegramLongPollingBot {

    private static final Logger logger = LoggerFactory.getLogger(TelegramBoot.class);
    private static TelegramBoot botInstance;
    private static final String COMMAND_RESET = "/reset";
    private static final String COMMAND_ID = "/id";
    private static final String COMMAND_HELP = "/help";
    private static final String HELP_MESSAGE = "📋 *Comandi disponibili:*\n" +
            COMMAND_ID + " - Ottieni il tuo ID Telegram\n" +
            COMMAND_RESET + " - Richiedi il reset della password\n" +
            COMMAND_HELP + " - Mostra questo messaggio";
    private static final String RESET_MESSAGE = "✅ L'amministratore ha ricevuto la tua richiesta.\n🔐 La tua password verrà resettata a breve.";

    // Configurazione
    private static final Properties config = new Properties();
    private static final String CONFIG_DIR = System.getProperty("user.home") + "/.play-app/";
    private static final String CONFIG_FILE = "telegram.properties";

    static {
        loadConfig();
    }

    private static void loadConfig() {
        try {
            Path configPath = Paths.get(CONFIG_DIR, CONFIG_FILE);

            // Crea directory se non esiste
            Files.createDirectories(configPath.getParent());

            // Se il file non esiste, copia da risorse interne
            if (!Files.exists(configPath)) {
                try (InputStream is = TelegramBoot.class.getResourceAsStream("/data/data.txt")) {
                    if (is != null) {
                        Files.copy(is, configPath, StandardCopyOption.REPLACE_EXISTING);
                        logger.info("File di configurazione creato in: {}", configPath);
                    } else {
                        createDefaultConfig(configPath);
                    }
                }
            }

            // Carica la configurazione
            loadConfiguration(configPath);

        } catch (IOException e) {
            logger.error("Errore nel caricamento della configurazione", e);
            throw new RuntimeException("Configurazione Telegram mancante");
        }
    }

    private static void createDefaultConfig(Path configPath) throws IOException {
        Files.createFile(configPath);
        try (OutputStream output = Files.newOutputStream(configPath)) {
            String defaultConfig = "# Inserisci il token del bot Telegram\ntoken = il_tuo_token_qui";
            output.write(defaultConfig.getBytes());
            logger.warn("Creato file di configurazione vuoto in: {}", configPath);
        }
    }

    private static void loadConfiguration(Path configPath) throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(configPath)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("token =")) {
                    config.put("TELEGRAM_BOT_TOKEN", line.split("=")[1].trim());
                }
            }
        }

        // Verifica che il token sia valido
        if (config.getProperty("TELEGRAM_BOT_TOKEN") == null ||
                config.getProperty("TELEGRAM_BOT_TOKEN").isEmpty()) {
            throw new IOException("Token non trovato nel file di configurazione");
        }
    }

    @Override
    public String getBotUsername() {
        return "PlayBoot";
    }

    @Override
    public String getBotToken() {
        return config.getProperty("TELEGRAM_BOT_TOKEN");
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            logger.info("Comando ricevuto: {} da: {}", messageText, chatId);

            switch (messageText) {
                case COMMAND_ID:
                    sendSimpleMessage(chatId, "Il tuo chat ID è: " + chatId);
                    break;

                case COMMAND_RESET:
                    sendSimpleMessage(chatId, RESET_MESSAGE);
                    logger.info("Richiesta di reset password da: {}", chatId);
                    break;

                case COMMAND_HELP:
                    sendSimpleMessage(chatId, HELP_MESSAGE);
                    break;

                default:
                    sendSimpleMessage(chatId, "❓ Comando non riconosciuto. Scrivi " + COMMAND_HELP + " per vedere i comandi disponibili.");
            }
        }
    }

    private void registerBotCommands() {
        List<BotCommand> commandList = new ArrayList<>();
        commandList.add(new BotCommand(COMMAND_ID, "Ottieni il tuo ID Telegram"));
        commandList.add(new BotCommand(COMMAND_RESET, "Richiedi il reset della password"));
        commandList.add(new BotCommand(COMMAND_HELP, "Mostra i comandi disponibili"));

        try {
            this.execute(new SetMyCommands(commandList, new BotCommandScopeDefault(), null));
            logger.info("Comandi del bot registrati con successo.");
        } catch (TelegramApiException e) {
            logger.error("Errore nella registrazione dei comandi: {}", e.getMessage());
        }
    }

    private void sendSimpleMessage(long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);
        message.enableMarkdown(true);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            logger.error("Errore durante l'invio del messaggio: {}", e.getMessage());
        }
    }

    public static void initBoot() {
        try {
            if (botInstance == null) {
                botInstance = new TelegramBoot();
            }

            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(botInstance);
            botInstance.registerBotCommands();
            logger.info("Bot avviato correttamente");

        } catch (TelegramApiException e) {
            logger.error("Errore critico nell'avvio del bot: {}", e.getMessage());
        }
    }

    public static void sendMessageOnce(long chatId, String messageText) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(messageText);

        try {
            if (botInstance == null) {
                botInstance = new TelegramBoot();
            }
            botInstance.execute(message);
        } catch (TelegramApiException e) {
            logger.error("Errore nell'invio del messaggio: {}", e.getMessage());
        }
    }
}