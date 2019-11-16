package page.devnet.app;

import page.devnet.app.translate.TranslateBotPlugin;
import page.devnet.app.translate.TranslateService;
import page.devnet.app.translate.yandex.YandexTranslateService;
import page.devnet.pluginmanager.PluginManager;
import page.devnet.telegrambot.TelegramBotExecutor;

public class App {

    public static void main(String[] args) {
        TranslateBotPlugin translatePlugin = createTranslatePlugin();

        PluginManager manager = new PluginManager(translatePlugin);

        if (isProd(args)) {
            TelegramBotExecutor.newInProdMode().runBotWith(manager);
        } else {
            TelegramBotExecutor.newInDevMode().runBotWith(manager);
        }
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
}
