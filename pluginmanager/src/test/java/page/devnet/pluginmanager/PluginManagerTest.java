package page.devnet.pluginmanager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

class PluginManagerTest {

    private static class TestPlugin implements Plugin<String, String> {
        private final String id;
        private final String response;

        TestPlugin(String id, String response) {
            this.id = id;
            this.response = response;
        }

        @Override
        public String getPluginId() {
            return id;
        }

        @Override
        public String onEvent(String event) {
            return response;
        }
    }

    private PluginManager<String, String> pluginManager;
    private Plugin<String, String> plugin1;
    private Plugin<String, String> plugin2;
    private Plugin<String, String> adminPlugin;

    @BeforeEach
    void setUp() {
        plugin1 = new TestPlugin("plugin1", "response1");
        plugin2 = new TestPlugin("plugin2", "response2");
        adminPlugin = new TestPlugin("adminPlug", "admin_response");
        pluginManager = new PluginManager<>(plugin1, plugin2);
    }

    @Test
    void testConstructorAndInitialization() {
        List<String> activePlugins = pluginManager.getWorkPluginsName();
        assertEquals(2, activePlugins.size());
        assertTrue(activePlugins.contains("plugin1"));
        assertTrue(activePlugins.contains("plugin2"));
    }

    @Test
    void testPluginDisabling() {
        pluginManager.disablePlugin("plugin1");
        List<String> response = pluginManager.consume("test");
        List<String> activePlugins = pluginManager.getWorkPluginsName();

        assertEquals(1, activePlugins.size());
        assertTrue(activePlugins.contains("plugin2"));
        assertFalse(activePlugins.contains("plugin1"));
        assertEquals(1, response.size());
        assertEquals("response2", response.get(0));
    }

    @Test
    void testPluginEnabling() {
        // First disable
        pluginManager.disablePlugin("plugin1");
        pluginManager.consume("test"); // Apply disable

        // Then enable
        pluginManager.enablePlugin("plugin1");
        List<String> response = pluginManager.consume("test");
        List<String> activePlugins = pluginManager.getWorkPluginsName();

        assertEquals(2, activePlugins.size());
        assertTrue(activePlugins.contains("plugin1"));
        assertTrue(activePlugins.contains("plugin2"));
        assertEquals(2, response.size());
    }

    @Test
    void testAdminPluginEnabling() {
        pluginManager.enableAdminPlugin(adminPlugin);
        List<String> response = pluginManager.consume("test");
        List<String> activePlugins = pluginManager.getWorkPluginsName();

        assertEquals(3, activePlugins.size());
        assertTrue(activePlugins.contains("adminPlug"));
        assertEquals(3, response.size());
    }

    @Test
    void testGetPluginById() {
        Plugin<String, String> retrieved = pluginManager.getPluginById("plugin1");
        assertEquals("plugin1", retrieved.getPluginId());
        assertEquals("response1", retrieved.onEvent("test"));
    }

    @Test
    void testGetAllPlugins() {
        String allPlugins = pluginManager.getAllPlugins();
        assertTrue(allPlugins.contains("plugin1"));
        assertTrue(allPlugins.contains("plugin2"));
    }

    @Test
    void testConsume() {
        List<String> response = pluginManager.consume("test");
        assertEquals(2, response.size());
        assertEquals("response1", response.get(0));
        assertEquals("response2", response.get(1));
    }

    @Test
    void testEnableAlreadyEnabledPlugin() {
        // Plugin1 is already enabled
        pluginManager.enablePlugin("plugin1");
        List<String> response = pluginManager.consume("test");
        List<String> activePlugins = pluginManager.getWorkPluginsName();

        // Should remain the same
        assertEquals(2, activePlugins.size());
        assertTrue(activePlugins.contains("plugin1"));
        assertEquals(2, response.size());
    }

    @Test
    void testDisableAlreadyDisabledPlugin() {
        // First disable
        pluginManager.disablePlugin("plugin1");
        pluginManager.consume("test");

        // Try to disable again
        pluginManager.disablePlugin("plugin1");
        List<String> response = pluginManager.consume("test");
        List<String> activePlugins = pluginManager.getWorkPluginsName();

        // Should remain the same
        assertEquals(1, activePlugins.size());
        assertFalse(activePlugins.contains("plugin1"));
        assertEquals(1, response.size());
    }

    @Test
    void testEnableNonAdminPluginAsAdmin() {
        Plugin<String, String> fakeAdminPlugin = new TestPlugin("plugin3", "fake_admin");
        pluginManager.enableAdminPlugin(fakeAdminPlugin);

        List<String> activePlugins = pluginManager.getWorkPluginsName();
        assertEquals(2, activePlugins.size()); // Should not be added
        assertFalse(activePlugins.contains("plugin3"));
    }

    @Test
    void testDisableNonExistentPlugin() {
        assertThrows(NullPointerException.class, () -> {
            pluginManager.disablePlugin("non_existent_plugin");
        });
    }
}
