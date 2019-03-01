package ru.sherb;

import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import ru.sherb.bot.TranslateBot;

public class Launch {

    private static final String MAIN_TAG = "Main";

    public static void main(String[] args) throws TelegramApiRequestException {

        ApiContextInitializer.init();

        System.out.println("Port: " + System.getenv("PORT"));
        TelegramBotsApi api = new TelegramBotsApi("https://perch-tg-bots.herokuapp.com/", "http://localhost:" + System.getenv("PORT") + "/");

        System.out.println("Bot name: " + System.getenv("TG_BOT_NAME"));
        api.registerBot(new TranslateBot(System.getenv("TG_BOT_NAME"), System.getenv("TG_BOT_TOKEN"), System.getenv("TG_BOT_NAME")));
    }
}
