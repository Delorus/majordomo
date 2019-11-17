package page.devnet.pluginmanager;

import java.util.List;

/**
 * @author maksim
 * @since 16.11.2019
 */
public interface TgMessageSubscriber {

    List<BotApiMethod> consume(Update update);
}
