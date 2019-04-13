package ru.sherb;

import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import ru.sherb.bot.BotManager;
import ru.sherb.bot.BotPlugin;
import ru.sherb.translate.TranslateBotPlugin;
import ru.sherb.translate.TranslateService;
import ru.sherb.translate.yandex.YandexTranslateService;

public class Launch {

    public static void main(String[] args) throws TelegramApiRequestException {
        TranslateBotPlugin translatePlugin = createTranslatePlugin();

        BotManager manager = createBotManager(translatePlugin);

        initTelegramConnection(manager, isProd(args));
    }

    private static boolean isProd(String[] args) {
        if (args.length < 1) {
            return true;
        }

        return !"-dev".equals(args[0]);
    }

    private static TranslateBotPlugin createTranslatePlugin() {
        TranslateService service = new YandexTranslateService(System.getenv("YNDX_TRNSL_API_KEY"));

        return new TranslateBotPlugin(service);
    }

    private static BotManager createBotManager(BotPlugin plugin, BotPlugin... plugins) {
        BotManager.Setting setting = BotManager.Setting.builder()
                .name(System.getenv("TG_BOT_NAME"))
                .token(System.getenv("TG_BOT_TOKEN"))
                .path(System.getenv("TG_BOT_NAME"))
                .build();

        return new BotManager(setting, plugin, plugins);
    }

    private static void initTelegramConnection(BotManager manager, boolean isProdEnv) throws TelegramApiRequestException {
        ApiContextInitializer.init();

        TelegramBotsApi api = new TelegramBotsApi(System.getenv("EXTERNAL_URI"), "http://0.0.0.0:" + System.getenv("PORT") + "/");

        if (isProdEnv) {
            api.registerBot(manager.atProductionBotManager());
        } else {
            api.registerBot(manager.atDevBotManager());
        }
    }
}
