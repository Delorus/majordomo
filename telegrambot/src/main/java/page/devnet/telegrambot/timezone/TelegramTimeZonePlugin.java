package page.devnet.telegrambot.timezone;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.botapimethods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import page.devnet.pluginmanager.Plugin;
import page.devnet.telegrambot.util.CommandUtils;
import page.devnet.telegrambot.util.ParserMessage;
import page.devnet.timezone.TimeZonePlugin;

import java.util.Collections;
import java.util.List;

@Slf4j
public class TelegramTimeZonePlugin implements Plugin<Update, List<PartialBotApiMethod<?>>> {
    private final TimeZonePlugin timeZonePlugin;
    private final CommandUtils commandUtils = new CommandUtils();

    public TelegramTimeZonePlugin() {
        log.info("Start Time Zone plugin");
        this.timeZonePlugin = new TimeZonePlugin();
    }

    @Override
    public String getPluginId() {
        return timeZonePlugin.getPluginId();
    }

    @Override
    public List<PartialBotApiMethod<?>> onEvent(Update update) {
        log.debug("timezone plugin onEvent on Thread id: {}", Thread.currentThread().threadId());
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return Collections.emptyList();
        }
        if (update.getMessage().isCommand()) {
            return executeCommand(update.getMessage());
        }
        return Collections.emptyList();
    }
    private List<PartialBotApiMethod<?>> executeCommand(Message message) {
        ParserMessage parserMessage = new ParserMessage();
        String command = commandUtils.normalizeCmdMsgWithParameter(message.getText());
        var commandParameter = parserMessage.getCommandParameterFromMessage(message.getText());
        var chatId = String.valueOf(message.getChatId());
        if (command.equals("time")) {
            try {
                String result = timeZonePlugin.onEvent(commandParameter);
                return List.of(new SendMessage(chatId, result));
            } catch (Exception e) {
                log.error("Error in execute command: {}", e.getMessage());
                return List.of(new SendMessage(chatId, e.getMessage()));
            }
        }
        return Collections.emptyList();
    }
}
