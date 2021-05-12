package page.devnet.vertxtgbot.tgapi;

import io.vertx.core.Vertx;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.methods.updates.GetUpdates;
import page.devnet.vertxtgbot.util.ReferenceBot;
import page.devnet.vertxtgbot.util.TestTelegramServer;

/**
 * @author maksim
 * @since 03.06.2020
 */
class GetUpdatesActionTest {

    static TestTelegramServer tgServer;

    static WebClient tgClient;
    static ReferenceBot referenceClient;

    @BeforeAll
    public static void init() throws InterruptedException {
        Vertx vertx = Vertx.vertx();

        tgServer = new TestTelegramServer(vertx);
        tgServer.startAndServe();

        tgClient = WebClient.create(vertx, new WebClientOptions()
                .setDefaultHost("localhost")
                .setDefaultPort(tgServer.getPort()));

        referenceClient = ReferenceBot.newBot(tgServer.getPort());
    }

    @Test
    public void sendGetUpdates() throws Throwable {
        // Setup
        GetUpdates msg = new GetUpdates();


        tgServer.recordRequest(() -> {
            referenceClient.execute(msg);
        });

        // Expect
        tgServer.recordNewAndCompareWithPrevious(() -> {
            new GetUpdatesAction(msg).execute(new VertxWebClientWrapper(tgClient, "test"));
        });
    }



}
