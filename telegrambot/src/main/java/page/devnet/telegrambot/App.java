package page.devnet.telegrambot;

import io.vertx.core.Vertx;
import page.devnet.convertercurrency.fxratesapi.FxRatesApiService;
import page.devnet.database.DataSource;
import page.devnet.database.RepositoryFactory;
import page.devnet.database.repository.impl.IgnoreMeRepositoryImpl;
import page.devnet.pluginmanager.MultiTenantPluginManager;
import page.devnet.pluginmanager.PluginManager;
import page.devnet.telegrambot.convertercurrency.CurrencyRatePlugin;
import page.devnet.telegrambot.timezone.TelegramTimeZonePlugin;
import page.devnet.telegrambot.util.TenantIdExtractor;
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
                    var wolframAlphaPlugin = new WolframAlphaBotPlugin(vertx);
                    var currencyPlugin = new CurrencyRatePlugin(new FxRatesApiService(vertx));
                    var timeZonePlugin = new TelegramTimeZonePlugin();
                    return new PluginManager<>(
                            statisticPlugin,
                            new WordLimiterPlugin(repositoryManager.buildUnsubscribeRepository()),
                            yesnoplug,
                            wolframAlphaPlugin,
                            currencyPlugin,
                            timeZonePlugin
                            );
                },
                new TenantIdExtractor()
            ),
            new IgnoreMeRepositoryImpl(ds));

        TelegramBotExecutor.newInDevMode().runBotWith(manager);
        /*if (isProd(args)) {
            TelegramBotExecutor.newInProdMode().runBotWith(manager);
        } else {
            TelegramBotExecutor.newInDevMode().runBotWith(manager);
        }*/
    }

    private static boolean isProd(String[] args) {
        if (args.length < 1) {
            return true;
        }

        return !"-dev".equals(args[0]);
    }
}
