package page.devnet.vertxtgbot.tgapi;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.codec.BodyCodec;
import io.vertx.ext.web.multipart.MultipartForm;
import lombok.extern.slf4j.Slf4j;


/**
 * @author sherb
 * @since 09.05.2021
 */
@Slf4j
class VertxWebClientWrapper implements Transport {

    private final WebClient client;
    private final String botToken;

    VertxWebClientWrapper(WebClient client, String botToken) {
        this.client = client;
        this.botToken = botToken;
    }

    @Override
    public void send(String url, MultipartForm multipartForm) {
        client.post("/bot"+botToken+"/"+url).sendMultipartForm(multipartForm, resp -> {
            if (resp.failed()) {
                log.error("Failed to send command to {}: {}", url, resp.cause());
            }
        });
    }

    @Override
    public void sendWithResponse(String url, MultipartForm multipartForm, Handler<AsyncResult<HttpResponse<String>>> responseHandler) {
        client.post("/bot"+botToken+"/"+url)
                .as(BodyCodec.string())
                .sendMultipartForm(multipartForm, responseHandler);
    }

    @Override
    public void sendJson(String url, Object json) {
        client.post("/bot"+botToken+"/"+url).sendJson(json, resp -> {
            if (resp.failed()) {
                log.error("Failed to send command to {}: {}", url, resp.cause());
            }
            if (resp.result().statusCode() >= 300) {
                log.error("Failed to send command to {}: {}", url, resp.result().bodyAsJsonObject());
            }
        });
    }
}
