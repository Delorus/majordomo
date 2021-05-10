package page.devnet.telegrambot;

import io.vertx.core.Vertx;
import page.devnet.database.DataSource;
import page.devnet.database.RepositoryFactory;
import page.devnet.pluginmanager.PluginManager;
import page.devnet.vertxtgbot.GlobalVertxHolder;
import page.devnet.wordstat.api.Statistics;

public class App {

    public static void main(String[] args) {
//        TranslateBotPlugin translatePlugin = TranslateBotPlugin.newYandexTranslatePlugin();
        var repositoryManager = new RepositoryFactory(new DataSource());
        var statisticPlugin = new WordStatisticPlugin(new Statistics(repositoryManager.buildWordStorageRepository()), repositoryManager.buildUserRepository());

        var manager = new PluginManager<>(/*translatePlugin, */statisticPlugin, new WordLimiterPlugin(repositoryManager.buildUnsubscribeRepository()));

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
