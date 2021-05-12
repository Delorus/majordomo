package page.devnet.vertxtgbot.tgapi;

import io.vertx.core.json.Json;
import io.vertx.ext.web.multipart.MultipartForm;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.exceptions.TelegramApiValidationException;

/**
 * @author maksim
 * @since 31.05.2020
 */
final class SendDocumentAction implements TelegramAction {

    private final SendDocument sendDocument;

    SendDocumentAction(SendDocument sendDocument) {
        assert sendDocument != null;

        this.sendDocument = sendDocument;
    }

    @Override
    public void execute(Transport transport) {
        try {
            sendDocument.validate();
        } catch (TelegramApiValidationException e) {
            throw new TelegramActionException(e);
        }

        String url = SendDocument.PATH;

        MultipartForm form = MultipartForm.create()
                .attribute(SendDocument.CHATID_FIELD, sendDocument.getChatId());

        InputFileHelper.addTextFileToForm(form, sendDocument.getDocument(), SendDocument.DOCUMENT_FIELD, true);

        if (sendDocument.getReplyMarkup() != null) {
            form.attribute(SendDocument.REPLYMARKUP_FIELD, Json.encode(sendDocument.getReplyMarkup()));
        }
        if (sendDocument.getReplyToMessageId() != null) {
            form.attribute(SendDocument.REPLYTOMESSAGEID_FIELD, sendDocument.getReplyToMessageId()
                    .toString());
        }
        if (sendDocument.getCaption() != null) {
            form.attribute(SendDocument.CAPTION_FIELD, sendDocument.getCaption());
            if (sendDocument.getParseMode() != null) {
                form.attribute(SendDocument.PARSEMODE_FIELD, sendDocument.getParseMode());
            }
        }
        if (sendDocument.getDisableNotification() != null) {
            form.attribute(SendDocument.DISABLENOTIFICATION_FIELD, sendDocument.getDisableNotification()
                    .toString());
        }

        if (sendDocument.getThumb() != null) {
            InputFileHelper.addTextFileToForm(form, sendDocument.getThumb(), SendDocument.THUMB_FIELD, false);
            form.attribute(SendDocument.THUMB_FIELD, sendDocument.getThumb().getAttachName());
        }

        transport.send(url, form);
    }
}
