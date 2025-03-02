package page.devnet.telegrambot;

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
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import page.devnet.vertxtgbot.tgapi.SendExternalAnimation;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class YesNoPluginTest {

    private YesNoPlugin plugin;

    @Mock
    private WebClient webClient;
    @Mock
    private HttpRequest<Buffer> request;
    @Mock
    private HttpResponse<Buffer> response;

    @BeforeEach
    void setUp() {
        plugin = new YesNoPlugin() {
            @Override
            protected WebClient createWebClient() {
                return webClient;
            }
        };

        lenient().when(webClient.getAbs(anyString())).thenReturn(request);
        lenient().when(request.addQueryParam(anyString(), anyString())).thenReturn(request);
        lenient().when(request.send()).thenReturn(Future.succeededFuture(response));
    }

    @Test
    void onEvent_YesCommand_Success() {
        // Given
        String imageUrl = "https://example.com/yes.gif";
        String jsonResponse = "{\"answer\":\"yes\",\"forced\":true,\"image\":\"" + imageUrl + "\"}";

        when(response.statusCode()).thenReturn(200);
        when(response.bodyAsString()).thenReturn(jsonResponse);

        Update update = createUpdateWithCommand("/yes");

        // When
        List<PartialBotApiMethod<?>> result = plugin.onEvent(update);

        // Then
        assertFalse(result.isEmpty());
        assertTrue(result.get(0) instanceof SendExternalAnimation);
        SendExternalAnimation animation = (SendExternalAnimation) result.get(0);
        assertEquals(imageUrl, animation.getAnimationUrl());
        verify(webClient).getAbs(contains("yesno.wtf"));
        verify(request).addQueryParam("force", "yes");
    }

    @Test
    void onEvent_NoCommand_Success() {
        // Given
        String imageUrl = "https://example.com/no.gif";
        String jsonResponse = "{\"answer\":\"no\",\"forced\":true,\"image\":\"" + imageUrl + "\"}";

        when(response.statusCode()).thenReturn(200);
        when(response.bodyAsString()).thenReturn(jsonResponse);

        Update update = createUpdateWithCommand("/no");

        // When
        List<PartialBotApiMethod<?>> result = plugin.onEvent(update);

        // Then
        assertFalse(result.isEmpty());
        assertTrue(result.get(0) instanceof SendExternalAnimation);
        SendExternalAnimation animation = (SendExternalAnimation) result.get(0);
        assertEquals(imageUrl, animation.getAnimationUrl());
        verify(webClient).getAbs(contains("yesno.wtf"));
        verify(request).addQueryParam("force", "no");
    }

    @Test
    void onEvent_MaybeCommand_Success() {
        // Given
        String imageUrl = "https://example.com/maybe.gif";
        String jsonResponse = "{\"answer\":\"maybe\",\"forced\":true,\"image\":\"" + imageUrl + "\"}";

        when(response.statusCode()).thenReturn(200);
        when(response.bodyAsString()).thenReturn(jsonResponse);

        Update update = createUpdateWithCommand("/maybe");

        // When
        List<PartialBotApiMethod<?>> result = plugin.onEvent(update);

        // Then
        assertFalse(result.isEmpty());
        assertTrue(result.get(0) instanceof SendExternalAnimation);
        SendExternalAnimation animation = (SendExternalAnimation) result.get(0);
        assertEquals(imageUrl, animation.getAnimationUrl());
        verify(webClient).getAbs(contains("yesno.wtf"));
        verify(request).addQueryParam("force", "maybe");
    }

    @Test
    void onEvent_ServerError_ReturnsEmptyImage() {
        // Given
        when(response.statusCode()).thenReturn(500);
        Update update = createUpdateWithCommand("/yes");

        // When
        List<PartialBotApiMethod<?>> result = plugin.onEvent(update);

        // Then
        assertTrue(result.isEmpty());
    }

    @Test
    void onEvent_InvalidCommand_ReturnsEmpty() {
        // Given
        Update update = createUpdateWithCommand("/invalid");

        // When
        List<PartialBotApiMethod<?>> result = plugin.onEvent(update);

        // Then
        assertTrue(result.isEmpty());
        verify(webClient, never()).getAbs(anyString());
    }

    @Test
    void onEvent_NonCommandMessage_ReturnsEmpty() {
        // Given
        Update update = createUpdateWithText("Hello");

        // When
        List<PartialBotApiMethod<?>> result = plugin.onEvent(update);

        // Then
        assertTrue(result.isEmpty());
        verify(webClient, never()).getAbs(anyString());
    }

    private Update createUpdateWithCommand(String command) {
        Message message = mock(Message.class);
        Chat chat = mock(Chat.class);
        lenient().when(chat.getId()).thenReturn(123L);
        lenient().when(message.getChatId()).thenReturn(123L);
        lenient().when(message.getMessageId()).thenReturn(456);
        lenient().when(message.getText()).thenReturn(command);
        lenient().when(message.isCommand()).thenReturn(true);
        lenient().when(message.hasText()).thenReturn(true);
        lenient().when(message.getChat()).thenReturn(chat);

        Update update = mock(Update.class);
        lenient().when(update.getMessage()).thenReturn(message);
        lenient().when(update.hasMessage()).thenReturn(true);

        return update;
    }

    private Update createUpdateWithText(String text) {
        Message message = mock(Message.class);
        Chat chat = mock(Chat.class);
        lenient().when(chat.getId()).thenReturn(123L);
        lenient().when(message.getChatId()).thenReturn(123L);
        lenient().when(message.getMessageId()).thenReturn(456);
        lenient().when(message.getText()).thenReturn(text);
        lenient().when(message.isCommand()).thenReturn(false);
        lenient().when(message.hasText()).thenReturn(true);
        lenient().when(message.getChat()).thenReturn(chat);

        Update update = mock(Update.class);
        lenient().when(update.getMessage()).thenReturn(message);
        lenient().when(update.hasMessage()).thenReturn(true);

        return update;
    }
}
