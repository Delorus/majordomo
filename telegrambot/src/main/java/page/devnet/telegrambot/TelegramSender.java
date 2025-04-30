package page.devnet.telegrambot;

import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.botapimethods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendAnimation;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

/**
 * @author maksim
 * @since 31.05.2020
 */
public final class TelegramSender {
    private TelegramClient telegramClient;

    public TelegramSender(TelegramClient telegramClient) {
        this.telegramClient = telegramClient;
    }

    public void send(PartialBotApiMethod<?> message) {
        try {
            if (message instanceof SendVideo sendVideo) {
                telegramClient.execute(sendVideo); // Sending our message object to user
            } else if (message instanceof SendDocument sendDocument) {
                telegramClient.execute(sendDocument); // Sending our message object to user
            } else if (message instanceof SendPhoto sendPhoto) {
                telegramClient.execute(sendPhoto); // Sending our message object to user
            } else if (message instanceof SendAnimation sendAnimation) {
                telegramClient.execute(sendAnimation); // Sending our message object to user
            } else if (message instanceof SetWebhook setWebhook) {
                telegramClient.execute(setWebhook); // Sending our message object to user
            } else if (message instanceof BotApiMethod<?> botApiMethod){
                telegramClient.execute(botApiMethod); // Sending our message object to user
            } else {
                throw new UnsupportedOperationException("Unsupported type of message: " + message.getClass());
            }
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
