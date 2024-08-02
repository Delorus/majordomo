package page.devnet.convertercurrency;

import lombok.extern.slf4j.Slf4j;

/**
 * @author konstantin
 * @since 01.08.24
 */

public class ConverterCurrencyException extends RuntimeException {

    private static final String template = "Unexpected error: %s\n\tText to convert: [%s]";

    public ConverterCurrencyException(Throwable cause) {
        super(cause);
    }

    public ConverterCurrencyException(Throwable cause, String text) {
        super(formatMsg(cause.getMessage(), text), cause);
    }

    public ConverterCurrencyException(String message, String text) {
        super(formatMsg(message, text));
    }

    private static String formatMsg(String defaultMessage, String text) {
        return String.format(template, defaultMessage, text);
    }
}
