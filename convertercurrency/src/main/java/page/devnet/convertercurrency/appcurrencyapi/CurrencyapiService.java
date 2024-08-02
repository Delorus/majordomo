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
import org.apache.http.protocol.HTTP;
import page.devnet.convertercurrency.ConverterCurrencyException;
import page.devnet.convertercurrency.ConverterCurrencyService;
import page.devnet.convertercurrency.utils.ParserCurrencyMessage;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

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

    private static final List<String> currencys = Arrays.asList("RUB", "USD", "EUR", "JPY", "KZT", "GEL", "NZD");

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
                .setConnectTimeout(5000)
                .setConnectionRequestTimeout(5000)
                .setSocketTimeout(5000).build();
        this.client = HttpClientBuilder.create()
                .setDefaultRequestConfig(config)
                .build();
    }

    @Override
    public String convert(String from) {
        String number = parserCurrencyMessage.getValue(from);
        String currency = parserCurrencyMessage.getBaseCurrency(from);
        if (currencys.contains(currency.toUpperCase())) {
            URI uri;
            try {
                uri = new URIBuilder(appCurrencyapiUrl)
                        .addParameter("currencies", currencys.stream().filter(s -> s.equalsIgnoreCase(currency)).toList().toString())
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
                    default -> {
                        log.error("Wolfram Alpha error: " + response.getStatusLine().getStatusCode());
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
/*
{
  "meta": {
    "last_updated_at": "2024-07-31T23:59:59Z"
  },
  "data": {
    "CAD": {
      "code": "CAD",
      "value": 1.3806402145
    },
    "EUR": {
      "code": "EUR",
      "value": 0.9238401291
    },
    "USD": {
      "code": "USD",
      "value": 1
    }
  }
}
 */
/**
 * {
 *   "meta": {
 *     "last_updated_at": "2024-07-31T23:59:59Z"
 *   },
 *   "data": {
 *     "CAD": {
 *       "code": "CAD",
 *       "value": 1.3806402145
 *     },
 *     "EUR": {
 *       "code": "EUR",
 *       "value": 0.9238401291
 *     },
 *     "USD": {
 *       "code": "USD",
 *       "value": 1
 *     }
 *   }
 * }
 */
