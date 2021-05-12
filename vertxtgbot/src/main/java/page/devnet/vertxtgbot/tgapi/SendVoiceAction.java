package page.devnet.vertxtgbot.tgapi;

import io.vertx.core.json.Json;
import io.vertx.ext.web.multipart.MultipartForm;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendVoice;
import org.telegram.telegrambots.meta.exceptions.TelegramApiValidationException;

/**
 * @author maksim
 * @since 03.06.2020
 */
final class SendVoiceAction implements TelegramAction {

    private final SendVoice sendVoice;

    SendVoiceAction(SendVoice sendVoice) {
        assert sendVoice != null;

        this.sendVoice = sendVoice;
    }

    @Override
    public void execute(Transport transport) {
        try {
            sendVoice.validate();
        } catch (TelegramApiValidationException e) {
            throw new TelegramActionException(e);
        }

        String url = SendVoice.PATH;

        MultipartForm form = MultipartForm.create()
                .attribute(SendPhoto.CHATID_FIELD, sendVoice.getChatId());

        InputFileHelper.addBinaryFileToForm(form, sendVoice.getVoice(), SendVoice.VOICE_FIELD, true);

        if (sendVoice.getReplyMarkup() != null) {
            form.attribute(SendVoice.REPLYMARKUP_FIELD, Json.encode(sendVoice.getReplyMarkup()));
        }
        if (sendVoice.getReplyToMessageId() != null) {
            form.attribute(SendVoice.REPLYTOMESSAGEID_FIELD, sendVoice.getReplyToMessageId()
                    .toString());
        }
        if (sendVoice.getDisableNotification() != null) {
            form.attribute(SendVoice.DISABLENOTIFICATION_FIELD, sendVoice.getDisableNotification()
                    .toString());
        }
        if (sendVoice.getDuration() != null) {
            form.attribute(SendVoice.DURATION_FIELD, sendVoice.getDuration().toString());
        }
        if (sendVoice.getCaption() != null) {
            form.attribute(SendVoice.CAPTION_FIELD, sendVoice.getCaption());
            if (sendVoice.getParseMode() != null) {
                form.attribute(SendVoice.PARSEMODE_FIELD, sendVoice.getParseMode());
            }
        }

        transport.send(url, form);
    }
}
