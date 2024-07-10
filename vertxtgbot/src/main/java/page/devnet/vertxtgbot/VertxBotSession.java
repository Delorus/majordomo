package page.devnet.vertxtgbot;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.ext.web.codec.BodyCodec;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.ApiConstants;
import org.telegram.telegrambots.meta.api.methods.updates.GetUpdates;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.BotOptions;
import org.telegram.telegrambots.meta.generics.BotSession;
import org.telegram.telegrambots.meta.generics.LongPollingBot;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author maksim
 * @since 05.06.2020
 */
@Slf4j
public class VertxBotSession implements BotSession {

    private final AtomicBoolean running = new AtomicBoolean(false);

    private final WebClient client;

    private BotOptions options;
    private String token;
    private LongPollingBot callback;

    private int lastReceivedUpdate;

    public VertxBotSession() {

        Vertx vertx = GlobalVertxHolder.getVertx();
        client = WebClient.create(vertx, new WebClientOptions()
                .setSsl(true)
                .setDefaultHost("api.telegram.org")
                .setDefaultPort(443)
                .setKeepAlive(true)
                .setKeepAliveTimeout(0)
                .setIdleTimeout(10)
                .setIdleTimeoutUnit(TimeUnit.MINUTES));
    }

    @Override
    public void setOptions(BotOptions options) {
        if (this.options != null) {
            throw new InvalidParameterException("BotOptions has already been set");
        }
        this.options = options;
    }

    @Override
    public void setToken(String token) {
        if (this.token != null) {
            throw new InvalidParameterException("Token has already been set");
        }
        this.token = token;
    }

    @Override
    public void setCallback(LongPollingBot callback) {
        if (this.callback != null) {
            throw new InvalidParameterException("Callback has already been set");
        }
        this.callback = callback;
    }

    @Override
    public void start() {
        if (!running.compareAndSet(false, true)) {
            log.warn("Long polling bot already running!");
            return;
        }

        lastReceivedUpdate = 0;
        pollUpdates();
    }

    private void pollUpdates() {
        if (!isRunning()) {
            return;
        }

        GetUpdates action = GetUpdates.builder()
                .limit(100)
                .timeout(ApiConstants.GETUPDATES_TIMEOUT)
                .offset(lastReceivedUpdate + 1)
                .build();
                
        GlobalVertxHolder.getVertx().setPeriodic(ApiConstants.GETUPDATES_TIMEOUT*1000, id -> {
            log.debug("Polling updates...");
            client.post("/bot" + token + "/" + GetUpdates.PATH)
                .timeout(-1)
                .as(BodyCodec.jsonObject())
                .sendJson(action, this::processResponce);
        });
    }

    private void processResponce(AsyncResult<HttpResponse<JsonObject>> resp) {
        if (resp.failed()) {
            log.warn("Something wrong, response failed {}", resp.cause());
            //todo test to http code (>500)
            //todo poll next updates after delay
            //                pollUpdates(request);
            return;
        }

        JsonObject body = resp.result().body();
        if (!body.getBoolean("ok")) {
            log.warn("Something wrong: {}", body);
            return;
        }
        List<Update> updates  = new ArrayList<>();
        for (Object rawUpdate : body.getJsonArray("result")) {
            if (!(rawUpdate instanceof JsonObject)) {
                throw new RuntimeException(String.format("Got incorrect [%s] response %s", rawUpdate.getClass(), body.getJsonArray("result")
                        .encodePrettily()));
            }
            Update update = Json.decodeValue(((JsonObject) rawUpdate).encode(), Update.class);
            log.debug("got new update [{}]: {}", update.getUpdateId(), update.getMessage() != null ? update.getMessage().toString() : "[no text]");
            //callback.onUpdateReceived(update);
            updates.add(update);
            if (update.getUpdateId() > lastReceivedUpdate) {
                lastReceivedUpdate = update.getUpdateId();
            }
        }
        callback.onUpdatesReceived(updates);
    }

    @Override
    public void stop() {
        if (!running.compareAndSet(true, false)) {
            log.warn("Long polling bot not runnig!");
        }
    }

    @Override
    public boolean isRunning() {
        return running.get();
    }
}
