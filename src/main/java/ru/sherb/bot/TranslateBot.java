package ru.sherb.bot;

import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * @author maksim
 * @since 01.03.19
 */
public final class TranslateBot extends TelegramWebhookBot {

    private final String name;
    private final String token;
    private final String path;

    public TranslateBot(String name, String token, String path) {
        super();
        this.name = name;
        this.token = token;
        this.path = path;
    }

    @Override
    public BotApiMethod onWebhookUpdateReceived(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return null;
        }

        Message inMsg = update.getMessage();
        return new SendMessage(inMsg.getChatId(), inMsg.getText());
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
