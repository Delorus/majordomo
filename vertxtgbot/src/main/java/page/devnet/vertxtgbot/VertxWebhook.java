package page.devnet.vertxtgbot;

import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.core.net.JksOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.meta.generics.Webhook;
import org.telegram.telegrambots.meta.generics.WebhookBot;

import java.io.File;
import java.net.URI;

/**
 * autor Konstantin
 */
@Slf4j
public class VertxWebhook implements Webhook {

    private final Vertx vertx;
    private final Router router;

    private String keystoreServerFile;
    private String keystoreServerPwd;
    private String internalUrl;

    public VertxWebhook() {
        this.vertx = GlobalVertxHolder.getVertx();

        router = Router.router(vertx);
    }

    @Override
    public void setInternalUrl(String internalUrl) {
        this.internalUrl = internalUrl;
    }

    @Override
    public void setKeyStore(String keyStore, String keyStorePassword) throws TelegramApiRequestException {
        this.keystoreServerFile = keyStore;
        this.keystoreServerPwd = keyStorePassword;
        validateServerKeystoreFile(keyStore);
    }

    @Override
    public void registerWebhook(WebhookBot callback) {
        log.info("Register route on path: /{}", callback.getBotPath());
        router.route("/callback/" + callback.getBotPath()).handler(BodyHandler.create());
        router.route("/callback/" + callback.getBotPath()).handler(createHandler(callback));
    }

    private Handler<RoutingContext> createHandler(WebhookBot callback) {
        return ctx -> {
            try {
                log.debug("Got new update: {}", ctx.getBodyAsString());
                Update update = ctx.getBodyAsJson().mapTo(Update.class);
                BotApiMethod<?> response = callback.onWebhookUpdateReceived(update);
                HttpServerResponse resp = ctx.response()
                        .putHeader(HttpHeaders.CONTENT_TYPE, "application/json");

                if (response != null) {
                    response.validate();
                    resp.end(Json.encodeToBuffer(response));
                } else {
                    resp.end();
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                ctx.response().setStatusCode(500).end();
            }
        };
    }

    @Override
    public void startServer() {
        URI uri = URI.create(internalUrl);
        HttpServerOptions options = new HttpServerOptions()
                .setPort(uri.getPort())
                .setHost(uri.getHost());

        if (log.isDebugEnabled()) {
            options.setLogActivity(true);
        }

        if (keystoreServerFile != null && keystoreServerPwd != null) {
            options.setSsl(true);
            options.setKeyStoreOptions(new JksOptions()
                    .setPath(keystoreServerFile)
                    .setPassword(keystoreServerPwd));
        }

        vertx.createHttpServer(options)
                .requestHandler(router)
                .listen();
    }

    private static void validateServerKeystoreFile(String keyStore) throws TelegramApiRequestException {
        File file = new File(keyStore);
        if (!file.exists() || !file.canRead()) {
            throw new TelegramApiRequestException("Can't find or access server keystore file.");
        }
    }
}
