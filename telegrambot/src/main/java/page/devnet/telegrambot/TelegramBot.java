package page.devnet.telegrambot;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClientOptions;
import lombok.Builder;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.meta.generics.BotOptions;
import org.telegram.telegrambots.meta.generics.LongPollingBot;
import org.telegram.telegrambots.meta.generics.WebhookBot;
import page.devnet.pluginmanager.MessageSubscriber;
import page.devnet.vertxtgbot.tgapi.TelegramSender;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Collection;
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
    private final MessageSubscriber<Update, List<PartialBotApiMethod<?>>> eventSubscriber;
    private final TelegramSender telegramSender;
    private final Instant startTime;

    public TelegramBot(Vertx vertx, Setting setting, MessageSubscriber<Update, List<PartialBotApiMethod<?>>> subscriber) {
        this.name = setting.name;
        this.token = setting.token;
        this.path = setting.path;
        this.eventSubscriber = subscriber;
        this.telegramSender = new TelegramSender(vertx, new TelegramSender.TelegramSenderSetting(new HttpClientOptions()
                .setDefaultHost("api.telegram.org")
                .setDefaultPort(443)
                .setSsl(true)
                .setKeepAlive(false),
                token));
        startTime = Instant.now();
    }

    public WebhookBot atProductionBotManager() {
        return new ProdBotManager();
    }

    public LongPollingBot atDevBotManager() {
        return new DevBotManager();
    }

    private class ProdBotManager implements WebhookBot {

        @Override
        public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
            if (update.hasMessage() && isBeforeStart(update.getMessage())) {
                log.warn("skip message: [{}], that got before starting: [start: {}, got: {}]", update.getMessage().getText(), startTime, Instant.ofEpochMilli(update.getMessage().getDate()).atZone(ZoneOffset.UTC));
                return null;
            }

            try {
                eventSubscriber.consume(update).stream()
                        .flatMap(Collection::stream)
                        .forEach(telegramSender::send);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                var chatId = update.getMessage().getChatId();
                telegramSender.send(new SendMessage(String.valueOf(chatId), e.toString()));
            }

            return null;
        }

        @Override
        public void setWebhook(SetWebhook setWebhook) {
            telegramSender.send(setWebhook);
        }

        private boolean isBeforeStart(Message message) {
            return Instant.ofEpochSecond(message.getDate()).isBefore(startTime);
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

    private class DevBotManager implements LongPollingBot {

        @Override
        public void onUpdateReceived(Update update) {
            if (update.hasMessage() && isBeforeStart(update.getMessage())) {
                log.warn("skip message: [{}], that got before starting: [start: {}, got: {}]", update.getMessage().getText(), startTime, update.getMessage().getDate());
                return;
            }

            try {
                eventSubscriber.consume(update).stream()
                        .flatMap(Collection::stream)
                        .forEach(telegramSender::send);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                var chatId = update.getMessage().getChatId();
                telegramSender.send(new SendMessage(String.valueOf(chatId), e.toString()));
            }
        }

        private boolean isBeforeStart(Message message) {
            return Instant.ofEpochSecond(message.getDate()).isBefore(startTime);
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
        public BotOptions getOptions() {
            return null;
        }

        @Override
        public void clearWebhook() throws TelegramApiRequestException {
            //no webhook on long pooling bot
        }
    }
}
