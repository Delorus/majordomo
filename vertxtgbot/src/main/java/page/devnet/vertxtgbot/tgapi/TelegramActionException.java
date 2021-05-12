package page.devnet.vertxtgbot.tgapi;

/**
 * @author maksim
 * @since 03.06.2020
 */
public class TelegramActionException extends RuntimeException {

    public TelegramActionException(String message) {
        super(message);
    }

    public TelegramActionException(String message, Throwable cause) {
        super(message, cause);
    }

    public TelegramActionException(Throwable cause) {
        super(cause);
    }

}
