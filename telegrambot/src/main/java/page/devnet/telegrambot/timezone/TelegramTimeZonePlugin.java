package page.devnet.telegrambot.timezone;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import page.devnet.pluginmanager.Plugin;
import page.devnet.timezone.TimeZonePlugin;

import java.util.Collections;
import java.util.List;

@Slf4j
public class TelegramTimeZonePlugin implements Plugin<Update, List<PartialBotApiMethod<?>>> {
    private final TimeZonePlugin timeZonePlugin;

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

        Message message = update.getMessage();
        String chatId = String.valueOf(message.getChatId());
        String text = message.getText().trim();

        // Only process messages that start with "/"
        if (!text.startsWith("/")) {
            return Collections.emptyList();
        }

        String response = timeZonePlugin.onEvent(text);
        if (response != null && !response.isEmpty()) {
            return List.of(new SendMessage(chatId, response));
        }

        return Collections.emptyList();
    }
}
