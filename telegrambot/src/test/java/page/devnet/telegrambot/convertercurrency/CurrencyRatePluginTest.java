package page.devnet.telegrambot.convertercurrency;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import page.devnet.convertercurrency.ConverterCurrencyService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CurrencyRatePluginTest {

    private CurrencyRatePlugin plugin;

    @BeforeEach
    public void setUp() {
        // Create a simple test implementation of ConverterCurrencyService
        ConverterCurrencyService testService = message -> "Test conversion result";
        plugin = new CurrencyRatePlugin(testService);
    }

    @Test
    void testCurrencyCommandWithoutArguments() {
        System.out.println("[DEBUG_LOG] Starting currency command test");
        // Prepare test data
        Update update = new Update();
        TestMessage message = new TestMessage();
        TestChat chat = new TestChat();
        chat.setId(123L);
        message.setChat(chat);
        message.setText("/currency");
        update.setMessage(message);

        System.out.println("[DEBUG_LOG] Message text: " + message.getText());
        System.out.println("[DEBUG_LOG] Message hasText: " + message.hasText());
        System.out.println("[DEBUG_LOG] Message isCommand: " + message.isCommand());

        // Execute
        var result = plugin.onEvent(update);

        // Verify
        assertEquals(1, result.size());
        SendMessage sendMessage = (SendMessage) result.get(0);
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
        TestMessage message = new TestMessage();
        TestChat chat = new TestChat();
        chat.setId(123L);
        message.setChat(chat);
        message.setText("/convert 100 USD to AED");
        update.setMessage(message);

        // Execute
        var result = plugin.onEvent(update);

        // Verify
        assertEquals(1, result.size());
        SendMessage sendMessage = (SendMessage) result.get(0);
        assertEquals("Test conversion result", sendMessage.getText());
    }
}
