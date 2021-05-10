package page.devnet.telegrambot;

import io.vertx.core.Vertx;
import page.devnet.database.RepositoryManager;
import page.devnet.pluginmanager.PluginManager;
import page.devnet.vertxtgbot.GlobalVertxHolder;
import page.devnet.wordstat.api.Statistics;

public class App {

    public static void main(String[] args) {
//        TranslateBotPlugin translatePlugin = TranslateBotPlugin.newYandexTranslatePlugin();
        var repositoryManager = new RepositoryManager();
        var statisticPlugin = new WordStatisticPlugin(new Statistics(repositoryManager.getWordStorageRepository()), repositoryManager.getUserRepository());

        var manager = new PluginManager<>(/*translatePlugin, */statisticPlugin, new WordLimiterPlugin(repositoryManager.getUnsubscribeRepository()));

        Vertx vertx = GlobalVertxHolder.getVertx();

        if (isProd(args)) {
            TelegramBotExecutor.newInProdMode(vertx).runBotWith(manager);
        } else {
            TelegramBotExecutor.newInDevMode(vertx).runBotWith(manager);
        }
    }

    private static boolean isProd(String[] args) {
        if (args.length < 1) {
            return true;
        }

        return !"-dev".equals(args[0]);
    }
}
