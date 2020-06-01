package page.devnet.telegrambot;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import page.devnet.pluginmanager.Plugin;
import page.devnet.telegrambot.util.CommandUtils;
import page.devnet.wordstat.api.Statistics;
import page.devnet.wordstat.chart.Chart;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author maksim
 * @since 19.11.2019
 */
@Slf4j
public class WordStatisticPlugin implements Plugin<Update, List<PartialBotApiMethod>> {

    private final Statistics statistics;

    @Setter
    private CommandUtils commandUtils = new CommandUtils();

    public WordStatisticPlugin(Statistics statistics) {
        this.statistics = statistics;
        log.info("Start Word Statistic plugin");
    }

    @Override
    public List<PartialBotApiMethod> onEvent(Update event) {
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
        var userId = formatUserName(message.getFrom()); //todo сохранять id и по нему уже потом восстанавливать нормальное имя
        var date = Instant.ofEpochSecond(message.getDate());
        statistics.processText(String.valueOf(userId), date, message.getText());

        return Collections.emptyList();
    }

    private List<PartialBotApiMethod> executeCommand(Message message) throws IOException {
        var text = commandUtils.normalizeCmdMsg(message.getText());
        switch (text) {
            case "statf":
                var fromLastDay = ZonedDateTime.now().minusDays(1);
                Chart top10UsedWordsFromLastDay = statistics.getTop10UsedWordsFrom(fromLastDay.toInstant());
                SendPhoto sendPhoto = wrapToSendPhoto(top10UsedWordsFromLastDay, message.getChatId());
                return List.of(sendPhoto);
            case "state":
                fromLastDay = ZonedDateTime.now().minusDays(1);
                List<Chart> top10WordsFromEachUserFromLastDay = statistics.getTop10UsedWordsFromEachUser(fromLastDay.toInstant());
                List<PartialBotApiMethod> result = new ArrayList<>();
                for (Chart chart : top10WordsFromEachUserFromLastDay) {
                    sendPhoto = wrapToSendPhoto(chart, message.getChatId());
                    result.add(sendPhoto);
                }
                return result;
            case "statu":
                fromLastDay = ZonedDateTime.now().minusDays(1);
                Chart top10WordsFromLastDayByUser = statistics.getWordsCountByUserFrom(fromLastDay.toInstant());
                sendPhoto = wrapToSendPhoto(top10WordsFromLastDayByUser, message.getChatId());
                return List.of(sendPhoto);
            case "flush":
                List<String> all = statistics.flushAll();
                if (all.isEmpty()) {
                    return Collections.emptyList();
                }

                Path allStat = Files.createTempFile("allStat", ".csv");
                Files.writeString(allStat, String.join("\n", all), StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
                SendDocument sendDocument = new SendDocument();
                sendDocument.setChatId(message.getChatId());
                sendDocument.setDocument(allStat.toFile());
                return List.of(sendDocument);
            default:
                return Collections.emptyList();
        }
    }

    private SendPhoto wrapToSendPhoto(Chart chart, Long chatId) throws IOException {
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(chatId);

        Path file = Files.createTempFile("chart", ".png");
        try (InputStream in = chart.toInputStream()) {
            Files.copy(in, file, StandardCopyOption.REPLACE_EXISTING);
        }
        sendPhoto.setPhoto(file.toFile());
        log.info("Send new chart {} with size: {}bytes", file.toFile().getName(), file.toFile().length());
        return sendPhoto;
    }


    private String formatUserName(User user) {
        String name = user.getUserName();
        if (user.getFirstName() != null && !user.getFirstName().isEmpty()) {
            name = user.getFirstName();

            if (user.getLastName() != null && !user.getLastName().isEmpty()) {
                name += " " + user.getLastName();
            }
        }
        return name;
    }
}
