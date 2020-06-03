package page.devnet.vertxtgbot.util;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.junit5.VertxTestContext;
import org.telegram.telegrambots.meta.api.objects.ApiResponse;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Вспомогательный класс для тестирования.
 * <p/>
 * Представляет из себя что-то типа мока телеграм сервера,
 * с дополнительной возможностью сравнивать два запроса на идентичность.
 * Это позволяет сравнивать две реализации телеграм-клиента.
 *
 * @author maksim
 * @since 02.06.2020
 */
public class TestTelegramServer {

    private final HttpServer server;
    private final BlockingQueue<String> singleRequestQueue = new ArrayBlockingQueue<>(1);

    public TestTelegramServer(Vertx vertx) {
        server = vertx.createHttpServer(new HttpServerOptions()
                .setHost("localhost")
                .setPort(0)
                .setLogActivity(true));
    }

    public void startAndServe() throws InterruptedException {
        VertxTestContext context = new VertxTestContext();
        server.requestHandler(event -> event.bodyHandler(req -> {
            singleRequestQueue.add(req.toString());
            event.response().end(okResponse());
            context.completeNow();
        })).listen(context.completing());

        // Ждем пока сервак не стартанет
        assertTrue(context.awaitCompletion(100, TimeUnit.MILLISECONDS));
        if (context.failed()) {
            throw new RuntimeException(context.causeOfFailure());
        }
    }

    public int getPort() {
        return server.actualPort();
    }

    @FunctionalInterface
    public interface TgSingleRequestExecutor {
        void sendRequest() throws TelegramApiException;
    }

    public void recordRequest(TgSingleRequestExecutor executor) {
        assertDoesNotThrow(executor::sendRequest);
    }

    public void recordNewAndCompareWithPrevious(TgSingleRequestExecutor executor) throws InterruptedException {
        String prev = singleRequestQueue.poll(100, TimeUnit.MILLISECONDS);
        assertNotNull(prev);

        assertDoesNotThrow(executor::sendRequest);
        String current = singleRequestQueue.poll(100, TimeUnit.MILLISECONDS);
        assertNotNull(current);

        assertEquals(prev, current);
    }

    private static String okResponse() {
        ApiResponse<Message> response = new ApiResponse<>();
        return "{ \"ok\": \"true\" }";
    }
}
