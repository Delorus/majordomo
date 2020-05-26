package page.devnet.vertxbot;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import org.telegram.telegrambots.meta.ApiConstants;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.meta.generics.WebhookBot;

import java.io.IOException;

public abstract class TelegramWebHookBot extends AbsSender implements WebhookBot {

    private SetWebhook setWebhook;
    HttpClientOptions options;
    HttpClient client;
    Vertx vertx;

    public TelegramWebHookBot( ){
        super();
        //TODO Maybe need add options on client;
        this.options = new HttpClientOptions().setKeepAlive(false).setSsl(true);
        this.client = this.vertx.createHttpClient(options);

    }
    public abstract String getBotToken();

    @Override
    public final Message execute(SendPhoto sendPhoto) throws TelegramApiException {
        if (sendPhoto==null){
            throw new TelegramApiException("Param sendphoto is null");
        }
        sendPhoto.validate();
        try{
            String url = ApiConstants.BASE_URL + getBotToken() + "/";
        }catch (Exception e){
            throw new TelegramApiException("Unable to send document", e);
        }
        return null;

    }
        @Override
    public void setWebhook(String url, String publicCertificatePath) throws TelegramApiRequestException {
        setWebhook = setWebhook.setUrl(url);
    }

}
