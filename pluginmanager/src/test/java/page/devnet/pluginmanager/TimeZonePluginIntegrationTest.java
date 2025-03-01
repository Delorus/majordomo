package page.devnet.pluginmanager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import page.devnet.timezone.TimeZonePlugin;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TimeZonePluginIntegrationTest {
    private PluginManager<String, String> pluginManager;
    private TimeZonePlugin timeZonePlugin;

    @BeforeEach
    void setUp() {
        timeZonePlugin = new TimeZonePlugin();
        pluginManager = new PluginManager<>(timeZonePlugin);
    }

    @Test
    void testPluginManagerIntegration() {
        // Verify plugin is active
        List<String> activePlugins = pluginManager.getWorkPluginsName();
        assertEquals(1, activePlugins.size());
        assertTrue(activePlugins.contains("timezone"));

        // Test invalid command
        List<String> invalidResponse = pluginManager.consume("invalid");
        assertEquals(1, invalidResponse.size());
        assertTrue(invalidResponse.get(0).contains("/time"));

        // Test valid command
        List<String> timeResponse = pluginManager.consume("/time");
        assertEquals(1, timeResponse.size());
        String response = timeResponse.get(0);

        // Verify all locations are present
        assertTrue(response.contains("Ekaterinburg:"));
        assertTrue(response.contains("Almaty:"));
        assertTrue(response.contains("Wellington:"));
        assertTrue(response.contains("Moscow:"));
        assertTrue(response.contains("Orenburg:"));
        assertTrue(response.contains("Warsaw:"));
        assertTrue(response.contains("Tokyo:"));
        assertTrue(response.contains("Switzerland:"));
    }

    @Test
    void testPluginDisabling() {
        pluginManager.disablePlugin("timezone");
        List<String> response = pluginManager.consume("/time");
        assertTrue(response.isEmpty());

        // Re-enable and verify it works
        pluginManager.enablePlugin("timezone");
        response = pluginManager.consume("/time");
        assertFalse(response.isEmpty());
        assertTrue(response.get(0).contains("Moscow:"));
    }
}
