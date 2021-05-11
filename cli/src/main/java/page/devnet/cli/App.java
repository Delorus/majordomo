package page.devnet.cli;

import page.devnet.cli.translate.TranslateCliPlugin;
import page.devnet.database.RepositoryManager;
import page.devnet.pluginmanager.PluginManager;
import page.devnet.wordstat.api.Statistics;

public class App {

    public static void main(String[] args) {
        var repositoryManager = new RepositoryManager();

        TranslateCliPlugin translatePlugin = TranslateCliPlugin.newYandexTranslatePlugin();
        WordStatisticCliPlugin statisticPlugin = new WordStatisticCliPlugin(new Statistics(repositoryManager.getWordStorageRepository()));
        var manager = new PluginManager<>(translatePlugin, statisticPlugin);
        AdministrationCliPlugin administrationCliPlugin = new AdministrationCliPlugin(manager, repositoryManager);
        manager.enableAdminPlugin(administrationCliPlugin);

        Interpreter interpreter = new Interpreter(manager);
        interpreter.setCommands(translatePlugin, statisticPlugin, administrationCliPlugin);
        interpreter.run(System.in, System.out);
    }
}
