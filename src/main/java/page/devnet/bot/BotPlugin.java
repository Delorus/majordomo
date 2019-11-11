package page.devnet.bot;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

/**
 * @author maksim
 * @since 23.03.19
 */
public interface BotPlugin {

    List<BotApiMethod> onUpdate(Update update);
}
