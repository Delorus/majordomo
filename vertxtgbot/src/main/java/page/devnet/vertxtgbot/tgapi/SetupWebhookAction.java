package page.devnet.vertxtgbot.tgapi;

import io.vertx.ext.web.multipart.MultipartForm;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiValidationException;

/**
 * @author maksim
 * @since 04.06.2020
 */
@Slf4j
public final class SetupWebhookAction implements TelegramAction {

    private final SetWebhook setWebhook;

    public SetupWebhookAction(SetWebhook setWebhook) {
        this.setWebhook = setWebhook;
    }

    public void execute(Transport transport) {
        try {
            setWebhook.validate();
        } catch (TelegramApiValidationException e) {
            throw new TelegramActionException(e);
        }

        String requestUrl = SetWebhook.PATH;

        MultipartForm form = MultipartForm.create()
                .attribute(SetWebhook.URL_FIELD, setWebhook.getUrl());

        if (setWebhook.getCertificate() != null) {
            throw new TelegramActionException("self signed certificates not supported");
        }

        transport.sendWithResponse(requestUrl, form, resp -> {
            if (resp.succeeded()) {
                try {
                    var ok = setWebhook.deserializeResponse(resp.result().body());
                    if (ok) {
                        log.info("Webhook successfully registered on address: {}", setWebhook.getUrl());
                    }
                } catch (TelegramApiRequestException e) {
                    throw new TelegramActionException("Error setting webhook", e);
                }
            } else {
                throw new TelegramActionException("Error setting webhook", resp.cause());
            }
        });
    }
}
