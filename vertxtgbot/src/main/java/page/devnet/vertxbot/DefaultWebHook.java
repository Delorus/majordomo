package page.devnet.vertxbot;

import io.vertx.core.Vertx;
import io.vertx.core.VertxException;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiValidationException;
import org.telegram.telegrambots.meta.generics.Webhook;
import org.telegram.telegrambots.meta.generics.WebhookBot;

import java.net.URI;

/**
 * autor Konstantin
 */
public class DefaultWebHook implements Webhook {

    private String internalUrl;
    private WebhookBot webhookBot;
    private Router router;
    private Vertx vertx;
    private static final Logger log = LogManager.getLogger(DefaultWebHook.class);

    public DefaultWebHook() throws TelegramApiRequestException {
    }
    public Vertx getVertex(){return this.vertx;}

    @Override
    public void startServer() throws TelegramApiRequestException {
        URI uri = URI.create(internalUrl);
        HttpServerOptions options = new HttpServerOptions()
                .setLogActivity(true)
                .setUseAlpn(true)
                .setSsl(true)
                .setPort(uri.getPort())
                .setHost(uri.getHost());
        vertx = Vertx.vertx();
        HttpServer server = vertx.createHttpServer(options);
        router = Router.router(vertx);
        //TODO need check to path right /BotPath
        /**
         * reading of the request body for all routes under webhookBot.getBotPath()
         */
        router.route("/" + webhookBot.getBotPath()).handler(BodyHandler.create());
        /**
         *  maps POST requests on webhookBot.getBotPath() to the method in handler
         */
        router.post("/" + webhookBot.getBotPath()).handler(routingContext -> {
            final Update update = Json.decodeValue(routingContext.getBodyAsString(),
                    Update.class);
            try {
                BotApiMethod response = webhookBot.onWebhookUpdateReceived(update);
                if (response != null) {
                    response.validate();
                }
                routingContext.response().setStatusCode(200).end(Json.encodePrettily(response));
            } catch (TelegramApiValidationException e) {
                log.error(e.getLocalizedMessage(), e);
                //TODO Check status code 204 - NO CONTENT
                routingContext.response().setStatusCode(204).end();
            }
        });
        try {
            server.requestHandler(router::accept).listen();
        } catch (VertxException e) {
            throw new TelegramApiRequestException("Error starting server", e);
        }

    }


    @Override
    public void setInternalUrl(String internalUrl) {
        this.internalUrl = internalUrl;
    }

    @Override
    public void registerWebhook(WebhookBot callback) {
        this.webhookBot = callback;
    }

    @Override
    public void setKeyStore(String keyStore, String keyStorePassword) throws TelegramApiRequestException {
    }
}
