package page.devnet.vertxtgbot;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.net.JksOptions;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.meta.generics.Webhook;
import org.telegram.telegrambots.meta.generics.WebhookBot;

import java.io.IOException;

public class DefaultWebHook implements Webhook {

    private String internalUrl;


    public DefaultWebHook() throws TelegramApiRequestException{

    }

    @Override
    public void startServer() throws TelegramApiRequestException {
        HttpServerOptions options = new HttpServerOptions()
                .setLogActivity(true)
                .setUseAlpn(true)
                .setSsl(true)
                .setKeyStoreOptions(new JksOptions().setPath("/path/to/my/keystore"));

        final Vertx vertx = Vertx.vertx();

        HttpServer server = vertx.createHttpServer(options);

        try {
            server.listen();
            //TODO What exception need to catch?
        }catch (Exception e){
            throw new TelegramApiRequestException("Error starting server", e);
        }
    }

    @Override
    public void registerWebhook(WebhookBot callback) {

    }

    @Override
    public void setInternalUrl(String internalUrl) {
        this.internalUrl = internalUrl;
    }

    @Override
    public void setKeyStore(String keyStore, String keyStorePassword) throws TelegramApiRequestException {

    }
}
