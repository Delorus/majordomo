package page.devnet.vertxtgbot.tgapi;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;

/**
 * @author maksim
 * @since 31.05.2020
 */
public final class TelegramSender {

    private final WebClient httpClient;

    public TelegramSender(Vertx vertx, HttpClientOptions options) {
        this.httpClient = WebClient.create(vertx, new WebClientOptions(options));
    }

    public TelegramSender(Vertx vertx) {
        this(vertx, new HttpClientOptions().setKeepAlive(false).setSsl(true));
    }

    public void send(PartialBotApiMethod<?> message) {
        TelegramAction action;
        if (message instanceof SendVideo) {
            action = new SendVideoAction((SendVideo) message);
        } else if (message instanceof SendDocument) {
            action = new SendDocumentAction((SendDocument) message);
        } else if (message instanceof BotApiMethod<?>){
            action = new DefaultBotAction((BotApiMethod<?>) message);
        } else {
            //todo throw exceptions
            action = null;
        }

        action.execute(httpClient);
    }

    // лайфхак для собственных экшенов, которые не маппятся на классы из либы (PartialBotApiMethod)
    public void send(SetupWebhookAction setupWebhookAction) {
        setupWebhookAction.execute(httpClient);
    }
}
