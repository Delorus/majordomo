package page.devnet.vertxtgbot.tgapi;

import io.vertx.core.json.Json;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.multipart.MultipartForm;
import org.telegram.telegrambots.meta.api.methods.send.SendAudio;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.exceptions.TelegramApiValidationException;

/**
 * @author maksim
 * @since 03.06.2020
 */
final class SendAudioAction implements TelegramAction {

    private final SendAudio sendAudio;

    SendAudioAction(SendAudio sendAudio) {
        assert sendAudio != null;

        this.sendAudio = sendAudio;
    }

    @Override
    public void execute(WebClient transport) {
        try {
            sendAudio.validate();
        } catch (TelegramApiValidationException e) {
            throw new TelegramActionException(e);
        }

        String url = SendAudio.PATH;

        MultipartForm form = MultipartForm.create()
                .attribute(SendPhoto.CHATID_FIELD, sendAudio.getChatId());

        InputFileHelper.addBinaryFileToForm(form, sendAudio.getAudio(), SendAudio.AUDIO_FIELD, true);

        if (sendAudio.getReplyMarkup() != null) {
            form.attribute(SendAudio.REPLYMARKUP_FIELD, Json.encode(sendAudio.getReplyMarkup()));
        }
        if (sendAudio.getReplyToMessageId() != null) {
            form.attribute(SendAudio.REPLYTOMESSAGEID_FIELD, sendAudio.getReplyToMessageId()
                    .toString());
        }
        if (sendAudio.getPerformer() != null) {
            form.attribute(SendAudio.PERFOMER_FIELD, sendAudio.getPerformer());
        }
        if (sendAudio.getTitle() != null) {
            form.attribute(SendAudio.TITLE_FIELD, sendAudio.getTitle());
        }
        if (sendAudio.getDuration() != null) {
            form.attribute(SendAudio.DURATION_FIELD, sendAudio.getDuration().toString());
        }
        if (sendAudio.getDisableNotification() != null) {
            form.attribute(SendAudio.DISABLENOTIFICATION_FIELD, sendAudio.getDisableNotification()
                    .toString());
        }
        if (sendAudio.getCaption() != null) {
            form.attribute(SendAudio.CAPTION_FIELD, sendAudio.getCaption());
            if (sendAudio.getParseMode() != null) {
                form.attribute(SendAudio.PARSEMODE_FIELD, sendAudio.getParseMode());
            }
        }
        if (sendAudio.getThumb() != null) {
            InputFileHelper.addBinaryFileToForm(form, sendAudio.getThumb(), SendAudio.THUMB_FIELD, false);
            form.attribute(SendAudio.THUMB_FIELD, sendAudio.getThumb().getAttachName());
        }

        transport.post(url).sendMultipartForm(form, resp -> {
            if (resp.failed()) {
                throw new TelegramActionException("Failed to send SendAudio", resp.cause());
            }
        });
    }
}
