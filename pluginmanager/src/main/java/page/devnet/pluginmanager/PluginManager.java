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
public final class PluginManager<T, R> implements MessageSubscriber<T, R> {

    private final List<Plugin<T, R>> plugins = new ArrayList<>();

    @SafeVarargs
    public PluginManager(Plugin<T, R> plugin, Plugin<T, R>... plugins) {
        this.plugins.add(plugin);
        this.plugins.addAll(Arrays.asList(plugins));
    }

    @Override
    public List<R> consume(T update) {
        List<R> response = new ArrayList<>();
        for (Plugin<T, R> plugin : plugins) {
            response.add(plugin.onEvent(update));
        }
        return response;
    }

    public void deletePlugin(String namePlugin) {
        System.out.println(namePlugin);
        List<Plugin<T, R>> plugins1 = new ArrayList<>();
        plugins.forEach(Plug -> {
            if (!Plug.getPluginId().equals(namePlugin)) {
                plugins1.add(Plug);
            }
        });
        plugins.clear();
        plugins.addAll(plugins1);

    }

    public void addPlugin(Plugin<T, R> plugin) {
        this.plugins.add(plugin);
    }

    public void print() {
        System.out.println(plugins.size() + " " + plugins);
    }
}
