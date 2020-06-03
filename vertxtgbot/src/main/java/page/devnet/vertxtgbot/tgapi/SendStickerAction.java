package page.devnet.vertxtgbot.tgapi;

import io.vertx.core.json.Json;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.multipart.MultipartForm;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendSticker;
import org.telegram.telegrambots.meta.exceptions.TelegramApiValidationException;

/**
 * @author maksim
 * @since 03.06.2020
 */
final class SendStickerAction implements TelegramAction {

    private final SendSticker sendSticker;

    SendStickerAction(SendSticker sendSticker) {
        assert sendSticker != null;

        this.sendSticker = sendSticker;
    }

    @Override
    public void execute(WebClient transport) {
        try {
            sendSticker.validate();
        } catch (TelegramApiValidationException e) {
            throw new TelegramActionException(e);
        }

        String url = SendSticker.PATH;

        MultipartForm form = MultipartForm.create()
                .attribute(SendPhoto.CHATID_FIELD, sendSticker.getChatId());

        InputFileHelper.addBinaryFileToForm(form, sendSticker.getSticker(), SendSticker.STICKER_FIELD, true);

        if (sendSticker.getReplyMarkup() != null) {
            form.attribute(SendSticker.REPLYMARKUP_FIELD, Json.encode(sendSticker.getReplyMarkup()));
        }
        if (sendSticker.getReplyToMessageId() != null) {
            form.attribute(SendSticker.REPLYTOMESSAGEID_FIELD, sendSticker.getReplyToMessageId()
                    .toString());
        }
        if (sendSticker.getDisableNotification() != null) {
            form.attribute(SendSticker.DISABLENOTIFICATION_FIELD, sendSticker.getDisableNotification()
                    .toString());
        }

        transport.post(url).sendMultipartForm(form, resp -> {
            if (resp.failed()) {
                throw new TelegramActionException("Failed to send SendSticker", resp.cause());
            }
        });
    }
}
