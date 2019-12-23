package page.devnet.vertxtgbot;

import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.meta.generics.WebhookBot;

public abstract class TelegramWebHookBot implements WebhookBot {

    public TelegramWebHookBot(){}

    @Override
    public void setWebhook(String url, String publicCertificatePath) throws TelegramApiRequestException {

    }

}
