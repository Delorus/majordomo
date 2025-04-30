package page.devnet.telegrambot.timezone;

import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.client.HttpRequest;
import io.vertx.ext.web.client.WebClient;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.chat.Chat;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import page.devnet.telegrambot.YesNoPlugin;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class TelegramTimeZonePluginTest {
    private TelegramTimeZonePlugin telegramTimeZonePlugin;
    private final String API_URL = "https://yesno.wtf/api";
    @Mock
    private WebClient webClient;
    @Mock
    HttpRequest<Buffer> apiRequest;
    @Test
    void testTimeCommand() {
        TelegramTimeZonePlugin plugin = new TelegramTimeZonePlugin();

        Update update = new Update();
        Chat chat = Chat.builder().id(123L).type("private").build();
        Message message = Message.builder()
                .chat(chat)
                .text("/time")
                .entities(List.of(
                        MessageEntity.builder()
                                .type("bot_command") // Indicates this is a bot command
                                .offset(0)          // Start position of the command in the text
                                .length(5)          // Length of the command ("/convert" has 8 characters)
                                .build()
                ))
                .build();
        update.setMessage(message);

        var result = plugin.onEvent(update);

        assertFalse(result.isEmpty());
        assertInstanceOf(SendMessage.class, result.getFirst());
        SendMessage sendMessage = (SendMessage) result.getFirst();
        assertEquals("123", sendMessage.getChatId());
        assertTrue(sendMessage.getText().contains("Ekaterinburg:"));
        assertTrue(sendMessage.getText().contains("Moscow:"));
        assertTrue(sendMessage.getText().contains("Tokyo:"));
    }

    @Test
    void testInvalidCommand() {
        TelegramTimeZonePlugin plugin = new TelegramTimeZonePlugin();

        Update update = new Update();

        Chat chat = Chat.builder()
                .id(123L)
                .type("private")
                .build();
        Message message = Message.builder()
                .chat(chat)
                .text("/invalid")
                .entities(List.of(
                        MessageEntity.builder()
                                .type("bot_command") // Indicates this is a bot command
                                .offset(0)          // Start position of the command in the text
                                .length(8)          // Length of the command ("/convert" has 8 characters)
                                .build()
                )).build();
        update.setMessage(message);
        var result = plugin.onEvent(update);
        assertTrue(result.isEmpty());
        //assertTrue(result.getFirst() instanceof SendMessage);
        //SendMessage sendMessage = (SendMessage) result.getFirst();
        //assertEquals("123", sendMessage.getChatId());
        //assertTrue(sendMessage.getText().contains("Use /time"));
    }

    @Test
    void testEmptyMessage() {
        TelegramTimeZonePlugin plugin = new TelegramTimeZonePlugin();

        Update update = new Update();
        var result = plugin.onEvent(update);

        assertTrue(result.isEmpty());
    }

    @Test
    void testPluginId() {
        TelegramTimeZonePlugin plugin = new TelegramTimeZonePlugin();
        assertEquals("timezone", plugin.getPluginId());
    }
}