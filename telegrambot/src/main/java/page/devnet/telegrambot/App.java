package page.devnet.telegrambot;

import io.vertx.core.Vertx;
import page.devnet.database.DataSource;
import page.devnet.database.RepositoryFactory;
import page.devnet.pluginmanager.MultiTenantPluginManager;
import page.devnet.pluginmanager.PluginManager;
import page.devnet.telegrambot.util.TenantIdExtractor;
import page.devnet.vertxtgbot.GlobalVertxHolder;
import page.devnet.wordstat.api.Statistics;

public class App {

    public static void main(String[] args) {
        DataSource ds = new DataSource();
        var manager = new MultiTenantPluginManager<>(
                id -> {
//                    TranslateBotPlugin translatePlugin = TranslateBotPlugin.newYandexTranslatePlugin();
                    var repositoryManager = RepositoryFactory.multitenancy(ds, id);
                    var statisticPlugin = new WordStatisticPlugin(new Statistics(repositoryManager.buildWordStorageRepository()), repositoryManager.buildUserRepository());
                    return new PluginManager<>(/*translatePlugin, */statisticPlugin, new WordLimiterPlugin(repositoryManager.buildUnsubscribeRepository()));
                },
                new TenantIdExtractor()
        );
        var wordLimiterPlugin = new WordLimiterPlugin(repositoryManager.getUnsubscribeRepository());

        Vertx vertx = GlobalVertxHolder.getVertx();

        manager.enableAdminPlugin(administartionPlugin);
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
