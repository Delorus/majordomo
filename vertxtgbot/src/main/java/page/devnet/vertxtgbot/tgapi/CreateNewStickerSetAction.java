package page.devnet.vertxtgbot.tgapi;

import io.vertx.core.json.Json;
import io.vertx.ext.web.multipart.MultipartForm;
import org.telegram.telegrambots.meta.api.methods.stickers.CreateNewStickerSet;
import org.telegram.telegrambots.meta.exceptions.TelegramApiValidationException;

/**
 * @author maksim
 * @since 03.06.2020
 */
final class CreateNewStickerSetAction implements TelegramAction {

    private final CreateNewStickerSet createNewStickerSet;

    CreateNewStickerSetAction(CreateNewStickerSet createNewStickerSet) {
        assert createNewStickerSet != null;

        this.createNewStickerSet = createNewStickerSet;
    }

    @Override
    public void execute(Transport transport) {
        try {
            createNewStickerSet.validate();
        } catch (TelegramApiValidationException e) {
            throw new TelegramActionException(e);
        }

        String url = CreateNewStickerSet.PATH;

        MultipartForm form = MultipartForm.create()
                .attribute(CreateNewStickerSet.USERID_FIELD, createNewStickerSet.getUserId().toString())
                .attribute(CreateNewStickerSet.NAME_FIELD, createNewStickerSet.getName())
                .attribute(CreateNewStickerSet.TITLE_FIELD, createNewStickerSet.getTitle())
                .attribute(CreateNewStickerSet.EMOJIS_FIELD, createNewStickerSet.getEmojis())
                .attribute(CreateNewStickerSet.CONTAINSMASKS_FIELD, createNewStickerSet.getContainsMasks().toString());

        InputFileHelper.addBinaryFileToForm(form, createNewStickerSet.getPngSticker(), CreateNewStickerSet.PNGSTICKER_FIELD, true);

        if (createNewStickerSet.getMaskPosition() != null) {
            form.attribute(CreateNewStickerSet.MASKPOSITION_FIELD, Json.encode(createNewStickerSet.getMaskPosition()));
        }

        transport.send(url, form);
    }
}
