package page.devnet.vertxtgbot.tgapi;

import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.codec.BodyCodec;
import io.vertx.ext.web.multipart.MultipartForm;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.ApiConstants;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;

import java.io.File;

/**
 * @author maksim
 * @since 04.06.2020
 */
@Slf4j
public final class SetupWebhookAction {

    private final String token;
    private final String url;
    private final String publicCertificatePath;

    public SetupWebhookAction(String token, String url, String publicCertificatePath) {
        this.token = token;
        this.url = url;
        this.publicCertificatePath = publicCertificatePath;
    }

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

        transport.post("/bot"+token+"/"+requestUrl)
                .as(BodyCodec.string())
                .sendMultipartForm(form, resp -> {
                    if (resp.succeeded()) {
                        JsonObject result = (JsonObject) Json.decodeValue(resp.result().body());
                        if (result.getBoolean(ApiConstants.RESPONSE_FIELD_OK)) {
                            log.info("Webhook successfully registered on address: {}", url);
                        } else {
                            throw new TelegramActionException("Error setting webhook:\n" + Json.encodePrettily(result));
                        }
                    } else {
                        throw new TelegramActionException("Error setting webhook", resp.cause());
                    }
                });
    }
}
