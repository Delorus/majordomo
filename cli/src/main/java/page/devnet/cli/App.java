package page.devnet.cli;

import page.devnet.cli.translate.TranslateCliPlugin;
import page.devnet.database.RepositoryManager;
import page.devnet.pluginmanager.PluginManager;
import page.devnet.wordstat.api.Statistics;

public class App {

    public static void main(String[] args) {
        var repositoryManager = new RepositoryManager();

        TranslateCliPlugin translatePlugin = TranslateCliPlugin.newYandexTranslatePlugin();
        WordStatisticPlugin statisticPlugin = new WordStatisticPlugin(new Statistics(repositoryManager.getWordStorageRepository()));
        var manager = new PluginManager<>(translatePlugin, statisticPlugin);
        manager.addPlugin(new AdministrationPlugin(manager));

        Interpreter interpreter = new Interpreter(manager);
        interpreter.setCommands(translatePlugin, statisticPlugin);
        interpreter.run(System.in, System.out);
    }
}
