package page.devnet.vertxtgbot.tgapi;

import org.telegram.telegrambots.meta.api.methods.updates.GetUpdates;
import org.telegram.telegrambots.meta.exceptions.TelegramApiValidationException;

/**
 * @author maksim
 * @since 13.06.2020
 */
public class GetUpdatesAction implements TelegramAction {

    private final GetUpdates getUpdates;

    public GetUpdatesAction(GetUpdates getUpdates) {
        this.getUpdates = getUpdates;
    }

    @Override
    public void execute(Transport transport) {
        try {
            getUpdates.validate();
        } catch (TelegramApiValidationException e) {
            throw new TelegramActionException(e);
        }

        String url = "/" + getUpdates.getMethod();

        transport.sendJson(url, getUpdates);
    }
}
