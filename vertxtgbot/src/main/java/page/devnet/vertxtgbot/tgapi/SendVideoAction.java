package page.devnet.vertxtgbot.tgapi;

import io.vertx.core.json.Json;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.multipart.MultipartForm;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.exceptions.TelegramApiValidationException;

/**
 * @author maksim
 * @since 31.05.2020
 */
final class SendVideoAction implements TelegramAction {

    private final SendVideo sendVideo;

    SendVideoAction(SendVideo sendVideo) {
        assert sendVideo != null;

        this.sendVideo = sendVideo;
    }

    @Override
    public void execute(WebClient transport) {
        try {
            sendVideo.validate();
        } catch (TelegramApiValidationException e) {
            throw new TelegramActionException(e);
        }

        String url = SendVideo.PATH;

        MultipartForm form = MultipartForm.create()
                .attribute(SendVideo.CHATID_FIELD, sendVideo.getChatId());

        InputFileHelper.addBinaryFileToForm(form, sendVideo.getVideo(), SendVideo.VIDEO_FIELD, true);

        if (sendVideo.getReplyMarkup() != null) {
            form.attribute(SendVideo.REPLYMARKUP_FIELD, Json.encode(sendVideo.getReplyMarkup()));
        }
        if (sendVideo.getReplyToMessageId() != null) {
            form.attribute(SendVideo.REPLYTOMESSAGEID_FIELD, sendVideo.getReplyToMessageId().toString());
        }
        if (sendVideo.getCaption() != null) {
            form.attribute(SendVideo.CAPTION_FIELD, sendVideo.getCaption());
            if (sendVideo.getParseMode() != null) {
                form.attribute(SendVideo.PARSEMODE_FIELD, sendVideo.getParseMode());
            }
        }
        if (sendVideo.getSupportsStreaming() != null) {
            form.attribute(SendVideo.SUPPORTSSTREAMING_FIELD, sendVideo.getSupportsStreaming()
                    .toString());
        }
        if (sendVideo.getDuration() != null) {
            form.attribute(SendVideo.DURATION_FIELD, sendVideo.getDuration().toString());
        }
        if (sendVideo.getWidth() != null) {
            form.attribute(SendVideo.WIDTH_FIELD, sendVideo.getWidth().toString());
        }
        if (sendVideo.getHeight() != null) {
            form.attribute(SendVideo.HEIGHT_FIELD, sendVideo.getHeight().toString());
        }
        if (sendVideo.getDisableNotification() != null) {
            form.attribute(SendVideo.DISABLENOTIFICATION_FIELD, sendVideo.getDisableNotification().toString());
        }
        if (sendVideo.getThumb() != null) {
            form.attribute(SendVideo.THUMB_FIELD, sendVideo.getThumb().getAttachName());
            InputFileHelper.addBinaryFileToForm(form, sendVideo.getThumb(), SendVideo.THUMB_FIELD, false);
        }

        transport.post(url).sendMultipartForm(form, resp -> {
            if (resp.failed()) {
                throw new TelegramActionException("Failed to send video", resp.cause());
            }
        });
    }
}
