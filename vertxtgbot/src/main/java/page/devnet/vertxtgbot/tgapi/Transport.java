package page.devnet.vertxtgbot.tgapi;

import io.vertx.ext.web.multipart.MultipartForm;

/**
 * @author sherb
 * @since 09.05.2021
 */
public interface Transport {

    void send(String url, MultipartForm multipartForm);

    void sendJson(String url, Object json);
}
