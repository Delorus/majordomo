package page.devnet.vertxtgbot;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import org.telegram.telegrambots.meta.ApiConstants;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.groupadministration.SetChatPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendAnimation;
import org.telegram.telegrambots.meta.api.methods.send.SendAudio;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendSticker;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.methods.send.SendVideoNote;
import org.telegram.telegrambots.meta.api.methods.send.SendVoice;
import org.telegram.telegrambots.meta.api.methods.stickers.AddStickerToSet;
import org.telegram.telegrambots.meta.api.methods.stickers.CreateNewStickerSet;
import org.telegram.telegrambots.meta.api.methods.stickers.SetStickerSetThumb;
import org.telegram.telegrambots.meta.api.methods.stickers.UploadStickerFile;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageMedia;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.meta.generics.WebhookBot;
import org.telegram.telegrambots.meta.updateshandlers.SentCallback;

import java.io.Serializable;
import java.util.List;

public abstract class VertxTelegramWebhookBot extends AbsSender implements WebhookBot {

    private SetWebhook setWebhook;
    private final HttpClientOptions options;
    private final HttpClient client;
    private final Vertx vertx;

    public VertxTelegramWebhookBot() {
        this.vertx = GlobalVertxHolder.getVertx();

        this.options = new HttpClientOptions().setKeepAlive(false).setSsl(true);
        this.client = vertx.createHttpClient(options);

    }

    @Override
    public final Message execute(SendPhoto sendPhoto) throws TelegramApiException {
        if (sendPhoto == null) {
            throw new TelegramApiException("Param sendphoto is null");
        }
        sendPhoto.validate();
        try {
            String url = ApiConstants.BASE_URL + getBotToken() + "/";
        } catch (Exception e) {
            throw new TelegramApiException("Unable to send document", e);
        }
        return null;

    }

    @Override
    public Message execute(SendVideo sendVideo) throws TelegramApiException {
        return null;
    }

    @Override
    public Message execute(SendVideoNote sendVideoNote) throws TelegramApiException {
        return null;
    }

    @Override
    public Message execute(SendSticker sendSticker) throws TelegramApiException {
        return null;
    }

    @Override
    public Message execute(SendAudio sendAudio) throws TelegramApiException {
        return null;
    }

    @Override
    public Message execute(SendVoice sendVoice) throws TelegramApiException {
        return null;
    }

    @Override
    public List<Message> execute(SendMediaGroup sendMediaGroup) throws TelegramApiException {
        return null;
    }

    @Override
    public Boolean execute(SetChatPhoto setChatPhoto) throws TelegramApiException {
        return null;
    }

    @Override
    public Boolean execute(AddStickerToSet addStickerToSet) throws TelegramApiException {
        return null;
    }

    @Override
    public Boolean execute(SetStickerSetThumb setStickerSetThumb) throws TelegramApiException {
        return null;
    }

    @Override
    public Boolean execute(CreateNewStickerSet createNewStickerSet) throws TelegramApiException {
        return null;
    }

    @Override
    public File execute(UploadStickerFile uploadStickerFile) throws TelegramApiException {
        return null;
    }

    @Override
    public Serializable execute(EditMessageMedia editMessageMedia) throws TelegramApiException {
        return null;
    }

    @Override
    public Message execute(SendAnimation sendAnimation) throws TelegramApiException {
        return null;
    }

    @Override
    protected <T extends Serializable, Method extends BotApiMethod<T>, Callback extends SentCallback<T>> void sendApiMethodAsync(Method method, Callback callback) {

    }

    @Override
    protected <T extends Serializable, Method extends BotApiMethod<T>> T sendApiMethod(Method method) throws TelegramApiException {
        return null;
    }

    @Override
    public void setWebhook(String url, String publicCertificatePath) throws TelegramApiRequestException {
        setWebhook = setWebhook.setUrl(url);
    }

    @Override
    public String getBotPath() {
        return null;
    }


}
