package page.devnet.vertxtgbot.tgapi;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.multipart.MultipartForm;


/**
 * @author sherb
 * @since 09.05.2021
 */
public interface Transport {

    void send(String url, MultipartForm multipartForm);

    void sendWithResponse(String url, MultipartForm multipartForm, Handler<AsyncResult<HttpResponse<String>>> responseHandler);

    void sendJson(String url, Object json);
}
