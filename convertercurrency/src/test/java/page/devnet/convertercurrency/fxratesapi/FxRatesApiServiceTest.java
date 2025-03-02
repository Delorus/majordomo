package page.devnet.convertercurrency.fxratesapi;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.HttpRequest;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import page.devnet.common.webclient.WebClientFactory;
import page.devnet.convertercurrency.ConverterCurrencyException;

import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FxRatesApiServiceTest {

    @Mock
    private Vertx vertx;

    @Mock
    private WebClient webClient;

    @Mock
    private HttpRequest<Buffer> request;

    @Mock
    private HttpResponse<Buffer> response;

    private FxRatesApiService service;
    private MockedStatic<WebClientFactory> factory;

    @BeforeEach
    void setUp() {
        factory = mockStatic(WebClientFactory.class);
        factory.when(() -> WebClientFactory.createWebClient(any(Vertx.class), anyInt()))
              .thenReturn(webClient);

        lenient().when(webClient.getAbs(anyString())).thenReturn(request);
        lenient().when(request.addQueryParam(anyString(), anyString())).thenReturn(request);
        service = new FxRatesApiService(vertx);
    }

    @Test
    void testConvertSuccess() {
        // Given
        String input = "100 USD";
        JsonObject responseJson = new JsonObject()
            .put("success", true)
            .put("rates", new JsonObject()
                .put("EUR", 0.85)
                .put("GBP", 0.73))
            .put("date", "2024-03-01T12:00:00Z");

        when(request.send()).thenReturn(Future.succeededFuture(response));
        when(response.bodyAsJsonObject()).thenReturn(responseJson);

        // When
        String result = service.convert(input);

        // Then
        assertNotNull(result);
        assertTrue(result.contains("EUR: 0.85"));
        assertTrue(result.contains("GBP: 0.73"));
        assertTrue(result.contains("Данные на (UTC): 2024-03-01T12:00:00Z"));
    }

    @Test
    void testConvertFailureInvalidCurrency() {
        // Given
        String input = "100 XXX";

        // When & Then
        assertThrows(ConverterCurrencyException.class, () -> service.convert(input));
        verify(webClient, never()).getAbs(anyString());
    }

    @Test
    void testConvertFailureApiError() {
        // Given
        String input = "100 USD";
        JsonObject responseJson = new JsonObject()
            .put("success", false)
            .put("error", "API error");

        when(request.send()).thenReturn(Future.succeededFuture(response));
        when(response.bodyAsJsonObject()).thenReturn(responseJson);

        // When & Then
        ConverterCurrencyException exception = assertThrows(
            ConverterCurrencyException.class,
            () -> service.convert(input)
        );
        assertTrue(exception.getMessage().contains("API error"));
    }

    @AfterEach
    void tearDown() {
        if (factory != null) {
            factory.close();
        }
    }

    @Test
    void testConvertFailureNetworkError() {
        // Given
        String input = "100 USD";
        RuntimeException networkError = new RuntimeException("Network error");
        when(request.send()).thenReturn(Future.failedFuture(networkError));

        // When & Then
        ConverterCurrencyException exception = assertThrows(
            ConverterCurrencyException.class,
            () -> service.convert(input)
        );
        String expectedMessage = String.format("Unexpected error: %s\n\tText to convert: [%s]", "Network error", input);
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void testConvertFailureTimeout() {
        // Given
        String input = "100 USD";
        when(request.send()).thenReturn(Future.failedFuture(new TimeoutException("Operation timed out")));

        // When & Then
        ConverterCurrencyException exception = assertThrows(
            ConverterCurrencyException.class,
            () -> service.convert(input)
        );
        String expectedMessage = String.format("Unexpected error: %s\n\tText to convert: [%s]", "Operation timed out", input);
        assertEquals(expectedMessage, exception.getMessage());
    }
}
