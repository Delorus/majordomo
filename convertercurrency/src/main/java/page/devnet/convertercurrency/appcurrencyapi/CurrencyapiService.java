package page.devnet.convertercurrency.appcurrencyapi;

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
import page.devnet.convertercurrency.ConverterCurrencyException;
import page.devnet.convertercurrency.ConverterCurrencyService;
import page.devnet.convertercurrency.CurrencyDictionary;
import page.devnet.convertercurrency.utils.ParserCurrencyMessage;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author Konstantin Agafonov
 * @since 01.08.24
 */
@Slf4j
//TODO
public class CurrencyapiService implements ConverterCurrencyService {

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    private final URI appCurrencyapiUrl;
    private final HttpClient client;
    private final ParserCurrencyMessage parserCurrencyMessage = new ParserCurrencyMessage();
    private final CurrencyDictionary currencyDictionary = new CurrencyDictionary();
    private static final int HTTP_TIMEOUT = 5000;
    public CurrencyapiService(String apiKey) {
        try {
            this.appCurrencyapiUrl = new URIBuilder(URI.create("https://api.currencyapi.com/v3/latest"))
                    .addParameter("key", apiKey)
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
    public String convert(String from) {
        String number = parserCurrencyMessage.getValue(from);
        String currency = parserCurrencyMessage.getBaseCurrency(from);
        if (currencyDictionary.getCurrencies().contains(currency.toUpperCase())) {
            URI uri;
            try {
                uri = new URIBuilder(appCurrencyapiUrl)
                        .addParameter("currencies", currencyDictionary.getCurrencies().stream()
                                .filter(s -> s.equalsIgnoreCase(currency))
                                .toList()
                                .toString())
                        .build();
            } catch (URISyntaxException e) {
                log.error(e.getMessage(), e);
                throw new ConverterCurrencyException(e, from);
            }

            HttpGet get = new HttpGet(uri);
            log.info("Currencyapi request");
            try (CloseableHttpResponse response = (CloseableHttpResponse) client.execute(get)) {
                switch (response.getStatusLine().getStatusCode()) {
                    case HttpStatus.SC_OK -> {
                        return number;
                    }
                    case HttpStatus.SC_BAD_REQUEST -> throw new ConverterCurrencyException("FxRatesApiService convert request error: Bad request", from);
                    default -> {
                        log.error("Currency api service error: {}", response.getStatusLine().getStatusCode());
                        break;
                    }
                }
            }catch (Exception e){
                log.error(e.getMessage(), e);
            }

        }
        return "";
    }
}
