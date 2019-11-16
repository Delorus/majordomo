package page.devnet.translate;

/**
 * @author maksim
 * @since 16.11.2019
 */
public class TranslateException extends RuntimeException {

    private static final String template = "Unexpected error: %s\n\tText to translate [%s]";

    public TranslateException(Throwable cause) {
        super(cause);
    }

    public TranslateException(Throwable cause, String text) {
        super(formatMsg(cause.getMessage(), text), cause);
    }

    public TranslateException(String message, String text) {
        super(formatMsg(message, text));
    }

    private static String formatMsg(String defaultMessage, String text) {
        return String.format(template, defaultMessage, text);
    }
}
