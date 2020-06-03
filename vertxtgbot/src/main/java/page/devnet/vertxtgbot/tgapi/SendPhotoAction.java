package page.devnet.vertxtgbot.tgapi;

import io.vertx.core.json.Json;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.multipart.MultipartForm;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.exceptions.TelegramApiValidationException;

/**
 * @author maksim
 * @since 03.06.2020
 */
final class SendPhotoAction implements TelegramAction {
    
    private final SendPhoto sendPhoto;
    
    SendPhotoAction(SendPhoto sendPhoto) {
        assert sendPhoto != null;

        this.sendPhoto = sendPhoto;
    }

    @Override
    public void execute(WebClient transport) {
        try {
            sendPhoto.validate();
        } catch (TelegramApiValidationException e) {
            throw new TelegramActionException(e);
        }
        
        String url = SendPhoto.PATH;

        MultipartForm form = MultipartForm.create()
                .attribute(SendPhoto.CHATID_FIELD, sendPhoto.getChatId());

        InputFileHelper.addTextFileToForm(form, sendPhoto.getPhoto(), SendPhoto.PHOTO_FIELD, true);

        if (sendPhoto.getReplyMarkup() != null) {
                form.attribute(SendPhoto.REPLYMARKUP_FIELD, Json.encode(sendPhoto.getReplyMarkup()));
            }
            if (sendPhoto.getReplyToMessageId() != null) {
                form.attribute(SendPhoto.REPLYTOMESSAGEID_FIELD, sendPhoto.getReplyToMessageId().toString());
            }
            if (sendPhoto.getCaption() != null) {
                form.attribute(SendPhoto.CAPTION_FIELD, sendPhoto.getCaption());
                if (sendPhoto.getParseMode() != null) {
                    form.attribute(SendPhoto.PARSEMODE_FIELD, sendPhoto.getParseMode());
                }
            }
            if (sendPhoto.getDisableNotification() != null) {
                form.attribute(SendPhoto.DISABLENOTIFICATION_FIELD, sendPhoto.getDisableNotification().toString());
            }

        transport.post(url).sendMultipartForm(form, resp -> {
            if (resp.failed()) {
                throw new TelegramActionException("Failed to send photo", resp.cause());
            }
        });
    }
}
