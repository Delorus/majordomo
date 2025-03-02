package page.devnet.convertercurrency.fxratesapi;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import lombok.extern.slf4j.Slf4j;
import page.devnet.common.webclient.WebClientFactory;
import page.devnet.convertercurrency.ConverterCurrencyException;
import page.devnet.convertercurrency.ConverterCurrencyService;
import page.devnet.convertercurrency.CurrencyDictionary;
import page.devnet.convertercurrency.utils.ParserCurrencyMessage;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Non-blocking currency conversion service using Vert.x WebClient.
 */
@Slf4j
public class FxRatesApiService implements ConverterCurrencyService {

    private static final int HTTP_TIMEOUT = 5000;
    private static final String API_URL = "https://api.fxratesapi.com/latest";

    private final WebClient client;
    private final ParserCurrencyMessage parserCurrencyMessage;
    private final CurrencyDictionary currencyDictionary;

    public FxRatesApiService(Vertx vertx) {
        this.client = WebClientFactory.createWebClient(vertx, HTTP_TIMEOUT);
        this.parserCurrencyMessage = new ParserCurrencyMessage();
        this.currencyDictionary = new CurrencyDictionary();
    }

    @Override
    public String convert(String from) throws ConverterCurrencyException {
        String number = parserCurrencyMessage.getValue(from);
        String currency = parserCurrencyMessage.getBaseCurrency(from);

        if (!currencyDictionary.getCurrencies().contains(currency.toUpperCase())) {
            log.error("Currency not found: {}", currency);
            throw new ConverterCurrencyException("Currency not found: " + currency, from);
        }

        String targetCurrencies = currencyDictionary.getCurrencies().stream()
                .filter(c -> !c.equalsIgnoreCase(currency))
                .collect(Collectors.joining(","));

        CompletableFuture<String> future = new CompletableFuture<>();

        client.getAbs(API_URL)
            .addQueryParam("currencies", targetCurrencies)
            .addQueryParam("base", currency.toUpperCase())
            .addQueryParam("amount", number)
            .addQueryParam("resolution", "1m")
            .addQueryParam("places", "2")
            .addQueryParam("format", "json")
            .send()
            .onSuccess(response -> {
                JsonObject json = response.bodyAsJsonObject();
                if (json.getBoolean("success", false)) {
                    JsonObject rates = json.getJsonObject("rates");
                    String date = json.getString("date");

                    StringBuilder result = new StringBuilder();
                    rates.forEach(entry -> {
                        result.append(entry.getKey())
                              .append(": ")
                              .append(entry.getValue())
                              .append("\n");
                    });
                    result.append("Данные на (UTC): ").append(date);
                    future.complete(result.toString());
                } else {
                    String error = "API request failed: " + json.getString("error", "Unknown error");
                    log.error(error);
                    future.completeExceptionally(new ConverterCurrencyException(error, from));
                }
            })
            .onFailure(error -> {
                String message = error.getMessage();
                log.error("Failed to get currency rates: {}", message);
                future.completeExceptionally(new ConverterCurrencyException(message, from));
            });

        try {
            return future.get(HTTP_TIMEOUT, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            Throwable cause = e.getCause();
            if (cause instanceof ConverterCurrencyException) {
                throw (ConverterCurrencyException) cause;
            }
            log.error("Failed to get currency rates: {}", e.getMessage());
            throw new ConverterCurrencyException(e.getMessage(), from);
        }
    }

}
