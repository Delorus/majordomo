package page.devnet.telegrambot;

import lombok.Builder;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import page.devnet.pluginmanager.MessageSubscriber;

import java.util.List;

/**
 * @author maksim
 * @since 16.11.2019
 */
@Slf4j
class TelegramBot {

    @Value
    @Builder
    static class Setting {
        String name;
        String token;
        String path;
    }

    private final String name;
    private final String token;
    private final String path;
    private final MessageSubscriber<Update, List<PartialBotApiMethod>> eventSubscriber;

    public TelegramBot(Setting setting, MessageSubscriber<Update, List<PartialBotApiMethod>> subscriber) {
        this.name = setting.name;
        this.token = setting.token;
        this.path = setting.path;
        this.eventSubscriber = subscriber;
    }

    public TelegramWebhookBot atProductionBotManager() {
        return new ProdBotManager();
    }

    public TelegramLongPollingBot atDevBotManager() {
        return new DevBotManager();
    }

    private class ProdBotManager extends TelegramWebhookBot {

        @Override
        public BotApiMethod onWebhookUpdateReceived(Update update) {
            eventSubscriber.consume(update)
                    .forEach(this::execute);

            return null; //todo return last msg
        }

        private void execute(List<PartialBotApiMethod> response) {
            try {
                for (PartialBotApiMethod method : response) {
                    if (method instanceof BotApiMethod) {
                        execute((BotApiMethod) method);
                    } else if (method instanceof SendPhoto) {
                        execute((SendPhoto) method);
                    }
                }
            } catch (TelegramApiException e) {
                log.error(e.getMessage(), e);
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

    private class DevBotManager extends TelegramLongPollingBot {

        @Override
        public void onUpdateReceived(Update update) {
            eventSubscriber.consume(update)
                    .forEach(this::execute);
        }

        private void execute(List<PartialBotApiMethod> response) {
            try {
                for (PartialBotApiMethod method : response) {
                    if (method instanceof BotApiMethod) {
                        execute((BotApiMethod) method);
                    } else if (method instanceof SendPhoto) {
                        execute((SendPhoto) method);
                    }
                }
            } catch (TelegramApiException e) {
                log.error(e.getMessage(), e);
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
}
