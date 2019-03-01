package ru.sherb.bot;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * @author maksim
 * @since 01.03.19
 */
public final class TranslateBot extends TelegramLongPollingBot {

    private final String name;
    private final String token;

    public TranslateBot(String name, String token) {
        super();
        this.name = name;
        this.token = token;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return;
        }

        Message inMsg = update.getMessage();
        SendMessage answer = new SendMessage(inMsg.getChatId(), inMsg.getText());
        try {
            this.execute(answer);
        } catch (TelegramApiException e) {
            e.printStackTrace();
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
}
