package page.devnet.cli;

import lombok.extern.slf4j.Slf4j;
import page.devnet.pluginmanager.Plugin;
import page.devnet.wordstat.api.Statistics;
import page.devnet.wordstat.chart.Chart;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Map;

/**
 * @author maksim
 * @since 19.11.2019
 */
@Slf4j
public class WordStatisticCliPlugin implements Plugin<Event, String>, Commandable {

    private final Statistics statistics;

    public WordStatisticCliPlugin(Statistics statistics) {
        this.statistics = statistics;
    }

    @Override
    public String getPluginId() {
        return "statsPlug";
    }

    @Override
    public String onEvent(Event event) {
        if (event.isEmpty()) {
            return "";
        }

        if (event.isCommand()) {
            try {
                return executeCommand(event);
            } catch (IOException e) {
                log.error(e.getMessage(), e);
                return "Sorry, something wrong with send chart: " + e.getMessage();
            }
        }

        var userId = "local_user";
        var date = Instant.now();
        statistics.processText(userId, date, event.getText());

        return "";
    }

    private String executeCommand(Event event) throws IOException {
        var text = event.getText();
        switch (text) {
            case ":statf":
                var fromLastDay = ZonedDateTime.now().minusDays(1);
                Chart top10UserWordsFromLastDay = statistics.getTop10UsedWordsFrom(fromLastDay.toInstant());

                File file = Paths.get("chart.png").toFile();
                file.createNewFile();
                try (InputStream in = top10UserWordsFromLastDay.toInputStream()) {
                    Files.copy(in, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
                }

                return "file://" + file.toPath().toAbsolutePath();
            default:
                return "";
        }
    }

    @Override
    public String serviceName() {
        return "Word statistic";
    }

    @Override
    public Map<String, String> commandDescriptionList() {
        return Map.of(
                ":statf", "top 10 of most frequency word",
                ":flush", "flush temporary storage to file"
        );
    }
}
