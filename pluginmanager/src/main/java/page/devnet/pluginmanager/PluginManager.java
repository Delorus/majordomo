package page.devnet.pluginmanager;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private final Map<String, PluginsWithName> pluginsWithName = new HashMap<>();


    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    private class PluginsWithName {
        Plugin<T, R> plugin;
        boolean isActive;

    }

    @SafeVarargs
    public PluginManager(Plugin<T, R> plugin, Plugin<T, R>... plugins) {
        this.plugins.add(plugin);
        this.plugins.addAll(Arrays.asList(plugins));
        this.pluginsWithName.put(plugin.getPluginId(), new PluginsWithName(plugin, true));
        this.pluginsWithName.putAll(Arrays.stream(plugins)
                .collect(Collectors.toMap(Plugin::getPluginId, p -> new PluginsWithName(p, true))));
    }

    @Override
    public List<R> consume(T update) {
        List<R> response = new ArrayList<>();

        if (pluginToDisable.size() > 0) {
            plugins.removeAll(pluginToDisable);
            pluginToDisable.forEach(disabled -> pluginsWithName.get(disabled.getPluginId()).setActive(false));
        }

        if (pluginToEnable.size() > 0) {
            plugins.addAll(pluginToEnable);
            pluginToEnable.forEach(enabled -> pluginsWithName.get(enabled.getPluginId()).setActive(true));
        }

        pluginToDisable.clear();
        pluginToEnable.clear();

        for (Plugin<T, R> plugin : plugins) {
            response.add(plugin.onEvent(update));
        }

        return response;
    }

    // delete plugin by name. to safe delete use pluginToDelete list.
    public void disablePlugin(String namePluginToDisable) {
        if (pluginsWithName.get(namePluginToDisable).isActive) {
            pluginToDisable.add(pluginsWithName.get(namePluginToDisable).getPlugin());
        }
    }

    //Find plugin by id to add plug.
    public void enablePlugin(String namePluginToEnable) {
        if (!pluginsWithName.get(namePluginToEnable).isActive) {
            pluginToEnable.add(pluginsWithName.get(namePluginToEnable).getPlugin());
            pluginsWithName.get(namePluginToEnable).setActive(true);
        }
    }

    public void enableAdminPlugin(Plugin<T, R> adminPlugin) {
        if (adminPlugin.getPluginId().equals("adminPlug")) {
            plugins.add(adminPlugin);
            pluginsWithName.put(adminPlugin.getPluginId(), new PluginsWithName(adminPlugin, true));
        }
    }

    public List<String> getWorkPluginsName() {
        List<String> workPlugins = new ArrayList<>();
        pluginsWithName.forEach((namePlug, pluginsWithName) -> {
            if (pluginsWithName.isActive) {
                workPlugins.add(namePlug);
            }
        });
        return workPlugins;
    }

    public Plugin<T, R> getPluginById(String pluginId) {
        return pluginsWithName.get(pluginId).getPlugin();
    }

    //TODO toString realization to String join
    public String getAllPlugins() {
        return pluginsWithName.keySet().toString();
    }
}
