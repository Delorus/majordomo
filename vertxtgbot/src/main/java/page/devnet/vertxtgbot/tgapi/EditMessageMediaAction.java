package page.devnet.vertxtgbot.tgapi;

import io.vertx.core.json.Json;
import io.vertx.ext.web.multipart.MultipartForm;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageMedia;
import org.telegram.telegrambots.meta.exceptions.TelegramApiValidationException;

/**
 * @author maksim
 * @since 03.06.2020
 */
final class EditMessageMediaAction implements TelegramAction {

    private final EditMessageMedia editMessageMedia;

    EditMessageMediaAction(EditMessageMedia editMessageMedia) {
        assert editMessageMedia != null;

        this.editMessageMedia = editMessageMedia;
    }

    @Override
    public void execute(Transport transport) {
        try {
            editMessageMedia.validate();
        } catch (TelegramApiValidationException e) {
            throw new TelegramActionException(e);
        }

        String url = EditMessageMedia.PATH;

        MultipartForm form = MultipartForm.create()
                .attribute(EditMessageMedia.CHATID_FIELD, editMessageMedia.getChatId());

        if (editMessageMedia.getInlineMessageId() == null) {
            form.attribute(EditMessageMedia.CHATID_FIELD, editMessageMedia.getChatId());
            form.attribute(EditMessageMedia.MESSAGEID_FIELD, editMessageMedia.getMessageId()
                    .toString());

        } else {
            form.attribute(EditMessageMedia.INLINE_MESSAGE_ID_FIELD, editMessageMedia.getInlineMessageId());
        }
        if (editMessageMedia.getReplyMarkup() != null) {
            form.attribute(EditMessageMedia.REPLYMARKUP_FIELD, Json.encode(editMessageMedia.getReplyMarkup()));
        }

        InputFileHelper.addInputMediaToForm(form, editMessageMedia.getMedia(), EditMessageMedia.MEDIA_FIELD);


        transport.send(url, form);
    }
}
