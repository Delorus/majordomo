package page.devnet.pluginmanager;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author maksim
 * @since 01.03.19
 */
@Slf4j
public final class PluginManager<T, R> implements MessageSubscriber<T, R> {

    private final List<Plugin<T, R>> plugins = new ArrayList<>();
    private final List<Plugin<T, R>> pluginToDelete = new ArrayList<>();
    private final List<Plugin<T, R>> pluginToAdd = new ArrayList<>();

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
        if (pluginToDelete.size() > 0) {
            plugins.removeAll(pluginToDelete);
        }
        if (pluginToAdd.size() > 0) {
            plugins.addAll(pluginToAdd);
        }

        pluginToDelete.clear();
        pluginToAdd.clear();
        return response;
    }

    // delete plagin by name. to safe delete use pluginToDelete list.
    public void deletePlugin(String namePlugin) {
        plugins.forEach(q -> {
            if (q.getPluginId().equals(namePlugin)) {
                pluginToDelete.add(q);
            }
        });
    }

    //Find plugin by id to add plug.
    public void addPlugin(Plugin<T, R> plugin) {
        AtomicBoolean flag = new AtomicBoolean(false);
        plugins.forEach(w -> {
            if (w.getPluginId().equals(plugin.getPluginId())) {
                flag.set(false);
                return;
            } else {
                flag.set(true);
            }
        });
        if (flag.get()) pluginToAdd.add(plugin);
    }

    public void getWorkPlug() {
        System.out.println(plugins.size());
        for (Plugin<T, R> plugin : plugins) {
            System.out.println(plugin.getPluginId());
        }
    }
}
