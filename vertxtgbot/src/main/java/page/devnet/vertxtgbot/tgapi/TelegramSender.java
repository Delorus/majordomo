package page.devnet.vertxtgbot.tgapi;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;

import java.util.Objects;

/**
 * @author maksim
 * @since 31.05.2020
 */
public final class TelegramSender {

    public static class TelegramSenderSetting {
        private final WebClientOptions webClientOptions;
        private String botToken = "";

        public TelegramSenderSetting(HttpClientOptions other) {
            this.webClientOptions = new WebClientOptions(other);
        }

        public TelegramSenderSetting setBotToken(String botToken) {
            Objects.requireNonNull(botToken);
            this.botToken = botToken;
            return this;
        }
    }

    private final WebClient httpClient;
    private final VertxWebClientWrapper transport;

    public TelegramSender(Vertx vertx, TelegramSenderSetting options) {
        this.httpClient = WebClient.create(vertx, options.webClientOptions);
        this.transport = new VertxWebClientWrapper(httpClient, options.botToken);
    }

    public void send(PartialBotApiMethod<?> message) {
        TelegramAction action;
        if (message instanceof SendVideo) {
            action = new SendVideoAction((SendVideo) message);
        } else if (message instanceof SendDocument) {
            action = new SendDocumentAction((SendDocument) message);
        } else if (message instanceof SendPhoto) {
            action = new SendPhotoAction((SendPhoto) message);
        } else if (message instanceof BotApiMethod<?>){
            action = new DefaultBotAction((BotApiMethod<?>) message);
        } else {
            throw new UnsupportedOperationException("Unsupported type of message: " + message.getClass());
        }

        action.execute(transport);
    }

    // лайфхак для собственных экшенов, которые не маппятся на классы из либы (PartialBotApiMethod)
    public void send(SetupWebhookAction setupWebhookAction) {
        setupWebhookAction.execute(httpClient);
    }
}
