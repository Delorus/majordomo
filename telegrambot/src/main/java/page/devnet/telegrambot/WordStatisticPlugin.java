package page.devnet.telegrambot;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import page.devnet.database.repository.UserRepository;
import page.devnet.pluginmanager.Plugin;
import page.devnet.telegrambot.util.CommandUtils;
import page.devnet.wordstat.api.Statistics;
import page.devnet.wordstat.chart.Chart;

import java.io.IOException;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * @author maksim
 * @since 19.11.2019
 */
@Slf4j
public class WordStatisticPlugin implements Plugin<Update, List<PartialBotApiMethod<?>>> {

    @Override
    public String getPluginId() {
        return "statPlug";
    }

    private final Statistics statistics;

    private final UserRepository userRepository;

    @Setter
    private CommandUtils commandUtils = new CommandUtils();

    public WordStatisticPlugin(Statistics statistics, UserRepository userRepository) {
        this.statistics = statistics;
        this.userRepository = userRepository;
        log.info("Start Word Statistic plugin");
    }

    @Override
    public List<PartialBotApiMethod<?>> onEvent(Update event) {
        if (!event.hasMessage()) {
            return Collections.emptyList();
        }

        if (event.getMessage().isCommand()) {
            try {
                return executeCommand(event.getMessage());
            } catch (IOException e) {
                log.error(e.getMessage(), e);
                var chatId = String.valueOf(event.getMessage().getChatId());
                return List.of(new SendMessage(chatId, "Sorry, something wrong with send chart: " + e.getMessage()));
            }
        }

        if (!event.getMessage().hasText()) {
            return Collections.emptyList();
        }

        var message = event.getMessage();
        var user = userRepository.find(message.getFrom().getId().intValue());
        if (user.isEmpty()) {
            user = createUser(message.getFrom());
        }
        var formattedUserName = user.get().getFormattedUsername();
        var date = Instant.ofEpochSecond(message.getDate());
        statistics.processText(String.valueOf(formattedUserName), date, message.getText());

        return Collections.emptyList();
    }

    private List<PartialBotApiMethod<?>> executeCommand(Message message) throws IOException {
        var text = commandUtils.normalizeCmdMsg(message.getText());
        var chatId = String.valueOf(message.getChatId());
        switch (text) {
            case "statf":
                var fromLastDay = ZonedDateTime.now().minusDays(1);
                try {
                    Chart top10UsedWordsFromLastDay = statistics.getTop10UsedWordsFrom(fromLastDay.toInstant());
                    SendPhoto sendPhoto = wrapToSendPhoto(top10UsedWordsFromLastDay, "top 10 used words from last day", chatId);
                    return List.of(sendPhoto);
                } catch (IllegalArgumentException e) {
                    return List.of(new SendMessage(chatId, e.getMessage()));
                }
            case "state":
                fromLastDay = ZonedDateTime.now().minusDays(1);
                try {
                    List<Chart> top10WordsFromEachUserFromLastDay = statistics.getTop10UsedWordsFromEachUser(fromLastDay.toInstant());
                    List<PartialBotApiMethod<?>> result = new ArrayList<>();
                    for (Chart chart : top10WordsFromEachUserFromLastDay) {
                        SendPhoto sendPhoto = wrapToSendPhoto(chart, "top 10 words by each user from last day", chatId);
                        result.add(sendPhoto);
                    }
                    return result;
                } catch (IllegalArgumentException e) {
                    return List.of(new SendMessage(chatId, e.getMessage()));
                }
            case "statu":
                fromLastDay = ZonedDateTime.now().minusDays(1);
                try {
                    Chart top10WordsFromLastDayByUser = statistics.getWordsCountByUserFrom(fromLastDay.toInstant());
                    SendPhoto sendPhoto = wrapToSendPhoto(top10WordsFromLastDayByUser, "top 10 words from last day by user", chatId);
                    return List.of(sendPhoto);
                } catch (IllegalArgumentException e) {
                    return List.of(new SendMessage(chatId, e.getMessage()));
                }

            default:
                return Collections.emptyList();
        }
    }

    private SendPhoto wrapToSendPhoto(Chart chart, String title, String chatId) throws IOException {
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(chatId);

        sendPhoto.setPhoto(new InputFile(chart.toInputStream(), title));
        log.info("Send new chart {} to group: {}", title, chatId);
        return sendPhoto;
    }

    private Optional<page.devnet.database.entity.User> createUser(User tgUser) {
        var user = new page.devnet.database.entity.User();

        if (tgUser.getUserName() != null && !tgUser.getUserName().isEmpty()) {
            user.setUserName(tgUser.getUserName());
        }

        if (!tgUser.getFirstName().isEmpty()) {
            user.setFirstName(tgUser.getFirstName());
        }

        if (tgUser.getLastName() != null && !tgUser.getLastName().isEmpty()) {
            user.setLastName(tgUser.getLastName());
        }

        return Optional.of(userRepository.createOrUpdate(tgUser.getId().intValue(), user));
    }
}
