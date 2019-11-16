package page.devnet.telegrambot;

import page.devnet.pluginmanager.PluginManager;
import page.devnet.telegrambot.translate.TranslateBotPlugin;

public class App {

    public static void main(String[] args) {
        TranslateBotPlugin translatePlugin = TranslateBotPlugin.newYandexTranslatePlugin();

        var manager = new PluginManager<>(translatePlugin);

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
}
