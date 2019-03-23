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

        initTelegramConnection(manager);
    }

    private static TranslateBotPlugin createTranslatePlugin() {
        //todo from env
        TranslateService service = new YandexTranslateService("trnsl.1.1.20190302T173434Z.b9d42857e1f5463e.8fdc17ce5df7d4a0aee3a9be6ade62b96671e05f");

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

    private static void initTelegramConnection(BotManager manager) throws TelegramApiRequestException {
        ApiContextInitializer.init();

        TelegramBotsApi api = new TelegramBotsApi("https://perch-tg-bots.herokuapp.com/", "http://0.0.0.0:" + System.getenv("PORT") + "/");

        api.registerBot(manager);
    }
}
