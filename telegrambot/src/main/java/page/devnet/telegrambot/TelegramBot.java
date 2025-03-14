package page.devnet.telegrambot;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClientOptions;
import lombok.Builder;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import page.devnet.pluginmanager.MessageSubscriber;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author maksim
 * @since 16.11.2019
 */
@Slf4j
class TelegramBot  {

    @Value
    @Builder
    static class Setting {
        String name;
        String token;
        String path;
    }

    private final String name;
    private final String token;
    private final MessageSubscriber<Update, List<PartialBotApiMethod<?>>> eventSubscriber;
    private final Instant startTime;
    private final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

    public TelegramBot(Setting setting, MessageSubscriber<Update, List<PartialBotApiMethod<?>>> subscriber) {
        this.name = setting.name;
        this.token = setting.token;
        this.eventSubscriber = subscriber;
        /*this.telegramSender = new TelegramSender(vertx, new TelegramSender.TelegramSenderSetting(new HttpClientOptions()
                .setDefaultHost("api.telegram.org")
                .setDefaultPort(443)
                .setSsl(true)
                .setKeepAlive(false),
                token));*/
        startTime = Instant.now();
    }


    private class DevBotManager implements LongPollingSingleThreadUpdateConsumer
    {
        private TelegramClient telegramClient = new OkHttpTelegramClient("12345:YOUR_TOKEN");

        @Override
        public void consume(Update update) {
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

        public String getBotUsername() {
            return name;
        }

        public String getBotToken() {
            return token;
        }
        /**@Override
        public void onUpdatesReceived(List<Update> updates) {
            for (var update : updates) {
                if (update.hasMessage() && isBeforeStart(update.getMessage())) {
                    log.warn("skip message: [{}], that got before starting: [start: {}, got: {}]", update.getMessage().getText(), startTime, update.getMessage().getDate());
                    continue;
                }
                CompletableFuture.supplyAsync(() -> {
                            try {
                                return eventSubscriber.consume(update);
                            } catch (Exception e) {
                                log.error(e.getMessage(), e);
                                throw e;
                            }
                        }, executor)
                        .thenAccept(st -> st.stream()
                                .flatMap(Collection::stream)
                                .forEach(telegramSender::send))
                        .exceptionally(e -> {
                            log.error("Send error to bot {}", e.getMessage());
                            var chatId = update.getMessage().getChatId();
                            telegramSender.send(new SendMessage(String.valueOf(chatId), e.toString()));
                            return null;
                        });
            }

        }

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
        }**/


    }
}
