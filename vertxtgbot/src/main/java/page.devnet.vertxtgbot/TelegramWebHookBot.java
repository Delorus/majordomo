package page.devnet.vertxtgbot;

import io.vertx.core.Vertx;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.meta.generics.WebhookBot;

public abstract class TelegramWebHookBot extends AbsSender implements WebhookBot {

    private SetWebhook setWebhook;
    public TelegramWebHookBot( ){

    }

    @Override
    public void setWebhook(String url, String publicCertificatePath) throws TelegramApiRequestException {
        setWebhook = setWebhook.setUrl(url);
    }

}
