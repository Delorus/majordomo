package page.devnet.bot;

import java.util.List;

/**
 * @author maksim
 * @since 23.03.19
 */
public interface BotPlugin {

    List<BotApiMethod> onUpdate(Update update);
}
