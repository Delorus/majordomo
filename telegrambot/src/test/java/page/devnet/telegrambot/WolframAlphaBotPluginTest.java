package page.devnet.telegrambot;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.client.HttpRequest;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.codec.BodyCodec;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import page.devnet.common.webclient.WebClientFactory;

import java.util.List;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings({"unchecked", "rawtypes"})
class WolframAlphaBotPluginTest {
    private static final String API_URL = "http://api.wolframalpha.com/v1/result";

    @Mock
    private Vertx vertx;

    @Mock
    private WebClient webClient;

    @Mock
    private HttpRequest<Buffer> request;

    @Mock
    private HttpRequest<String> stringRequest;

    @Mock
    private HttpResponse<String> response;

    @Mock
    private Update update;

    @Mock
    private Message message;

    private WolframAlphaBotPlugin plugin;
    private MockedStatic<WebClientFactory> factory;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() {
        factory = mockStatic(WebClientFactory.class);
        factory.when(() -> WebClientFactory.createWebClient(any(Vertx.class), anyInt()))
              .thenReturn(webClient);

        // Setup the request chain
        lenient().when(webClient.getAbs(eq(API_URL))).thenReturn(request);
        lenient().when(request.addQueryParam(eq("appid"), any())).thenReturn(request);
        lenient().when(request.addQueryParam(eq("i"), any())).thenReturn(request);
        lenient().when(request.putHeader(eq("Content-Type"), eq("text/plain;charset=utf-8"))).thenReturn(request);
        lenient().when(request.as(eq(BodyCodec.string()))).thenReturn(stringRequest);
        lenient().when(stringRequest.send()).thenReturn(Future.succeededFuture(response));

        plugin = new WolframAlphaBotPlugin(vertx);
    }

    @AfterEach
    void tearDown() {
        if (factory != null) {
            factory.close();
        }
    }

    @Test
    void testWolframCommandSuccess() {
        // Given
        String query = "/wolfram 2+2";
        String expectedResult = "4";

        when(update.hasMessage()).thenReturn(true);
        when(update.getMessage()).thenReturn(message);
        when(message.hasText()).thenReturn(true);
        when(message.isCommand()).thenReturn(true);
        when(message.getText()).thenReturn(query);
        when(message.getChatId()).thenReturn(123L);

        // Setup successful response
        when(response.statusCode()).thenReturn(200);
        when(response.body()).thenReturn(expectedResult);
        doReturn(Future.succeededFuture(response)).when(stringRequest).send();

        // When
        List<PartialBotApiMethod<?>> result = plugin.onEvent(update);

        // Then
        assertFalse(result.isEmpty());
        SendMessage sendMessage = (SendMessage) result.get(0);
        assertEquals("123", sendMessage.getChatId());
        assertEquals(expectedResult, sendMessage.getText());
    }

    @Test
    void testWolframCommandError400() {
        // Given
        String query = "/wolfram invalid query";

        when(update.hasMessage()).thenReturn(true);
        when(update.getMessage()).thenReturn(message);
        when(message.hasText()).thenReturn(true);
        when(message.isCommand()).thenReturn(true);
        when(message.getText()).thenReturn(query);
        when(message.getChatId()).thenReturn(123L);

        // Setup 400 error response
        when(response.statusCode()).thenReturn(400);
        doReturn(Future.succeededFuture(response)).when(stringRequest).send();

        // When
        List<PartialBotApiMethod<?>> result = plugin.onEvent(update);

        // Then
        assertFalse(result.isEmpty());
        SendMessage sendMessage = (SendMessage) result.get(0);
        assertEquals("123", sendMessage.getChatId());
        assertTrue(sendMessage.getText().contains("Error while requesting WolframAlpha: code 400"));
    }

    @Test
    void testWolframCommandNetworkError() {
        // Given
        String query = "/wolfram 2+2";

        when(update.hasMessage()).thenReturn(true);
        when(update.getMessage()).thenReturn(message);
        when(message.hasText()).thenReturn(true);
        when(message.isCommand()).thenReturn(true);
        when(message.getText()).thenReturn(query);
        when(message.getChatId()).thenReturn(123L);

        // Override the default success response with a failure
        doReturn(Future.failedFuture(new RuntimeException("Network error"))).when(stringRequest).send();

        // When
        List<PartialBotApiMethod<?>> result = plugin.onEvent(update);

        // Then
        assertFalse(result.isEmpty());
        SendMessage sendMessage = (SendMessage) result.get(0);
        assertEquals("123", sendMessage.getChatId());
        assertTrue(sendMessage.getText().contains("Error to execute request: Network error"));
    }

    @Test
    void testWolframCommandTimeout() {
        // Given
        String query = "/wolfram 2+2";

        when(update.hasMessage()).thenReturn(true);
        when(update.getMessage()).thenReturn(message);
        when(message.hasText()).thenReturn(true);
        when(message.isCommand()).thenReturn(true);
        when(message.getText()).thenReturn(query);
        when(message.getChatId()).thenReturn(123L);

        // Override the default success response with a timeout
        doReturn(Future.failedFuture(new TimeoutException("Operation timed out"))).when(stringRequest).send();

        // When
        List<PartialBotApiMethod<?>> result = plugin.onEvent(update);

        // Then
        assertFalse(result.isEmpty());
        SendMessage sendMessage = (SendMessage) result.get(0);
        assertEquals("123", sendMessage.getChatId());
        assertTrue(sendMessage.getText().contains("Error to execute request: Operation timed out"));
    }

    @Test
    void testNonWolframCommand() {
        // Given
        String query = "/other command";

        when(update.hasMessage()).thenReturn(true);
        when(update.getMessage()).thenReturn(message);
        when(message.hasText()).thenReturn(true);
        when(message.isCommand()).thenReturn(true);
        when(message.getText()).thenReturn(query);

        // When
        List<PartialBotApiMethod<?>> result = plugin.onEvent(update);

        // Then
        assertTrue(result.isEmpty());
        verify(webClient, never()).getAbs(anyString());
    }
}
