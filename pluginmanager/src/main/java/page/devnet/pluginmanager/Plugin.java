package page.devnet.pluginmanager;

import java.util.List;

/**
 * @author maksim
 * @since 23.03.19
 */
public interface Plugin {

    List<BotApiMethod> onUpdate(Update update);
}
