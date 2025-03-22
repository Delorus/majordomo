package page.devnet.telegrambot.timezone;

import org.checkerframework.checker.units.qual.C;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.chat.Chat;
import org.telegram.telegrambots.meta.api.objects.message.Message;

import static org.junit.jupiter.api.Assertions.*;

class TelegramTimeZonePluginTest {
/**
    @Test
    void testTimeCommand() {
        TelegramTimeZonePlugin plugin = new TelegramTimeZonePlugin();
        
        Update update = new Update();
        Message message = new Message();
        Chat chat = Chat.builder().id(123L).build();
        message.setChat(chat);
        message.setText("/time");
        update.setMessage(message);
        
        var result = plugin.onEvent(update);
        
        assertFalse(result.isEmpty());
        assertTrue(result.get(0) instanceof SendMessage);
        SendMessage sendMessage = (SendMessage) result.get(0);
        assertEquals("123", sendMessage.getChatId());
        assertTrue(sendMessage.getText().contains("Ekaterinburg:"));
        assertTrue(sendMessage.getText().contains("Moscow:"));
        assertTrue(sendMessage.getText().contains("Tokyo:"));
    }

    @Test
    void testInvalidCommand() {
        TelegramTimeZonePlugin plugin = new TelegramTimeZonePlugin();
        
        Update update = new Update();
        Message message = new Message();
        Chat chat = Chat.builder().id(123L).build();
        message.setChat(chat);
        message.setText("/invalid");
        update.setMessage(message);
        
        var result = plugin.onEvent(update);
        
        assertFalse(result.isEmpty());
        assertTrue(result.get(0) instanceof SendMessage);
        SendMessage sendMessage = (SendMessage) result.get(0);
        assertEquals("123", sendMessage.getChatId());
        assertTrue(sendMessage.getText().contains("Use /time"));
    }
**/
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