package page.devnet.bot;

import ru.sherb.bot.BotApiMethod;
import ru.sherb.bot.Update;

import java.util.List;

/**
 * @author maksim
 * @since 23.03.19
 */
public interface BotPlugin {

    List<BotApiMethod> onUpdate(Update update);
}
