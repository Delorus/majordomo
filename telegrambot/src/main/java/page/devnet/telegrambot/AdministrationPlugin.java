package page.devnet.telegrambot;

import lombok.Setter;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import page.devnet.pluginmanager.Plugin;
import page.devnet.pluginmanager.PluginManager;
import page.devnet.telegrambot.util.CommandUtils;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class AdministrationPlugin implements Plugin<Update, List<PartialBotApiMethod>> {

    private final String nameAdministrationPlugin = "adminPlug";

    PluginManager pluginManager;
    public AdministrationPlugin(PluginManager<Update, List<PartialBotApiMethod>> manager) {
    }

    @Override
    public String getPluginId() {
        return nameAdministrationPlugin;
    }

    private PluginManager plugManager;

    @Setter
    private CommandUtils commandUtils = new CommandUtils();

    @Override
    public List<PartialBotApiMethod> onEvent(Update event) {
        if (!event.hasMessage() || !event.getMessage().hasText()) {
            return Collections.emptyList();
        }

        if (event.getMessage().isCommand()) {
            try {
                executeCommand(event.getMessage());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return Collections.emptyList();
        }

        return null;
    }
    //TODO addplugin and other
    private List<PartialBotApiMethod> executeCommand(Message message) throws IOException {
        var text = commandUtils.normalizeCmdMsg(message.getText());
        switch (text) {
            case "adminPlug":
                pluginManager.deletePlugin(text);
                return Collections.emptyList();
            case "limitPlug":
                pluginManager.deletePlugin(text);
                return Collections.emptyList();
            case "statPlug":
                pluginManager.deletePlugin(text);
                return Collections.emptyList();
        }
        return Collections.emptyList();
    }
}
