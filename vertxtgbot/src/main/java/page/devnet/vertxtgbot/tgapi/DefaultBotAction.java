package page.devnet.vertxtgbot.tgapi;

import io.vertx.ext.web.client.WebClient;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.exceptions.TelegramApiValidationException;

/**
 * @author maksim
 * @since 03.06.2020
 */
public class DefaultBotAction implements TelegramAction {

    private final BotApiMethod<?> apiMessage;

    DefaultBotAction(BotApiMethod<?> apiMessage) {
        assert apiMessage != null;

        this.apiMessage = apiMessage;
    }

    @Override
    public void execute(WebClient transport) {
        try {
            apiMessage.validate();
        } catch (TelegramApiValidationException e) {
            throw new TelegramActionException(e);
        }

        String url = apiMessage.getMethod();

        transport.post(url).sendJson(apiMessage, resp -> {
            if (resp.failed()) {
                throw new TelegramActionException("Failed to send " + apiMessage.getClass().getSimpleName(), resp.cause());
            }
        });
    }
}
