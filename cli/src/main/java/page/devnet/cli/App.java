package page.devnet.cli;

import page.devnet.cli.translate.TranslateCliPlugin;
import page.devnet.pluginmanager.PluginManager;
import page.devnet.wordstat.api.Statistics;
import page.devnet.wordstat.store.InMemoryWordStorage;

public class App {

    public static void main(String[] args) {
        TranslateCliPlugin translatePlugin = TranslateCliPlugin.newYandexTranslatePlugin();
        WordStatisticPlugin statisticPlugin = new WordStatisticPlugin(new Statistics(new InMemoryWordStorage()));

        var manager = new PluginManager<>(translatePlugin, statisticPlugin);

        Interpreter interpreter = new Interpreter(manager);
        interpreter.setCommands(translatePlugin, statisticPlugin);
        interpreter.run(System.in, System.out);
    }
}
