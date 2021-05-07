package page.devnet.telegrambot;

import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import page.devnet.pluginmanager.Plugin;
import page.devnet.pluginmanager.PluginManager;
import page.devnet.telegrambot.util.Parser;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class AdministrationPlugin implements Plugin<Update, List<PartialBotApiMethod>> {

    private final PluginManager pluginManager;

    public AdministrationPlugin(PluginManager<Update, List<PartialBotApiMethod>> pluginManager) {
        this.pluginManager = pluginManager;
    }

    @Override
    public String getPluginId() {
        return "adminPlug";
    }

    @Override
    public List<PartialBotApiMethod> onEvent(Update event) {
        if (!event.hasMessage() || !event.getMessage().hasText()) {
            return Collections.emptyList();
        }

        if (event.getMessage().isCommand()) {
            try {
                return executeCommand(event.getMessage());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return Collections.emptyList();
        }

        return null;
    }

    private List<PartialBotApiMethod> executeCommand(Message message) throws IOException {
        Parser parserMessage = new Parser();
        var parsedCommand = parserMessage.getCommandFromMessage(message.getText());
        var commandParametr = parserMessage.getCommandParametrFromMessage(message.getText());

        switch (parsedCommand) {
            case ENABLE:
                if (commandParametr.equals("")) {
                    return List.of(new SendMessage(message.getChatId(), "please input pluginId as second parametr, example: '/enable limitPlug' " + pluginManager.getAllPlugins()).enableMarkdown(true));
                }
                Plugin pluginToEnable = pluginManager.getPluginById(commandParametr);
                pluginManager.enablePlugin(pluginToEnable);
                return List.of(new SendMessage(message.getChatId(), "Enable plugin " + pluginToEnable.getPluginId()).enableMarkdown(true));
            case DISABLE:
                if (commandParametr.equals("")) {
                    return List.of(new SendMessage(message.getChatId(), "please input pluginId as second parametr, example: '/disable limitPlug' " + pluginManager.getAllPlugins()).enableMarkdown(true));
                }
                Plugin pluginToDisable = pluginManager.getPluginById(commandParametr);
                pluginManager.disablePlugin(pluginToDisable.getPluginId());
                return List.of(new SendMessage(message.getChatId(), "Disable plugin " + pluginToDisable.getPluginId()).enableMarkdown(true));
        }
        return Collections.emptyList();
    }
}
