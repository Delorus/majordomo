package page.devnet.pluginmanager;

import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author maksim
 * @since 01.03.19
 */
@Slf4j
public final class PluginManager<T, R> implements MessageSubscriber<T, R> {

    private final List<Plugin<T, R>> plugins = new ArrayList<>();
    private final List<Plugin<T, R>> pluginToDisable = new ArrayList<>();
    private final List<Plugin<T, R>> pluginToEnable = new ArrayList<>();
    private final Map<String, Plugin<T, R>> pluginsWithName = new HashMap<>();


    @SafeVarargs
    public PluginManager(Plugin<T, R> plugin, Plugin<T, R>... plugins) {
        this.plugins.add(plugin);
        this.plugins.addAll(Arrays.asList(plugins));
        this.pluginsWithName.put(plugin.getPluginId(), plugin);
        this.pluginsWithName.putAll(Arrays.stream(plugins)
                .collect(Collectors.toMap(Plugin::getPluginId, Function.identity())));
    }

    @Override
    public List<R> consume(T update) {
        List<R> response = new ArrayList<>();

        if (pluginToDisable.size() > 0) {
            plugins.removeAll(pluginToDisable);
        }

        if (pluginToEnable.size() > 0) {
            plugins.addAll(pluginToEnable);
        }

        pluginToDisable.clear();
        pluginToEnable.clear();

        for (Plugin<T, R> plugin : plugins) {
            response.add(plugin.onEvent(update));
        }

        return response;
    }

    // delete plugin by name. to safe delete use pluginToDelete list.
    public void disablePlugin(String namePlugin) {
        plugins.forEach(q -> {
            if (q.getPluginId().equals(namePlugin)) {
                pluginToDisable.add(q);
            }
        });
    }

    //Find plugin by id to add plug.
    public void enablePlugin(Plugin<T, R> plugin) {
        AtomicBoolean flag = new AtomicBoolean(false);

        if (plugin.getPluginId().equals("adminPlug")) {
            plugins.add(plugin);
            pluginsWithName.put(plugin.getPluginId(), plugin);
            return;
        }
        plugins.forEach(w -> {
            if (w.getPluginId().equals(plugin.getPluginId())) {
                flag.set(false);
                return;
            } else {
                flag.set(true);
            }
        });
        if (flag.get()) pluginToEnable.add(plugin);
    }

    public List<Plugin<T, R>> getWorkPlugins() {
        return plugins;
    }

    public Plugin<T, R> getPluginById(String pluginId) {
        return pluginsWithName.get(pluginId);
    }

    public Map<String, Plugin<T, R>> getAllPlugins() {
        return pluginsWithName;
    }
}
