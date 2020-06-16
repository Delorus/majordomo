package page.devnet.cli;

import page.devnet.cli.translate.TranslateCliPlugin;
import page.devnet.database.DataSource;
import page.devnet.database.RepositoryFactory;
import page.devnet.pluginmanager.PluginManager;
import page.devnet.wordstat.api.Statistics;

public class App {

    public static void main(String[] args) {
        var repositoryManager = RepositoryFactory.simple(new DataSource());

        TranslateCliPlugin translatePlugin = TranslateCliPlugin.newYandexTranslatePlugin();
        WordStatisticPlugin statisticPlugin = new WordStatisticPlugin(new Statistics(repositoryManager.buildWordStorageRepository()));

        var manager = new PluginManager<>(translatePlugin, statisticPlugin);
        manager.addPlugin(new AdministrationCliPlugin(manager, repositoryManager));

        Interpreter interpreter = new Interpreter(manager);
        interpreter.setCommands(translatePlugin, statisticPlugin);
        interpreter.run(System.in, System.out);
    }
}
