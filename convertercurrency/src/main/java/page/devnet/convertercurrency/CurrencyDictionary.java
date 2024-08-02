package page.devnet.convertercurrency;

import java.util.List;

/**
 * @author Konstantin Agafonov
 * @since 02.08.24
 */
//TODO add addCurrency, and remove currency by command
public class CurrencyDictionary {

    private final List<String> currencies = List.of("RUB", "USD", "EUR", "JPY", "KZT", "GEL", "NZD");

    public List<String> getCurrencies() {
        return currencies;
    }

}
