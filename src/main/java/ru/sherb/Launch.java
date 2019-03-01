package ru.sherb;

import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import ru.sherb.bot.TranslateBot;

import java.text.MessageFormat;

public class Launch {

    public static void main(String[] args) throws TelegramApiRequestException {
        System.out.println("START APPLICATION");
        System.out.println(System.getenv());
        ApiContextInitializer.init();

        System.out.println("Port: " + System.getenv("PORT"));
        TelegramBotsApi api = new TelegramBotsApi("https://perch-tg-bots.herokuapp.com/", "http://localhost:" + System.getenv("PORT") + "/");

        System.out.println("Bot name: " + System.getenv("TG_BOT_NAME"));
        System.out.println("Bot token: " + System.getenv("TG_BOT_TOKEN"));
        System.out.println("Bot url: " + MessageFormat.format("{0}callback/", "https://perch-tg-bots.herokuapp.com/") + System.getenv("TG_BOT_NAME"));
        api.registerBot(new TranslateBot(System.getenv("TG_BOT_NAME"), System.getenv("TG_BOT_TOKEN"), System.getenv("TG_BOT_NAME")));
    }
}
