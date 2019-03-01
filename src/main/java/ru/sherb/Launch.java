package ru.sherb;

import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import ru.sherb.bot.TranslateBot;

public class Launch {
    public static void main(String[] args) throws TelegramApiRequestException {
        ApiContextInitializer.init();

        TelegramBotsApi api = new TelegramBotsApi();

        api.registerBot(new TranslateBot("artificial_butler_bot", "489229556:AAEecUEiFazv6c53T2vAGgvkrK8gC3SLHEc"));
    }
}
