package page.devnet.vertxtgbot.tgapi;

import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.multipart.MultipartForm;
import org.telegram.telegrambots.meta.api.methods.groupadministration.SetChatPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.exceptions.TelegramApiValidationException;

import java.io.File;

import static page.devnet.vertxtgbot.tgapi.InputFileHelper.APPLICATION_OCTET_STREAM;

/**
 * @author maksim
 * @since 03.06.2020
 */
final class SetChatPhotoAction implements TelegramAction {

    private final SetChatPhoto setChatPhoto;

    SetChatPhotoAction(SetChatPhoto setChatPhoto) {
        assert setChatPhoto != null;

        this.setChatPhoto = setChatPhoto;
    }

    @Override
    public void execute(WebClient transport) {
        try {
            setChatPhoto.validate();
        } catch (TelegramApiValidationException e) {
            throw new TelegramActionException(e);
        }

        String url = SetChatPhoto.PATH;

        MultipartForm form = MultipartForm.create()
                .attribute(SendPhoto.CHATID_FIELD, setChatPhoto.getChatId());

        form.attribute(SetChatPhoto.CHATID_FIELD, setChatPhoto.getChatId());
        if (setChatPhoto.getPhoto() != null) {
            File photo = setChatPhoto.getPhoto();

            form.binaryFileUpload(SetChatPhoto.PHOTO_FIELD, photo.getName(), photo.getAbsolutePath(), APPLICATION_OCTET_STREAM);
        } else if (setChatPhoto.getPhotoStream() != null) {
            throw new UnsupportedOperationException("Upload file via InputStream is not supported to ensure non-blocking operations");
        }

        transport.post(url).sendMultipartForm(form, resp -> {
            if (resp.failed()) {
                throw new TelegramActionException("Failed to send SetChatPhoto", resp.cause());
            }
        });
    }
}
