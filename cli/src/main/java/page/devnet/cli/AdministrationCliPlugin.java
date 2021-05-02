package page.devnet.cli;

import lombok.extern.slf4j.Slf4j;
import page.devnet.database.RepositoryManager;
import page.devnet.pluginmanager.Plugin;
import page.devnet.pluginmanager.PluginManager;

import java.io.IOException;
import java.util.Map;

@Slf4j
public class AdministrationCliPlugin implements Plugin<Event, String>, Commandable {

    private final PluginManager plugManager;

    public AdministrationCliPlugin(PluginManager plugManager, RepositoryManager repositoryManager) {
        this.plugManager = plugManager;
    }

    @Override
    public String getPluginId() {
        return "adminPlug";
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
        String message = event.getText();
        String command, namePlugin = "";

        if (message.indexOf(" ") > 0) {
            command = message.substring(0, message.indexOf(" "));
            namePlugin = message.substring(message.indexOf(" ")).trim();
        } else {
            command = message;
        }

        switch (command) {
            case ":enable":

                Plugin pluginToEnable = plugManager.getPluginById(namePlugin);
                if (pluginToEnable == null) return "please input pluginId";
                if (!plugManager.getWorkPlugins().contains(pluginToEnable)) {
                    plugManager.enablePlugin(pluginToEnable);
                    return "enable " + namePlugin;
                }
                return "plugin " + namePlugin + " now work";
            case ":disable":
                Plugin pluginToDisable = plugManager.getPluginById(namePlugin);
                if (pluginToDisable == null & namePlugin.equals("adminPlug"))
                    return "please input pluginId, adminPlug - prohibited  ";
                if (plugManager.getWorkPlugins().contains(pluginToDisable)) {
                    plugManager.disablePlugin(namePlugin);
                    return "disable " + namePlugin;
                }
                return "plugin " + namePlugin + " now isn't work";

            case ":workPlug":
                return plugManager.getWorkPlugins().toString();
            case ":allPlug":
                return plugManager.getAllPlugins().keySet().toString();
            default:
                return "";
        }

    }

    @Override
    public String serviceName() {
        return "Administration plugin";
    }

    @Override
    public Map<String, String> commandDescriptionList() {
        return Map.of(
                ":enable PLUGIN", " enable PLUGIN-must set pluginId ",
                ":disable PLUGIN", "disable PLUGIN-must set pluginId ",
                ":workPlug", "get work Plugin ",
                ":allPlug", "get all Plug"

        );
    }
}