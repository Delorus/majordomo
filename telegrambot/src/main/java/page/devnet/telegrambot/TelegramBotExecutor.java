package page.devnet.telegrambot;

import io.vertx.core.Vertx;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import page.devnet.pluginmanager.MessageSubscriber;
import page.devnet.vertxtgbot.VertxBotSession;
import page.devnet.vertxtgbot.VertxWebhook;

import java.util.List;

/**
 * @author maksim
 * @since 16.11.2019
 */
@Slf4j
public final class TelegramBotExecutor {

    public static TelegramBotExecutor newInDevMode(Vertx vertx) {
        return new TelegramBotExecutor(vertx, false);
    }

    public static TelegramBotExecutor newInProdMode(Vertx vertx) {
        return new TelegramBotExecutor(vertx, true);
    }

    private final Vertx vertx;
    private final boolean isProd;

    private TelegramBotExecutor(Vertx vertx, boolean isProd) {
        this.isProd = isProd;
        this.vertx = vertx;
    }

    public void runBotWith(MessageSubscriber<Update, List<PartialBotApiMethod<?>>> subscriber) {
        var telegramBot = createTelegramBot(subscriber);

        try {
            initTelegramConnection(telegramBot, isProd);
        } catch (TelegramApiException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    private TelegramBot createTelegramBot(MessageSubscriber<Update, List<PartialBotApiMethod<?>>> subscriber) {
        TelegramBot.Setting setting = TelegramBot.Setting.builder()
                .name(System.getenv("TG_BOT_NAME"))
                .token(System.getenv("TG_BOT_TOKEN"))
                .path(System.getenv("TG_BOT_NAME"))
                .build();

        return new TelegramBot(vertx, setting, subscriber);
    }

    private void initTelegramConnection(TelegramBot bot, boolean isProdEnv) throws TelegramApiException {

        TelegramBotsApi api;
        if (isProdEnv) {
            var webhook = new VertxWebhook(vertx);
            webhook.setInternalUrl("http://0.0.0.0:" + System.getenv("PORT") + "/");
            api = new TelegramBotsApi(VertxBotSession.class, webhook);
        } else {
            api = new TelegramBotsApi(VertxBotSession.class);
        }

        if (isProdEnv) {
            var setWebhook = new SetWebhook(System.getenv("EXTERNAL_URI") + System.getenv("PORT") +"/" + bot.atProductionBotManager().getBotToken());
            api.registerBot(bot.atProductionBotManager(), setWebhook);
        } else {
            api.registerBot(bot.atDevBotManager());
        }
    }
}
