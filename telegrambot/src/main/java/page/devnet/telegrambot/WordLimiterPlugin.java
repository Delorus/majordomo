package page.devnet.telegrambot;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import page.devnet.database.repository.UnsubscribeRepository;
import page.devnet.pluginmanager.Plugin;
import page.devnet.telegrambot.util.ChatDateTime;
import page.devnet.telegrambot.util.ParserMessage;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author maksim
 * @since 14.05.2020
 */
@Slf4j
public class WordLimiterPlugin implements Plugin<Update, List<PartialBotApiMethod<?>>> {


    @Override
    public String getPluginId() {
        return "limitPlug";
    }

    private static final Pattern WORD_PATTERN = Pattern.compile("\\w+", Pattern.UNICODE_CHARACTER_CLASS);

    private final ConcurrentMap<String, WordCount> countWordsByUser = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, List<Integer>> limitSettingByUser = new ConcurrentHashMap<>();

    private final UnsubscribeRepository unsubscribeRepository;

    public WordLimiterPlugin(UnsubscribeRepository unsubscribeRepository) {
        this.unsubscribeRepository = unsubscribeRepository;

    }

    @Override
    public List<PartialBotApiMethod<?>> onEvent(Update event) {
        if (!event.hasMessage() || !event.getMessage().hasText()) {
            return Collections.emptyList();
        }

        if (event.getMessage().isCommand()) {
            return executeCommand(event.getMessage());
        }

        Message message = event.getMessage();
        if (unsubscribeRepository.find(message.getFrom().getId().intValue()).isPresent()) {
            return Collections.emptyList();
        }

        var formattedUserName = formatUserName(message.getFrom());

        int wordsCount = parseWords(message.getText()).size();

        var wc = countWordsByUser.merge(formattedUserName, WordCount.ofCount(wordsCount), (old, next) -> {

            var fromStartDayToNow = Duration.between(new ChatDateTime(next.timestamp).fromFixHoursTime(3), next.timestamp);
            if (Duration.between(old.timestamp, next.timestamp).compareTo(fromStartDayToNow) >= 0) {
                return next;
            } else {
                return old.add(next);
            }
        });

        if (!limitSettingByUser.containsKey(formattedUserName)) {
            limitSettingByUser.put(formattedUserName, Arrays.asList(600, 800, 1000, 1300));
        }

        String msg = "";
        msg = checkWCRange(formattedUserName, wc);

        if (msg.isEmpty()) {
            return Collections.emptyList();
        }

        msg = String.join(", ", formattedUserName, new String(Base64.getDecoder().decode(msg)));
        var chatId = message.getChatId();
        return List.of(SendMessage.builder()
                .chatId(String.valueOf(chatId))
                .text(msg)
                .parseMode(ParseMode.MARKDOWN)
                .build());
    }

    private void setLimitRange(String formatedUserName, String commandParameter) {
        List<Integer> warningsValue = Arrays.stream(commandParameter.split(" "))
                .map(Integer::valueOf)
                .collect(Collectors.toList());
        limitSettingByUser.compute(formatedUserName, (u, w) -> warningsValue);

    }

    private String checkWCRange(String formattedUsername, WordCount wc) {
        List<Integer> rangeValue = limitSettingByUser.get(formattedUsername);
        if (wc.inRange(rangeValue.get(0))) {
            return "0YHQtdCz0L7QtNC90Y8g0JLRiyDQtNC+0YHRgtCw0YLQvtGH0L3QviDQvdCw0L/QuNGB0LDQu9C4LCDQvtGC0LTQvtGF0L3QuNGC0LUu";
        } else if (wc.inRange(rangeValue.get(1))) {
            return "0JLRiyDRg9C20LUg0LTQvtCy0L7Qu9GM0L3QviDQvNC90L7Qs9C+INC90LDQv9C40YHQsNC70LgsINC80L7QttC10YIg0L/QvtGA0LAg0L7RgdGC0LDQvdC+0LLQuNGC0YzRgdGPPw==";
        } else if (wc.inRange(rangeValue.get(2))) {
            return "0L/QvtGB0LvQtdC00L3QtdC1INC/0YDQtdC00YPQv9GA0LXQttC00LXQvdC40LUsINCS0Ysg0YPQttC1INC/0L7Rh9GC0Lgg0LTQvtGB0YLQuNCz0LvQuCDQv9C10YDQstC+0LPQviDQvNC10YHRgtCwINCyINC90LjQutGH0LXQvNC90L7RgdGC0LgsINGF0LLQsNGC0LjRgi4=";
        } else {
            if (wc.inRange(rangeValue.get(3)) || wc.count > rangeValue.get(3) && (wc.count - wc.prevCount >= 10)) {
                return "KirQntCh0KLQkNCd0J7QktCY0KHQrCEhISoq";
            }
        }
        return "";
    }

    private List<PartialBotApiMethod<?>> executeCommand(Message message) {
        ParserMessage parserMessage = new ParserMessage();
        var command = parserMessage.getCommandFromMessage(message.getText());
        var commandParameter = parserMessage.getCommandParameterFromMessage(message.getText());
        switch (command) {
            case UNSUBSCRIBE: {
                unsubscribeRepository.createOrUpdate(message.getFrom().getId().intValue(), message.getFrom().getId().intValue());
                return Collections.emptyList();
            }
            case SUBSCRIBE: {
                unsubscribeRepository.delete(message.getFrom().getId().intValue());
                return Collections.emptyList();
            }
            case LIMITTIME: {
                if (commandParameter.split(" ").length == 4) {
                    setLimitRange(formatUserName(message.getFrom()), commandParameter);
                    return List.of(new SendMessage(String.valueOf(message.getChatId()), "It's set"));
                } else {
                    return List.of(new SendMessage(String.valueOf(message.getChatId()), "Sorry, message must be in format: 'number number number number'"));
                }
            }
            default:
                return Collections.emptyList();
        }
    }

    // copy-paste
    private String formatUserName(User user) {
        String name = user.getUserName();
        if (!user.getFirstName().isEmpty()) {
            name = user.getFirstName();

            if (user.getLastName() != null && !user.getLastName().isEmpty()) {
                name += " " + user.getLastName();
            }
        }

        return name;
    }

    // copy-paste
    private List<String> parseWords(String text) {
        var result = new ArrayList<String>();
        var matcher = WORD_PATTERN.matcher(text);
        while (matcher.find()) {
            result.add(matcher.group());
        }

        return result;
    }

    @RequiredArgsConstructor
    @Getter
    @EqualsAndHashCode
    @ToString
    @Setter
    private static final class WordCount {

        public static WordCount ofCount(int count) {
            return new WordCount(count, 0, ZonedDateTime.now(ZoneId.of("Asia/Yekaterinburg")));
        }

        private final int count;
        private final int prevCount;
        private final ZonedDateTime timestamp;

        public WordCount add(WordCount wc) {
            return new WordCount(count + wc.count, count, timestamp);
        }

        public boolean inRange(int c) {
            return prevCount <= c && count > c;
        }
    }
}
