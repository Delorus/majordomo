package page.devnet.cli;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import page.devnet.cli.translate.TranslateCliPlugin;
import page.devnet.database.RepositoryManager;
import page.devnet.pluginmanager.Plugin;
import page.devnet.pluginmanager.PluginManager;
import page.devnet.wordstat.api.Statistics;

import java.io.IOException;
import java.util.Map;

@Slf4j
public class AdministrationPlugin implements Plugin<Event, String>, Commandable {

    private final String nameAdministrationPlugin = "adminPlug";

    private PluginManager plugManager;

    public AdministrationPlugin(PluginManager plugManager) {
        this.plugManager = plugManager;
    }

    @Override
    public String getPluginId() {
        return nameAdministrationPlugin;
    }

    @Override
    public String onEvent(Event event) {
        if (event.isEmpty()) {
            return "";
        }

        if (event.isCommand()) {
            try {
                return executeCommand(event);
            } catch (IOException e) {
                log.error(e.getMessage(), e);
                return "Sorry, something wrong with send chart: " + e.getMessage();
            }
        }
        return "";
    }

    private String executeCommand(Event event) throws IOException {
        String text = event.getText();
        switch (text) {
            case ":adminPlug":
                plugManager.deletePlugin(text);
                plugManager.print();
                return "";
            case ":statsPlug":
                System.out.println("test");
                plugManager.print();
                plugManager.deletePlugin("statsPlug");
                plugManager.print();
                return "";
            case ":limitPlug":
                plugManager.deletePlugin(text);
                plugManager.print();
                return "";
            case ":statPlug":
                plugManager.deletePlugin(text);
                plugManager.print();
                return "";
        }
        return "";

}


    @Override
    public String serviceName() {
        return null;
    }

    @Override
    public Map<String, String> commandDescriptionList() {
        return null;
    }
}
