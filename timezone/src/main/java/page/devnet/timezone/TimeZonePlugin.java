package page.devnet.timezone;

import page.devnet.pluginmanager.Plugin;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class TimeZonePlugin implements Plugin<String, String> {
    private static final String PLUGIN_ID = "timezone";
    private static final String COMMAND = "/time";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm (z)");
    
    private final Map<String, String> zoneMapping;
    
    public TimeZonePlugin() {
        zoneMapping = new LinkedHashMap<>();
        zoneMapping.put("Ekaterinburg", "Asia/Yekaterinburg");
        zoneMapping.put("Almaty", "Asia/Almaty");
        zoneMapping.put("Wellington", "Pacific/Auckland");
        zoneMapping.put("Moscow", "Europe/Moscow");
        zoneMapping.put("Orenburg", "Asia/Yekaterinburg");  // Same timezone as Ekaterinburg
        zoneMapping.put("Warsaw", "Europe/Warsaw");
        zoneMapping.put("Tokyo", "Asia/Tokyo");
        zoneMapping.put("Switzerland", "Europe/Zurich");
    }
    
    @Override
    public String getPluginId() {
        return PLUGIN_ID;
    }
    
    @Override
    public String onEvent(String event) {
        if (!event.trim().equalsIgnoreCase(COMMAND)) {
            return "Use " + COMMAND + " to get current time in different locations";
        }
        
        return zoneMapping.entrySet().stream()
            .map(entry -> {
                ZonedDateTime time = ZonedDateTime.now(ZoneId.of(entry.getValue()));
                return String.format("%s: %s", entry.getKey(), time.format(formatter));
            })
            .collect(Collectors.joining("\n"));
    }
}