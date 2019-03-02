package ru.sherb;

import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import ru.sherb.bot.TranslateBot;

public class Launch {

    public static void main(String[] args) throws TelegramApiRequestException {
        ApiContextInitializer.init();

        TelegramBotsApi api = new TelegramBotsApi("https://perch-tg-bots.herokuapp.com/", "http://0.0.0.0:" + System.getenv("PORT") + "/");

        api.registerBot(new TranslateBot(System.getenv("TG_BOT_NAME"), System.getenv("TG_BOT_TOKEN"), System.getenv("TG_BOT_NAME")));
    }
}
