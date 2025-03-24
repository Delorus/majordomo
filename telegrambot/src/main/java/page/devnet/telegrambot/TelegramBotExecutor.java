package page.devnet.telegrambot;

import io.vertx.core.Vertx;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.meta.api.methods.botapimethods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import page.devnet.pluginmanager.MessageSubscriber;

import java.util.List;

/**
 * @author maksim
 * @since 16.11.2019
 */
@Slf4j
public final class TelegramBotExecutor {

    public static TelegramBotExecutor newInDevMode() {
        return new TelegramBotExecutor( false);
    }

    public static TelegramBotExecutor newInProdMode() {
        return new TelegramBotExecutor(true);
    }

    private final boolean isProd;

    private TelegramBotExecutor(boolean isProd) {
        this.isProd = isProd;
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
                .name("ComXvrBot")
                .token(System.getenv("TELEGRAM_TOKEN"))
                .path("ComXvrBot")
                /*.name(System.getenv("TG_BOT_NAME"))
                .token(System.getenv("TG_BOT_TOKEN"))
                .path(System.getenv("TG_BOT_NAME"))*/
                .build();

        return new TelegramBot(setting, subscriber);
    }

    private void initTelegramConnection(TelegramBot bot, boolean isProdEnv) throws TelegramApiException {

        if (isProdEnv) {
            log.info("Start telegram bot in prod mode");
            try (TelegramBotsLongPollingApplication botsApplication = new TelegramBotsLongPollingApplication()) {
                botsApplication.registerBot(bot.getBotToken(), bot);
                Thread.currentThread().join();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            log.info("Start telegram bot in dev mode");
            try ( TelegramBotsLongPollingApplication botsApplication = new TelegramBotsLongPollingApplication()) {
                botsApplication.registerBot(bot.getBotToken(), bot);
                Thread.currentThread().join();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
