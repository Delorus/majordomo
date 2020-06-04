package page.devnet.telegrambot;

import page.devnet.database.RepositoryManager;
import page.devnet.pluginmanager.PluginManager;
import page.devnet.wordstat.api.Statistics;
import page.devnet.wordstat.store.InMemoryWordStorage;

public class App {

    public static void main(String[] args) {
//        TranslateBotPlugin translatePlugin = TranslateBotPlugin.newYandexTranslatePlugin();
        var repositoryManager = new RepositoryManager();
        var statisticPlugin = new WordStatisticPlugin(new Statistics(repositoryManager.getWordStorageRepository()), repositoryManager.getUserRepository());

        var manager = new PluginManager<>(/*translatePlugin, */statisticPlugin, new WordLimiterPlugin(repositoryManager.getUnsubscribeRepository()));

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
