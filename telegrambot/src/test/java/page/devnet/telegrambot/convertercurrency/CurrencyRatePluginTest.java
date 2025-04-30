package page.devnet.telegrambot.convertercurrency;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.client.HttpRequest;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.telegram.telegrambots.meta.api.methods.send.SendAnimation;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.chat.Chat;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import page.devnet.convertercurrency.fxratesapi.FxRatesApiService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CurrencyRatePluginTest {

    private CurrencyRatePlugin plugin;
    private static final String API_URL = "https://api.fxratesapi.com/latest";
    @Mock
    private WebClient webClient;
    @Mock
    HttpRequest<Buffer> apiRequest;
    @Mock
    HttpResponse<Buffer> apiResponse;
    private final ObjectMapper mapper = new ObjectMapper();


    @BeforeEach
    public void setUp() {
        // Create a simple test implementation of ConverterCurrencyService
        plugin = new CurrencyRatePlugin( new FxRatesApiService());
    }

    @Test
    void testConvertCommand() throws JsonProcessingException {
        String apiJson = mapper.writeValueAsString("""
                {
                  "success": true,
                  "terms": "https://fxratesapi.com/legal/terms-conditions",
                  "privacy": "https://fxratesapi.com/legal/privacy-policy",
                  "timestamp": 1746007260,
                  "date": "2025-04-30T10:01:00.000Z",
                  "base": "RUB",
                  "rates": {
                    "AED": 4.51,
                    "DZD": 163.41,
                    "EUR": 1.08,
                    "GEL": 3.39,
                    "JPY": 175.94,
                    "KZT": 630.25,
                    "NZD": 2.07,
                    "USD": 1.23
                  }
                }
                """);

        when(webClient.getAbs(API_URL)).thenReturn(apiRequest);
        when(apiRequest.addQueryParam("currencies", "yes")).thenReturn(apiRequest);
        when(apiRequest.addQueryParam("base", "RUB")).thenReturn(apiRequest);
        when(apiRequest.addQueryParam("amount", "100")).thenReturn(apiRequest);
        when(apiRequest.addQueryParam("resolution", "1m")).thenReturn(apiRequest);
        when(apiRequest.addQueryParam("places", "2")).thenReturn(apiRequest);
        when(apiRequest.addQueryParam("format", "json")).thenReturn(apiRequest);
        when(apiRequest.send()).thenReturn(Future.succeededFuture(apiResponse));
        when(apiResponse.statusCode()).thenReturn(200);
        when(apiResponse.bodyAsString()).thenReturn(apiJson);
        // Prepare test data
        Update update = new Update();
        Chat chat = Chat.builder()
                .id(123L)
                .type("private")
                .build();
        Message message = Message.builder()
                .messageId(1)
                .chat(chat)
                .text("/convert 100 RUB")
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
        assertInstanceOf(SendMessage.class,result.getFirst());
        SendMessage sendMessage = (SendMessage) result.getFirst();
        String responseText = sendMessage.getText();

        assertTrue(responseText.contains("AED"));
        assertTrue(responseText.contains("DZD"));
        assertTrue(responseText.contains("EUR"));
        assertTrue(responseText.contains("GEL"));
        assertTrue(responseText.contains("JPY"));
        assertTrue(responseText.contains("KZT"));
        assertTrue(responseText.contains("NZD"));
        assertTrue(responseText.contains("USD"));

    }
}
