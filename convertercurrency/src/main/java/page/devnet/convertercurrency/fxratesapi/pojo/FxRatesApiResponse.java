package page.devnet.convertercurrency.fxratesapi.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Map;

@lombok.Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class FxRatesApiResponse {
    private Boolean success;
    private Instant timestamp;
    private String baseCurrency;
    private ZonedDateTime date;
    private Map<String, Double> rates;
}
