package page.devnet.vertxtgbot.tgapi;

import io.vertx.ext.web.multipart.MultipartForm;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.exceptions.TelegramApiValidationException;

/**
 * @author maksim
 * @since 03.06.2020
 */
final class SendMediaGroupAction implements TelegramAction {

    private final SendMediaGroup sendMediaGroup;

    SendMediaGroupAction(SendMediaGroup sendMediaGroup) {
        assert sendMediaGroup != null;

        this.sendMediaGroup = sendMediaGroup;
    }

    @Override
    public void execute(Transport transport) {
        try {
            sendMediaGroup.validate();
        } catch (TelegramApiValidationException e) {
            throw new TelegramActionException(e);
        }

        String url = SendMediaGroup.PATH;

        MultipartForm form = MultipartForm.create()
                .attribute(SendPhoto.CHATID_FIELD, sendMediaGroup.getChatId());

        InputFileHelper.addInputMediaToForm(form, sendMediaGroup.getMedia(), SendMediaGroup.MEDIA_FIELD);

        if (sendMediaGroup.getDisableNotification() != null) {
            form.attribute(SendMediaGroup.DISABLENOTIFICATION_FIELD, sendMediaGroup.getDisableNotification()
                    .toString());
        }

        if (sendMediaGroup.getReplyToMessageId() != null) {
            form.attribute(SendMediaGroup.REPLYTOMESSAGEID_FIELD, sendMediaGroup.getReplyToMessageId()
                    .toString());
        }

        transport.send(url, form);
    }
}
