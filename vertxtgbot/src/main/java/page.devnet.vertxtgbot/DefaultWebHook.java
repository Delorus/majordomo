package page.devnet.vertxtgbot;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.meta.generics.Webhook;
import org.telegram.telegrambots.meta.generics.WebhookBot;

import java.io.IOException;

public class DefaultWebHook implements Webhook {

    @Override
    public void startServer() throws TelegramApiRequestException {
        final Vertx vertx = Vertx.vertx();
        HttpServer server = vertx.createHttpServer();

        try {
            server.listen();
            //TODO What exception need to catch?
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void registerWebhook(WebhookBot callback) {

    }

    @Override
    public void setInternalUrl(String internalUrl) {

    }

    @Override
    public void setKeyStore(String keyStore, String keyStorePassword) throws TelegramApiRequestException {

    }
}
