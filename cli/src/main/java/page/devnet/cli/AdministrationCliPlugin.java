package page.devnet.cli;

import lombok.extern.slf4j.Slf4j;
import page.devnet.pluginmanager.Plugin;
import page.devnet.pluginmanager.PluginManager;

import java.io.IOException;
import java.util.Map;

@Slf4j
public class AdministrationCliPlugin implements Plugin<Event, String>, Commandable {

    private final PluginManager plugManager;

    public AdministrationCliPlugin(PluginManager plugManager) {
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
                return "";
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

                if (namePlugin.isEmpty()) return "please input pluginId";
                if (!plugManager.getWorkPluginsName().contains(namePlugin)) {
                    plugManager.enablePlugin(namePlugin);
                    return "enable " + namePlugin;
                }
                return "plugin " + namePlugin + " now work";
            case ":disable":
                if (namePlugin.equals("adminPlug"))
                    return "please input pluginId, adminPlug - prohibited  ";
                if (plugManager.getWorkPluginsName().contains(namePlugin)) {
                    plugManager.disablePlugin(namePlugin);
                    return "disable " + namePlugin;
                }
                return "plugin " + namePlugin + " now isn't work";

            case ":workPlug":
                return plugManager.getWorkPluginsName().toString();
            case ":allPlug":
                return plugManager.getAllPlugins();
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