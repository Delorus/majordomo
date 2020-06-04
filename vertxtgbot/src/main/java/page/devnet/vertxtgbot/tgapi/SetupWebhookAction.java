package page.devnet.vertxtgbot.tgapi;

import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.codec.BodyCodec;
import io.vertx.ext.web.multipart.MultipartForm;
import org.telegram.telegrambots.meta.ApiConstants;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;

import java.io.File;

/**
 * @author maksim
 * @since 04.06.2020
 */
public final class SetupWebhookAction implements TelegramAction {

    private final String url;
    private final String publicCertificatePath;

    public SetupWebhookAction(String url, String publicCertificatePath) {

        this.url = url;
        this.publicCertificatePath = publicCertificatePath;
    }

    @Override
    public void execute(WebClient transport) {
        String requestUrl = SetWebhook.PATH;

        MultipartForm form = MultipartForm.create()
                .attribute(SetWebhook.URL_FIELD, url);

        if (publicCertificatePath != null) {
            File certificate = new File(publicCertificatePath);
            if (certificate.exists()) {
                form.binaryFileUpload(SetWebhook.CERTIFICATE_FIELD, certificate.getName(), certificate.getAbsolutePath(), InputFileHelper.APPLICATION_OCTET_STREAM);
            }
        }

        transport.post(requestUrl)
                .as(BodyCodec.jsonObject())
                .sendMultipartForm(form, resp -> {
                    if (resp.succeeded()) {
                        JsonObject result = resp.result().body();
                        if (!result.getBoolean(ApiConstants.RESPONSE_FIELD_OK)) {
                            throw new TelegramActionException("Error setting webhook:\n" + Json.encodePrettily(result));
                        }
                    } else {
                        throw new TelegramActionException("Error setting webhook", resp.cause());
                    }
                });
    }
}
