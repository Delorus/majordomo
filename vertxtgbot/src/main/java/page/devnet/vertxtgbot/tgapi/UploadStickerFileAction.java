package page.devnet.vertxtgbot.tgapi;

import io.vertx.ext.web.multipart.MultipartForm;
import org.telegram.telegrambots.meta.api.methods.stickers.UploadStickerFile;
import org.telegram.telegrambots.meta.exceptions.TelegramApiValidationException;

/**
 * @author maksim
 * @since 03.06.2020
 */
final class UploadStickerFileAction implements TelegramAction {

    private final UploadStickerFile uploadStickerFile;

    UploadStickerFileAction(UploadStickerFile uploadStickerFile) {
        assert uploadStickerFile != null;

        this.uploadStickerFile = uploadStickerFile;
    }

    @Override
    public void execute(Transport transport) {
        try {
            uploadStickerFile.validate();
        } catch (TelegramApiValidationException e) {
            throw new TelegramActionException(e);
        }

        String url = UploadStickerFile.PATH;

        MultipartForm form = MultipartForm.create()
                .attribute(UploadStickerFile.USERID_FIELD, uploadStickerFile.getUserId().toString());

        InputFileHelper.addBinaryFileToForm(form, uploadStickerFile.getPngSticker(), UploadStickerFile.PNGSTICKER_FIELD, true);

        transport.send(url, form);
    }
}
