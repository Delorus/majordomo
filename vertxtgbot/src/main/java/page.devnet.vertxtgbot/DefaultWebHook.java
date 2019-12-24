package page.devnet.vertxtgbot;

import io.vertx.core.Vertx;
import io.vertx.core.VertxException;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.meta.generics.Webhook;
import org.telegram.telegrambots.meta.generics.WebhookBot;

import java.net.URI;

/**
 * autor Konstantin
 */
public class DefaultWebHook  implements Webhook {

    private String internalUrl;
    private WebhookBot webhookBot;

    public DefaultWebHook() throws TelegramApiRequestException{
    }

    @Override
    public void startServer() throws TelegramApiRequestException {
        URI uri = URI.create(internalUrl);
        HttpServerOptions options = new HttpServerOptions()
                .setLogActivity(true)
                .setUseAlpn(true)
                .setSsl(true)
                .setPort(uri.getPort())
                .setHost(uri.getHost());

        final Vertx vertx = Vertx.vertx();
        HttpServer server = vertx.createHttpServer(options);
        try {
            server.listen();
        }catch (VertxException e){
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
