package page.devnet.vertxtgbot.tgapi;

import io.vertx.core.json.Json;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.multipart.MultipartForm;
import org.telegram.telegrambots.meta.api.methods.stickers.AddStickerToSet;
import org.telegram.telegrambots.meta.exceptions.TelegramApiValidationException;

/**
 * @author maksim
 * @since 03.06.2020
 */
final class AddStickerToSetAction implements TelegramAction {

    private final AddStickerToSet addStickerToSet;

    AddStickerToSetAction(AddStickerToSet addStickerToSet) {
        assert addStickerToSet != null;

        this.addStickerToSet = addStickerToSet;
    }

    @Override
    public void execute(WebClient transport) {
        try {
            addStickerToSet.validate();
        } catch (TelegramApiValidationException e) {
            throw new TelegramActionException(e);
        }

        String url = AddStickerToSet.PATH;

        MultipartForm form = MultipartForm.create()
                .attribute(AddStickerToSet.USERID_FIELD, addStickerToSet.getUserId().toString())
                .attribute(AddStickerToSet.NAME_FIELD, addStickerToSet.getName())
                .attribute(AddStickerToSet.EMOJIS_FIELD, addStickerToSet.getEmojis());
        InputFileHelper.addBinaryFileToForm(form, addStickerToSet.getPngSticker(), AddStickerToSet.PNGSTICKER_FIELD, true);

        if (addStickerToSet.getMaskPosition() != null) {
            form.attribute(AddStickerToSet.MASKPOSITION_FIELD, Json.encode(addStickerToSet.getMaskPosition()));
        }

        transport.post(url).sendMultipartForm(form, resp -> {
            if (resp.failed()) {
                throw new TelegramActionException("Failed to send AddStickerToSet", resp.cause());
            }
        });
    }
}
