package page.devnet.telegrambot;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import page.devnet.pluginmanager.MessageSubscriber;

/**
 * @author maksim
 * @since 16.11.2019
 */
@Slf4j
public final class TelegramBotExecutor {

    public static TelegramBotExecutor newInDevMode() {
        return new TelegramBotExecutor(false);
    }

    public static TelegramBotExecutor newInProdMode() {
        return new TelegramBotExecutor(true);
    }

    private final boolean isProd;

    private TelegramBotExecutor(boolean isProd) {
        this.isProd = isProd;
    }

    public void runBotWith(MessageSubscriber subscriber) {
        var telegramBot = createTelegramBot(subscriber);

        try {
            initTelegramConnection(telegramBot, isProd);
        } catch (TelegramApiRequestException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    private TelegramBot createTelegramBot(MessageSubscriber subscriber) {
        TelegramBot.Setting setting = TelegramBot.Setting.builder()
                .name(System.getenv("TG_BOT_NAME"))
                .token(System.getenv("TG_BOT_TOKEN"))
                .path(System.getenv("TG_BOT_NAME"))
                .build();

        return new TelegramBot(setting, subscriber);
    }

    private void initTelegramConnection(TelegramBot bot, boolean isProdEnv) throws TelegramApiRequestException {
        ApiContextInitializer.init();

        TelegramBotsApi api = new TelegramBotsApi(System.getenv("EXTERNAL_URI"), "http://0.0.0.0:" + System.getenv("PORT") + "/");

        if (isProdEnv) {
            api.registerBot(bot.atProductionBotManager());
        } else {
            api.registerBot(bot.atDevBotManager());
        }
    }
}
