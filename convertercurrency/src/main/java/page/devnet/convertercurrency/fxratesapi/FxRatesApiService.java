package page.devnet.convertercurrency.fxratesapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import page.devnet.convertercurrency.ConverterCurrencyException;
import page.devnet.convertercurrency.ConverterCurrencyService;
import page.devnet.convertercurrency.CurrencyDictionary;
import page.devnet.convertercurrency.fxratesapi.pojo.FxRatesApiResponse;
import page.devnet.convertercurrency.utils.ParserCurrencyMessage;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

/**
 * @author Konstantin Agafonov
 * @since 01.08.24
 */
@Slf4j
public class FxRatesApiService implements ConverterCurrencyService {

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    private final URI appCurrencyApiUrl;
    private final HttpClient client;
    private final ParserCurrencyMessage parserCurrencyMessage = new ParserCurrencyMessage();
    private final CurrencyDictionary currencyDictionary = new CurrencyDictionary();
    private static final int HTTP_TIMEOUT = 5000;

    public FxRatesApiService() {
        try {
            this.appCurrencyApiUrl = new URIBuilder(URI.create("https://api.fxratesapi.com/latest"))
                    .addParameter("resolution", "1m")
                    .addParameter("places", "2")
                    .addParameter("format", "json")
                    .build();
        } catch (URISyntaxException e) {
            log.error(e.getMessage(), e);
            throw new ConverterCurrencyException(e);
        }

        RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(HTTP_TIMEOUT)
                .setConnectionRequestTimeout(HTTP_TIMEOUT)
                .setSocketTimeout(HTTP_TIMEOUT)
                .build();
        this.client = HttpClientBuilder.create()
                .setDefaultRequestConfig(config)
                .build();
    }

    @Override
    public String convert(String from) throws ConverterCurrencyException {
        String number = parserCurrencyMessage.getValue(from);
        String currency = parserCurrencyMessage.getBaseCurrency(from);
        if (currencyDictionary.getCurrencies().contains(currency.toUpperCase())) {
            URI uri;
            try {
                uri = new URIBuilder(appCurrencyApiUrl)
                        .addParameter("currencies", currencyDictionary.getCurrencies().stream()
                                .filter(s -> !s.equalsIgnoreCase(currency))
                                .toList()
                                .toString()
                                .replace("[", "")
                                .replace("]", "")
                        )
                        .addParameter("base", currency.toUpperCase())
                        .addParameter("amount", number)
                        .build();
            } catch (URISyntaxException e) {
                log.error(e.getMessage(), e);
                throw new ConverterCurrencyException(e, from);
            }
            return requestResponse(uri, from);
        } else {
            log.error("FxRatesApiService convert error: Currency not found");
            throw new ConverterCurrencyException("Не найдена валюта из сообщения: ", from);
        }

    }

    private String requestResponse(URI uri, String from) throws ConverterCurrencyException {
        HttpGet get = new HttpGet(uri);
        try (CloseableHttpResponse response = (CloseableHttpResponse) client.execute(get)) {
            switch (response.getStatusLine().getStatusCode()) {
                case HttpStatus.SC_OK -> {return parseResponseBodyIfSuccess(response, from);}
                case HttpStatus.SC_BAD_REQUEST -> throw new ConverterCurrencyException("FxRatesApiService convert request error: Bad request", from);
                case HttpStatus.SC_NOT_FOUND -> throw new ConverterCurrencyException("FxRatesApiService convert request error: Not found", from);
                default -> {
                    log.error("FxRatesApiService convert request error: " + response.getStatusLine().getStatusCode());
                    throw new ConverterCurrencyException("FxRatesApiService convert request error: " + response.getStatusLine().getStatusCode(), from);
                }
            }
        } catch (Exception e) {
            log.error("FxRatesApiService convert request error: " + e.getMessage());
            throw new ConverterCurrencyException(e, from);
        }
    }

    private String parseResponseBodyIfSuccess(CloseableHttpResponse response, String from) throws IOException {
        String json = EntityUtils.toString(response.getEntity());
        FxRatesApiResponse fxRatesApiResponse = objectMapper.readValue(json, FxRatesApiResponse.class);
        StringBuilder builder = new StringBuilder();
        if (Boolean.TRUE.equals(fxRatesApiResponse.getSuccess())) {
            for (Map.Entry<String, Double> entry : fxRatesApiResponse.getRates().entrySet()) {
                builder.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
            }
            return builder.append("Данные на (UTC): ").append(fxRatesApiResponse.getDate()).toString();
        } else {
            throw new ConverterCurrencyException("FxRatesApiService convert request error: success is false", from);
        }
    }

}
