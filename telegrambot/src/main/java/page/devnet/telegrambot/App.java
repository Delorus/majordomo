package page.devnet.telegrambot;

import io.vertx.core.Vertx;
import page.devnet.database.DataSource;
import page.devnet.database.RepositoryFactory;
import page.devnet.database.repository.impl.IgnoreMeRepositoryImpl;
import page.devnet.pluginmanager.MultiTenantPluginManager;
import page.devnet.pluginmanager.PluginManager;
import page.devnet.telegrambot.util.TenantIdExtractor;
import page.devnet.vertxtgbot.GlobalVertxHolder;
import page.devnet.wordstat.api.Statistics;

public class App {

    public static void main(String[] args) {
        DataSource ds = isProd(args) ? new DataSource() : DataSource.inMemory();
        var manager = new IgnoreMeFilter(
            new MultiTenantPluginManager<>(
                id -> {
                    var repositoryManager = RepositoryFactory.multitenancy(ds, id);
                    var statisticPlugin = new WordStatisticPlugin(new Statistics(repositoryManager.buildWordStorageRepository()), repositoryManager.buildUserRepository());
                    var yesnoplug = new YesNoPlugin();
                    var pluginManager = new PluginManager<>(statisticPlugin, new WordLimiterPlugin(repositoryManager.buildUnsubscribeRepository()), yesnoplug);
                    return pluginManager;
                },
                new TenantIdExtractor()
            ),
            new IgnoreMeRepositoryImpl(ds));

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
