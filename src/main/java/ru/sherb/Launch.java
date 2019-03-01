package ru.sherb;

import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.meta.logging.BotLogger;
import ru.sherb.bot.TranslateBot;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;

public class Launch {

    private static final String MAIN_TAG = "Main";

    public static void main(String[] args) throws TelegramApiRequestException {
        BotLogger.setLevel(Level.ALL);
        BotLogger.registerLogger(new ConsoleHandler());

        ApiContextInitializer.init();

        BotLogger.debug(MAIN_TAG, "get port: " + args[0]);
        TelegramBotsApi api = new TelegramBotsApi("https://perch-tg-bots.herokuapp.com", "localhost:" + args[0]);

        BotLogger.debug(MAIN_TAG, "get bot name: " + System.getenv("TG_BOT_NAME"));
        api.registerBot(new TranslateBot(System.getenv("TG_BOT_NAME"), System.getenv("TG_BOT_TOKEN"), System.getenv("TG_BOT_NAME")));
    }
}
