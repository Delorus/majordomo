package page.devnet.cli;

import page.devnet.cli.translate.TranslateCliPlugin;
import page.devnet.pluginmanager.PluginManager;

public class App {

    public static void main(String[] args) {
        TranslateCliPlugin translatePlugin = TranslateCliPlugin.newYandexTranslatePlugin();

        PluginManager manager = new PluginManager(translatePlugin);

        Interpreter interpreter = new Interpreter(manager);
        interpreter.setCommands(translatePlugin);
        interpreter.run(System.in, System.out);
    }
}
