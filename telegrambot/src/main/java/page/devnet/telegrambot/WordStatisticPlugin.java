package page.devnet.telegrambot;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import page.devnet.pluginmanager.Plugin;
import page.devnet.wordstat.api.Statistics;
import page.devnet.wordstat.chart.Chart;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.PosixFilePermissions;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static java.nio.file.attribute.PosixFilePermission.OWNER_READ;
import static java.nio.file.attribute.PosixFilePermission.OWNER_WRITE;

/**
 * @author maksim
 * @since 19.11.2019
 */
@Slf4j
public class WordStatisticPlugin implements Plugin<Update, List<PartialBotApiMethod>> {

    private final Statistics statistics;
    private final Instant startTime;

    public WordStatisticPlugin(Statistics statistics, Instant startTime) {
        this.statistics = statistics;
        this.startTime = startTime;
    }

    @Override
    public List<PartialBotApiMethod> onEvent(Update event) {
        if (isBeforeStart(event.getMessage())) {
            try {
                return List.of(new SendMessage(event.getMessage()
                        .getChatId(), "Команда пришла раньше старта приложения:" + startTime));
            } catch (NullPointerException ignored) { }
        }
        if (!event.hasMessage()) {
            return Collections.emptyList();
        }

        if (event.getMessage().isCommand()) {
            try {
                return executeCommand(event.getMessage());
            } catch (IOException e) {
                log.error(e.getMessage(), e);
                return List.of(new SendMessage(event.getMessage()
                        .getChatId(), "Sorry, something wrong with send chart: " + e.getMessage()));
            }
        }

        if (!event.getMessage().hasText()) {
            return Collections.emptyList();
        }

        var message = event.getMessage();
        var userId = message.getFrom().getId();
        var date = Instant.ofEpochSecond(message.getDate());
        statistics.processText(String.valueOf(userId), date, message.getText());

        return Collections.emptyList();
    }

    private boolean isBeforeStart(Message message) {
        return Instant.ofEpochSecond(message.getDate()).isBefore(startTime);
    }

    private List<PartialBotApiMethod> executeCommand(Message message) throws IOException {
        var text = normalizeCmdMsg(message.getText());
        switch (text) {
            case "statf":
                var fromLastDay = ZonedDateTime.now().minusDays(1);
                Chart top10UserWordsFromLastDay = statistics.getTop10UserWordsFrom(fromLastDay.toInstant());
                SendPhoto sendPhoto = new SendPhoto();
                sendPhoto.setChatId(message.getChatId());

                sendPhoto.setPhoto("Word frequency in one day", top10UserWordsFromLastDay.toInputStream());
                return List.of(sendPhoto);
            case "flush":
                List<String> all = statistics.flushAll();
                Path allStat = Files.createTempFile("allStat", ".csv", PosixFilePermissions.asFileAttribute(Set.of(OWNER_WRITE, OWNER_READ)));
                Files.writeString(allStat, String.join("\n", all), StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
                SendDocument sendDocument = new SendDocument();
                sendDocument.setChatId(message.getChatId());
                sendDocument.setDocument(allStat.toFile());
                return List.of(sendDocument);
            default:
                return Collections.emptyList();
        }
    }

    private String normalizeCmdMsg(String text) {
        int begin = 0;
        int end = text.length();

        if (text.startsWith("/")) {
            begin = 1;
        }

        if (text.contains("@")) {
            end = text.indexOf('@');
        }

        return text.substring(begin, end);
    }
}
