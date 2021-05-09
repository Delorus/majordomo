package page.devnet.vertxtgbot.tgapi;

import io.vertx.core.json.Json;
import io.vertx.ext.web.multipart.MultipartForm;
import org.telegram.telegrambots.meta.api.methods.send.SendAnimation;
import org.telegram.telegrambots.meta.exceptions.TelegramApiValidationException;

/**
 * @author maksim
 * @since 03.06.2020
 */
final class SendAnimationAction implements TelegramAction {

    private final SendAnimation sendAnimation;

    SendAnimationAction(SendAnimation sendAnimation) {
        assert sendAnimation != null;

        this.sendAnimation = sendAnimation;
    }

    @Override
    public void execute(Transport transport) {
        try {
            sendAnimation.validate();
        } catch (TelegramApiValidationException e) {
            throw new TelegramActionException(e);
        }

        String url = SendAnimation.PATH;

        MultipartForm form = MultipartForm.create()
                .attribute(SendAnimation.CHATID_FIELD, sendAnimation.getChatId());

        InputFileHelper.addBinaryFileToForm(form, sendAnimation.getAnimation(), SendAnimation.ANIMATION_FIELD, true);

        if (sendAnimation.getReplyMarkup() != null) {
            form.attribute(SendAnimation.REPLYMARKUP_FIELD, Json.encode(sendAnimation.getReplyMarkup()));
        }
        if (sendAnimation.getReplyToMessageId() != null) {
            form.attribute(SendAnimation.REPLYTOMESSAGEID_FIELD, sendAnimation.getReplyToMessageId().toString());
        }
        if (sendAnimation.getDisableNotification() != null) {
            form.attribute(SendAnimation.DISABLENOTIFICATION_FIELD, sendAnimation.getDisableNotification().toString());
        }
        if (sendAnimation.getDuration() != null) {
            form.attribute(SendAnimation.DURATION_FIELD, sendAnimation.getDuration().toString());
        }
        if (sendAnimation.getWidth() != null) {
            form.attribute(SendAnimation.WIDTH_FIELD, sendAnimation.getWidth().toString());
        }
        if (sendAnimation.getHeight() != null) {
            form.attribute(SendAnimation.HEIGHT_FIELD, sendAnimation.getHeight().toString());
        }
        if (sendAnimation.getThumb() != null) {
            InputFileHelper.addBinaryFileToForm(form, sendAnimation.getThumb(), SendAnimation.THUMB_FIELD, false);
            form.attribute(SendAnimation.THUMB_FIELD, sendAnimation.getThumb().getAttachName());
        }

        if (sendAnimation.getCaption() != null) {
            form.attribute(SendAnimation.CAPTION_FIELD, sendAnimation.getCaption());
            if (sendAnimation.getParseMode() != null) {
                form.attribute(SendAnimation.PARSEMODE_FIELD, sendAnimation.getParseMode());
            }
        }

        transport.send(url, form);
    }
}
