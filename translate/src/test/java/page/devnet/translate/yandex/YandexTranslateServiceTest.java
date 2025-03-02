package page.devnet.translate.yandex;

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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class YandexTranslateServiceTest {

    private static final String API_KEY = "test-api-key";
    private YandexTranslateService service;

    @Mock
    private WebClient webClient;
    @Mock
    private HttpRequest<Buffer> request;
    @Mock
    private HttpResponse<Buffer> response;

    @BeforeEach
    void setUp() {
        service = new YandexTranslateService(API_KEY) {
            @Override
            protected WebClient createWebClient() {
                return webClient;
            }
        };

        lenient().when(webClient.postAbs(anyString())).thenReturn(request);
        lenient().when(request.addQueryParam(anyString(), anyString())).thenReturn(request);
        lenient().when(request.putHeader(anyString(), anyString())).thenReturn(request);
        lenient().when(request.sendBuffer(any(Buffer.class))).thenReturn(Future.succeededFuture(response));
    }

    @Test
    void transRuToEn_Success() throws Exception {
        // Given
        String russianText = "Привет мир";
        String expectedTranslation = "Hello world";
        String jsonResponse = "{\"code\":200,\"lang\":\"ru-en\",\"text\":[\"" + expectedTranslation + "\"]}";

        when(response.statusCode()).thenReturn(200);
        when(response.bodyAsString()).thenReturn(jsonResponse);

        // When
        String result = service.transRuToEn(russianText);

        // Then
        assertEquals(expectedTranslation, result);
        verify(webClient).postAbs(contains("translate.yandex.net"));
    }

    @Test
    void transRuToEn_NonRussianText_ReturnsEmpty() throws Exception {
        // Given
        String englishText = "Hello world";

        // When
        String result = service.transRuToEn(englishText);

        // Then
        assertEquals("", result);
        verify(webClient, never()).postAbs(anyString());
    }

    @Test
    void transEnToRu_Success() throws Exception {
        // Given
        String englishText = "Hello world";
        String expectedTranslation = "Привет мир";
        String jsonResponse = "{\"code\":200,\"lang\":\"en-ru\",\"text\":[\"" + expectedTranslation + "\"]}";

        when(response.statusCode()).thenReturn(200);
        when(response.bodyAsString()).thenReturn(jsonResponse);

        // When
        String result = service.transEnToRu(englishText);

        // Then
        assertEquals(expectedTranslation, result);
        verify(webClient).postAbs(contains("translate.yandex.net"));
    }

    @Test
    void translate_ServerError_ReturnsEmpty() throws Exception {
        // Given
        String text = "Hello world";
        when(response.statusCode()).thenReturn(500);

        // When
        String result = service.transEnToRu(text);

        // Then
        assertEquals("", result);
    }

    @Test
    void translate_ApiError_ReturnsEmpty() throws Exception {
        // Given
        String text = "Hello world";
        String jsonResponse = "{\"code\":422,\"message\":\"Invalid parameter\"}";

        when(response.statusCode()).thenReturn(200);
        when(response.bodyAsString()).thenReturn(jsonResponse);

        // When
        String result = service.transEnToRu(text);

        // Then
        assertEquals("", result);
    }
}
