package page.devnet.vertxtgbot.tgapi;

import io.vertx.core.json.Json;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.multipart.MultipartForm;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendVideoNote;
import org.telegram.telegrambots.meta.exceptions.TelegramApiValidationException;

/**
 * @author maksim
 * @since 03.06.2020
 */
final class SendVideoNoteAction implements TelegramAction {

    private final SendVideoNote sendVideoNote;

    SendVideoNoteAction(SendVideoNote sendVideoNote) {
        assert sendVideoNote != null;

        this.sendVideoNote = sendVideoNote;
    }

    @Override
    public void execute(WebClient transport) {
        try {
            sendVideoNote.validate();
        } catch (TelegramApiValidationException e) {
            throw new TelegramActionException(e);
        }

        String url = SendVideoNote.PATH;

        MultipartForm form = MultipartForm.create()
                .attribute(SendPhoto.CHATID_FIELD, sendVideoNote.getChatId());

        InputFileHelper.addBinaryFileToForm(form, sendVideoNote.getVideoNote(), SendVideoNote.VIDEONOTE_FIELD, true);

        if (sendVideoNote.getReplyMarkup() != null) {
            form.attribute(SendVideoNote.REPLYMARKUP_FIELD, Json.encode(sendVideoNote.getReplyMarkup()));
        }
        if (sendVideoNote.getReplyToMessageId() != null) {
            form.attribute(SendVideoNote.REPLYTOMESSAGEID_FIELD, sendVideoNote.getReplyToMessageId()
                    .toString());
        }
        if (sendVideoNote.getDuration() != null) {
            form.attribute(SendVideoNote.DURATION_FIELD, sendVideoNote.getDuration().toString());
        }
        if (sendVideoNote.getLength() != null) {
            form.attribute(SendVideoNote.LENGTH_FIELD, sendVideoNote.getLength().toString());
        }
        if (sendVideoNote.getDisableNotification() != null) {
            form.attribute(SendVideoNote.DISABLENOTIFICATION_FIELD, sendVideoNote.getDisableNotification()
                    .toString());
        }
        if (sendVideoNote.getThumb() != null) {
            InputFileHelper.addBinaryFileToForm(form, sendVideoNote.getThumb(), SendVideoNote.THUMB_FIELD, false);
            form.attribute(SendVideoNote.THUMB_FIELD, sendVideoNote.getThumb().getAttachName());
        }

        transport.post(url).sendMultipartForm(form, resp -> {
            if (resp.failed()) {
                throw new TelegramActionException("Failed to send SendVideoNote", resp.cause());
            }
        });
    }
}
