package ru.sherb.translate;

import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.sherb.translate.yandex.TranslateServiceImpl;

import java.io.IOException;

/**
 * @author maksim
 * @since 01.03.19
 */
public final class TranslateBot extends TelegramWebhookBot {

    private final TranslateService service;

    private final String name;
    private final String token;
    private final String path;

    public TranslateBot(String name, String token, String path) {
        super();
        this.name = name;
        this.token = token;
        this.path = path;
        service = new TranslateServiceImpl("trnsl.1.1.20190302T173434Z.b9d42857e1f5463e.8fdc17ce5df7d4a0aee3a9be6ade62b96671e05f");
    }

    @Override
    public BotApiMethod onWebhookUpdateReceived(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return null;
        }

        Message inMsg = update.getMessage();
        try {
            String ru = service.transRuToEn(inMsg.getText());
            return new SendMessage(inMsg.getChatId(), ru);
        } catch (IOException e) {
            e.printStackTrace();
            return new SendMessage(inMsg.getChatId(), "Извините, произошла ошибка.");
        }
    }

    @Override
    public String getBotUsername() {
        return name;
    }

    @Override
    public String getBotToken() {
        return token;
    }

    @Override
    public String getBotPath() {
        return path;
    }
}
