package page.devnet.telegrambot;

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
import org.telegram.telegrambots.meta.api.methods.botapimethods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendAnimation;
import org.telegram.telegrambots.meta.api.objects.ApiResponse;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.chat.Chat;
import org.telegram.telegrambots.meta.api.objects.message.Message;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class YesNoPluginTest {

    private YesNoPlugin plugin;
    private final String API_URL = "https://yesno.wtf/api";
    @Mock
    private WebClient webClient;
    @Mock
    HttpRequest<Buffer> apiRequest;
    @Mock
    HttpRequest<Buffer> imageRequest;
    @Mock
    HttpResponse<Buffer> apiResponse;
    @Mock
    HttpResponse<Buffer> imageResponse;
    @Mock
    Buffer imageBuffer;
    @Mock
    Update update;
    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        plugin = new YesNoPlugin() {
            @Override
            protected WebClient createWebClient() {
                return webClient;
            }
        };
        lenient().when(webClient.getAbs(anyString())).thenReturn(apiRequest);
    }

    @Test
    void onEvent_YesCommand_Success() throws JsonProcessingException {

        // Arrange
        String imageYesURL = "https://yesno.wtf/assets/yes/1.gif";

        ApiResponse apiResp = new ApiResponse("yes", true, imageYesURL);
        String apiJson = mapper.writeValueAsString(apiResp);
        byte[] imageBytes = "GIF89a".getBytes(); // минимальный gif-заголовок

        // Первичный запрос к yesno.wtf/api
        when(webClient.getAbs(API_URL)).thenReturn(apiRequest);
        when(apiRequest.addQueryParam("force", "yes")).thenReturn(apiRequest);
        when(apiRequest.send()).thenReturn(Future.succeededFuture(apiResponse));
        when(apiResponse.statusCode()).thenReturn(200);
        when(apiResponse.bodyAsString()).thenReturn(apiJson);

        // Запрос к изображению
        when(webClient.getAbs(imageYesURL)).thenReturn(imageRequest);
        when(imageRequest.send()).thenReturn(Future.succeededFuture(imageResponse));
        when(imageResponse.statusCode()).thenReturn(200);
        when(imageResponse.body()).thenReturn(imageBuffer);
        when(imageBuffer.getBytes()).thenReturn(imageBytes);


        update = createUpdateWithCommand("/yes");

        // Act
        List<PartialBotApiMethod<?>> result = plugin.onEvent(update);

        // Assert
        assertFalse(result.isEmpty());
        assertInstanceOf(SendAnimation.class,result.getFirst());
        verify(webClient).getAbs("https://yesno.wtf/api");
        verify(apiRequest).addQueryParam("force", "yes");
    }

    @Test
    void onEvent_NoCommand_Success() throws JsonProcessingException {
        // Given
        String imageNoURL = "https://yesno.wtf/assets/no/1.gif";

        // Arrange
        ApiResponse apiResp = new ApiResponse("no", true, imageNoURL);
        String apiJson = mapper.writeValueAsString(apiResp);
        byte[] imageBytes = "GIF189a".getBytes(); // минимальный gif-заголовок

        // Первичный запрос к yesno.wtf/api
        when(webClient.getAbs(API_URL)).thenReturn(apiRequest);
        when(apiRequest.addQueryParam("force", "no")).thenReturn(apiRequest);
        when(apiRequest.send()).thenReturn(Future.succeededFuture(apiResponse));
        when(apiResponse.statusCode()).thenReturn(200);
        when(apiResponse.bodyAsString()).thenReturn(apiJson);

        // Запрос к изображению
        when(webClient.getAbs(imageNoURL)).thenReturn(imageRequest);
        when(imageRequest.send()).thenReturn(Future.succeededFuture(imageResponse));
        when(imageResponse.statusCode()).thenReturn(200);
        when(imageResponse.body()).thenReturn(imageBuffer);
        when(imageBuffer.getBytes()).thenReturn(imageBytes);


        update = createUpdateWithCommand("/no");

        // Act
        List<PartialBotApiMethod<?>> result = plugin.onEvent(update);

        // Assert
        assertFalse(result.isEmpty());
        assertInstanceOf(SendAnimation.class,result.getFirst());
        verify(webClient).getAbs("https://yesno.wtf/api");
        verify(apiRequest).addQueryParam("force", "no");
    }


    @Test
    void onEvent_MaybeCommand_Success() throws JsonProcessingException {
        // Given
        String imageMaybeURL = "https://yesno.wtf/assets/maybe/1.gif";

        // Arrange
        ApiResponse apiResp = new ApiResponse("no", true, imageMaybeURL);
        String apiJson = mapper.writeValueAsString(apiResp);
        byte[] imageBytes = "GIF189a".getBytes(); // минимальный gif-заголовок

        // Первичный запрос к yesno.wtf/api
        when(webClient.getAbs(API_URL)).thenReturn(apiRequest);
        when(apiRequest.addQueryParam("force", "maybe")).thenReturn(apiRequest);
        when(apiRequest.send()).thenReturn(Future.succeededFuture(apiResponse));
        when(apiResponse.statusCode()).thenReturn(200);
        when(apiResponse.bodyAsString()).thenReturn(apiJson);

        // Запрос к изображению
        when(webClient.getAbs(imageMaybeURL)).thenReturn(imageRequest);
        when(imageRequest.send()).thenReturn(Future.succeededFuture(imageResponse));
        when(imageResponse.statusCode()).thenReturn(200);
        when(imageResponse.body()).thenReturn(imageBuffer);
        when(imageBuffer.getBytes()).thenReturn(imageBytes);


        update = createUpdateWithCommand("/maybe");

        // Act
        List<PartialBotApiMethod<?>> result = plugin.onEvent(update);

        // Assert
        assertFalse(result.isEmpty());
        assertInstanceOf(SendAnimation.class,result.getFirst());
        verify(webClient).getAbs("https://yesno.wtf/api");
        verify(apiRequest).addQueryParam("force", "maybe");
    }

    @Test
    void onEvent_ServerError_ReturnsEmptyImage() {
        // Given
        when(webClient.getAbs(API_URL)).thenReturn(apiRequest);
        when(apiRequest.addQueryParam("force", "yes")).thenReturn(apiRequest);
        when(apiRequest.send()).thenReturn(Future.succeededFuture(apiResponse));
        when(apiResponse.statusCode()).thenReturn(500);
        update = createUpdateWithCommand("/yes");
        // When
        List<PartialBotApiMethod<?>> result = plugin.onEvent(update);
        // Then
        assertTrue(result.isEmpty());
    }

    @Test
    void onEvent_InvalidCommand_ReturnsEmpty() {
        // Given

        update = createUpdateWithCommand("/invalid");

        // When
        List<PartialBotApiMethod<?>> result = plugin.onEvent(update);
        // Then
        assertTrue(result.isEmpty());
        verify(webClient, never()).getAbs(anyString());
    }

    @Test
    void onEvent_NonCommandMessage_ReturnsEmpty() {
        // Given
        update = createUpdateWithText("Hello");

        // When
        List<PartialBotApiMethod<?>> result = plugin.onEvent(update);

        // Then
        assertTrue(result.isEmpty());
        verify(webClient, never()).getAbs(anyString());
    }

    private Update createUpdateWithCommand(String command) {
        Message message = mock(Message.class);
        MessageEntity messageEntity = mock(MessageEntity.class);

        Chat chat = mock(Chat.class);
        lenient().when(chat.getId()).thenReturn(123L);
        lenient().when(chat.getType()).thenReturn("private");

        lenient().when(messageEntity.getType()).thenReturn("bot_command");
        lenient().when(messageEntity.getOffset()).thenReturn(0);
        lenient().when(messageEntity.getLength()).thenReturn(command.length());


        lenient().when(message.getChatId()).thenReturn(123L);
        lenient().when(message.getMessageId()).thenReturn(456);
        lenient().when(message.getText()).thenReturn(command);
        lenient().when(message.isCommand()).thenReturn(true);
        lenient().when(message.hasText()).thenReturn(true);
        lenient().when(message.getChat()).thenReturn(chat);
        lenient().when(message.getEntities()).thenReturn(List.of(messageEntity));

        update = mock(Update.class);
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

         update = mock(Update.class);
         lenient().when(update.getMessage()).thenReturn(message);
         lenient().when(update.hasMessage()).thenReturn(true);

         return update;
     }

    private static class ApiResponse {
        private String answer;
        private Boolean forced;
        private String image;

        public ApiResponse(String answer, Boolean forced, String image) {
            this.answer = answer;
            this.forced = forced;
            this.image = image;
        }

        public String getAnswer() {
            return answer;
        }

        public void setAnswer(String answer) {
            this.answer = answer;
        }

        public Boolean getForced() {
            return forced;
        }

        public void setForced(Boolean forced) {
            this.forced = forced;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }
    }
}


