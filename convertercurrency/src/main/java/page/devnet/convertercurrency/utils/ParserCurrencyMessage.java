package page.devnet.convertercurrency.utils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ParserCurrencyMessage {
    private static final String DIGITAL_NUMBER_PATTERN = "\\D";
    private static final String WORD_PATTERN ="[^a-zA-Z]";

    public String getBaseCurrency(String from) {
        return from.replaceAll(WORD_PATTERN, "").replace(" ", "").trim();
    }

    public String getValue(String from) {
        return from.replaceAll(DIGITAL_NUMBER_PATTERN, "").replace(" ", "").trim();
    }
}
