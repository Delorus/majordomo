package page.devnet.vertxtgbot.tgapi;

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
    public void execute(Transport transport) {
        try {
            apiMessage.validate();
        } catch (TelegramApiValidationException e) {
            throw new TelegramActionException(e);
        }

        transport.sendJson(apiMessage.getMethod(), apiMessage);
    }
}
