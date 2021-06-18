package page.devnet.vertxtgbot.tgapi;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendAnimation;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;

/**
 * @author maksim
 * @since 31.05.2020
 */
public final class TelegramSender {

    public static class TelegramSenderSetting {
        private final WebClientOptions webClientOptions;
        private final String botToken;

        public TelegramSenderSetting(HttpClientOptions other, String botToken) {
            this.webClientOptions = new WebClientOptions(other);
            this.botToken = botToken;
        }
    }

    private final VertxWebClientWrapper transport;

    public TelegramSender(Vertx vertx, TelegramSenderSetting options) {
        WebClient httpClient = WebClient.create(vertx, options.webClientOptions);
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
        } else if (message instanceof SendAnimation) {
            action = new SendAnimationAction((SendAnimation) message);
        } else if (message instanceof SendVideo) {
            action = new SendVideoAction((SendVideo) message);
        } else if (message instanceof SetWebhook) {
            action = new SetupWebhookAction((SetWebhook) message);
        } else if (message instanceof BotApiMethod<?>){
            action = new DefaultBotAction((BotApiMethod<?>) message);
        } else {
            throw new UnsupportedOperationException("Unsupported type of message: " + message.getClass());
        }

        action.execute(transport);
    }
}
