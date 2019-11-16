package page.devnet.pluginmanager;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author maksim
 * @since 01.03.19
 */
@Slf4j
public final class PluginManager implements TgMessageSubscriber {

    private final List<BotPlugin> plugins = new ArrayList<>();

    public PluginManager(BotPlugin plugin, BotPlugin... plugins) {
        this.plugins.add(plugin);
        this.plugins.addAll(Arrays.asList(plugins));
    }

    @Override
    public List<BotApiMethod> consume(Update update) {
        List<BotApiMethod> response = new ArrayList<>();
        for (BotPlugin plugin : plugins) {
            response.addAll(plugin.onUpdate(update));
        }

        return response;
    }
}
