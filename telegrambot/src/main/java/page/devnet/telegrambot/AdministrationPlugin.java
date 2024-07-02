package page.devnet.telegrambot;

import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import page.devnet.pluginmanager.Plugin;
import page.devnet.pluginmanager.PluginManager;
import page.devnet.telegrambot.util.ParserMessage;

import java.util.Collections;
import java.util.List;

public class AdministrationPlugin implements Plugin<Update, List<PartialBotApiMethod<?>>> {

    private final PluginManager pluginManager;

    public AdministrationPlugin(PluginManager<Update, List<PartialBotApiMethod<?>>> pluginManager) {
        this.pluginManager = pluginManager;
    }

    @Override
    public String getPluginId() {
        return "adminPlug";
    }

    @Override
    public List<PartialBotApiMethod<?>> onEvent(Update event) {
        if (!event.hasMessage() || !event.getMessage().hasText()) {
            return Collections.emptyList();
        }

        if (event.getMessage().isCommand()) {
            return executeCommand(event.getMessage());
        }

        return Collections.emptyList();
    }

    private List<PartialBotApiMethod<?>> executeCommand(Message message) {
        ParserMessage parserMessage = new ParserMessage();
        var parsedCommand = parserMessage.getCommandFromMessage(message.getText());
        var commandParameter = parserMessage.getCommandParameterFromMessage(message.getText());

        var chatId = String.valueOf(message.getChatId());
        switch (parsedCommand) {
            case ENABLE:
                if (commandParameter.isEmpty()) {
                    return List.of(SendMessage.builder()
                            .chatId(chatId)
                            .text("please input pluginId as second parameter, example: '/enable limitPlug' " + pluginManager.getAllPlugins())
                            .parseMode(ParseMode.MARKDOWN)
                            .build());
                }
                var pluginToEnable = pluginManager.getPluginById(commandParameter);
                pluginManager.enablePlugin(commandParameter);
                return List.of(SendMessage.builder()
                            .chatId(chatId)
                            .text("Enable plugin " + pluginToEnable.getPluginId())
                            .parseMode(ParseMode.MARKDOWN)
                            .build());
            case DISABLE:
                if (commandParameter.isEmpty()) {
                    return List.of(SendMessage.builder()
                            .chatId(chatId)
                            .text("please input pluginId as second parameter, example: '/disable limitPlug' " + pluginManager.getAllPlugins())
                            .parseMode(ParseMode.MARKDOWN)
                            .build());
                }
                if (commandParameter.equals("adminPlug")) {
                    return List.of(SendMessage.builder()
                            .chatId(chatId)
                            .text("please input pluginId, adminPlug - prohibited")
                            .parseMode(ParseMode.MARKDOWN)
                            .build());
                }
                pluginManager.disablePlugin(commandParameter);
                return List.of(new SendMessage(chatId, "Disable plugin " + commandParameter));
            case WORKPLUG:
                return List.of(new SendMessage(chatId,pluginManager.getWorkPluginsName().toString()));
            case ALLPLUG:
                return List.of(new SendMessage(chatId,pluginManager.getAllPlugins()));
            default:
                return Collections.emptyList();
        }
    }
}
