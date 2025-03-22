package page.devnet.telegrambot.convertercurrency;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.chat.Chat;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import page.devnet.convertercurrency.ConverterCurrencyService;
import page.devnet.convertercurrency.fxratesapi.FxRatesApiService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CurrencyRatePluginTest {

    private CurrencyRatePlugin plugin;

    @BeforeEach
    public void setUp() {
        // Create a simple test implementation of ConverterCurrencyService
        //ConverterCurrencyService testService = message -> "Test conversion result";
        plugin = new CurrencyRatePlugin(new FxRatesApiService());
    }

    @Test
    void testCurrencyCommandWithoutArguments() {
        System.out.println("[DEBUG_LOG] Starting currency command test");
        // Prepare test data

        Chat chat = Chat.builder()
                .id(123L)
                .type("private")
                .build();
        Message message = Message.builder()
                .messageId(12)
                .chat(chat)
                .text("/convert")
                .entities(List.of(
                        MessageEntity.builder()
                                .type("bot_command") // Indicates this is a bot command
                                .offset(0)          // Start position of the command in the text
                                .length(8)          // Length of the command ("/convert" has 8 characters)
                                .build()
                )).build();
        Update update = new Update();
        update.setMessage(message);
        update.setUpdateId(1);

        System.out.println("[DEBUG_LOG] Message text: " + message.getText());
        System.out.println("[DEBUG_LOG] Message hasText: " + message.hasText());
        System.out.println("[DEBUG_LOG] Message isCommand: " + message.isCommand());

        // Execute
        var result = plugin.onEvent(update);

        // Verify
        assertEquals(1, result.size());
        SendMessage sendMessage = (SendMessage) result.getFirst();
        String responseText = sendMessage.getText();

        // Verify that response contains all currencies in Russian
        assertTrue(responseText.contains("Поддерживаемые валюты:"));
        assertTrue(responseText.contains("RUB - Российский рубль"));
        assertTrue(responseText.contains("USD - Доллар США"));
        assertTrue(responseText.contains("EUR - Евро"));
        assertTrue(responseText.contains("AED - Дирхам ОАЭ"));
        assertTrue(responseText.contains("DZD - Алжирский динар"));
    }

    @Test
    void testConvertCommand() {
        // Prepare test data
        Update update = new Update();
        Chat chat = Chat.builder()
                .id(123L)
                .type("private")
                .build();
        Message message = Message.builder()
                .messageId(1)
                .chat(chat)
                .text("/convert 100 USD")
                .entities(List.of(
                        MessageEntity.builder()
                                .type("bot_command") // Indicates this is a bot command
                                .offset(0)          // Start position of the command in the text
                                .length(8)          // Length of the command ("/convert" has 8 characters)
                                .build()
                )).build();
        update.setMessage(message);
        update.setUpdateId(1);
        // Execute
        var result = plugin.onEvent(update);

        // Verify
        assertEquals(1, result.size());
        SendMessage sendMessage = (SendMessage) result.getFirst();
        //TODO refactor
        //assertEquals("Test conversion result", sendMessage.getText());
    }
}
