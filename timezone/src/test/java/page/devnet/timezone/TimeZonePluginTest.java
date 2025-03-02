package page.devnet.timezone;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TimeZonePluginTest {
    private TimeZonePlugin plugin;

    @BeforeEach
    void setUp() {
        plugin = new TimeZonePlugin();
    }

    @Test
    void testPluginId() {
        assertEquals("timezone", plugin.getPluginId());
    }

    @Test
    void testInvalidCommand() {
        String response = plugin.onEvent("invalid");
        assertEquals(null, response);
    }

    @Test
    void testTimeCommand() {
        String response = plugin.onEvent("/time");

        // Check if all required locations are present
        assertTrue(response.contains("Ekaterinburg:"));
        assertTrue(response.contains("Almaty:"));
        assertTrue(response.contains("Wellington:"));
        assertTrue(response.contains("Moscow:"));
        assertTrue(response.contains("Orenburg:"));
        assertTrue(response.contains("Warsaw:"));
        assertTrue(response.contains("Tokyo:"));
        assertTrue(response.contains("Switzerland:"));

        // Check time format (HH:mm (z))
        String[] lines = response.split("\n");
        assertEquals(8, lines.length);
        for (String line : lines) {
            assertTrue(line.matches(".*: \\d{2}:\\d{2} \\([A-Z]+\\)"));
        }
    }

    @Test
    void testCommandCaseInsensitive() {
        String response1 = plugin.onEvent("/TIME");
        String response2 = plugin.onEvent("/time");
        assertEquals(response1.split("\n").length, response2.split("\n").length);
    }
}
