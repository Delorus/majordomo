package page.devnet.telegrambot;

import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import page.devnet.pluginmanager.Plugin;
import page.devnet.pluginmanager.PluginManager;
import page.devnet.telegrambot.util.ParserMessage;

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

        return Collections.emptyList();
    }

    private List<PartialBotApiMethod> executeCommand(Message message) throws IOException {
        ParserMessage parserMessage = new ParserMessage();
        var parsedCommand = parserMessage.getCommandFromMessage(message.getText());
        var commandParameter = parserMessage.getCommandParameterFromMessage(message.getText());

        switch (parsedCommand) {
            case ENABLE:
                if (commandParameter.equals("")) {
                    return List.of(new SendMessage(message.getChatId(), "please input pluginId as second parameter, example: '/enable limitPlug' " + pluginManager.getAllPlugins()).enableMarkdown(true));
                }
                var pluginToEnable = pluginManager.getPluginById(commandParameter);
                pluginManager.enablePlugin(commandParameter);
                return List.of(new SendMessage(message.getChatId(), "Enable plugin " + pluginToEnable.getPluginId()).enableMarkdown(true));
            case DISABLE:
                if (commandParameter.equals("")) {
                    return List.of(new SendMessage(message.getChatId(), "please input pluginId as second parametr, example: '/disable limitPlug' " + pluginManager.getAllPlugins()).enableMarkdown(true));
                }
                if (commandParameter.equals("adminPlug")) {
                    return List.of(new SendMessage(message.getChatId(),"please input pluginId, adminPlug - prohibited ").enableMarkdown(true));
                }
                pluginManager.disablePlugin(commandParameter);
                return List.of(new SendMessage(message.getChatId(), "Disable plugin " + commandParameter).enableMarkdown(true));
            case WORKPLUG:
                return List.of(new SendMessage(message.getChatId(),pluginManager.getWorkPluginsName().toString()).enableMarkdown(true));
            case ALLPLUG:
                return List.of(new SendMessage(message.getChatId(),pluginManager.getAllPlugins().keySet().toString()).enableMarkdown(true));
            default:
                return Collections.emptyList();
        }
    }
}
