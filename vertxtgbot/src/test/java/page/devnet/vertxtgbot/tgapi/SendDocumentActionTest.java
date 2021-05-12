package page.devnet.vertxtgbot.tgapi;

import io.vertx.core.Vertx;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import page.devnet.vertxtgbot.util.ReferenceBot;
import page.devnet.vertxtgbot.util.TestTelegramServer;

import java.io.File;

/**
 * @author maksim
 * @since 31.05.2020
 */
@Disabled("потому что в мультипарте генерируется уникальный ID и два реквеста не получится сравнить")
public class SendDocumentActionTest {

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
    public void sendDocument() throws Throwable {
        // Setup
        SendDocument document = new SendDocument();
        document.setChatId(123L);
        document.enableNotification();
        document.setCaption("test file");

        document.setDocument(new File("src/test/resources/test_files/document.txt"));

        tgServer.recordRequest(() -> {
            referenceClient.execute(document);
        });

        // Expect
        tgServer.recordNewAndCompareWithPrevious(() -> {
            new SendDocumentAction(document).execute(new VertxWebClientWrapper(tgClient, "test"));
        });
    }

}
