package page.devnet.telegrambot;

import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import page.devnet.pluginmanager.Plugin;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Pattern;

/**
 * @author maksim
 * @since 14.05.2020
 */
@Slf4j
public class WordLimiterPlugin implements Plugin<Update, List<PartialBotApiMethod>> {

    private static final Pattern WORD_PATTERN = Pattern.compile("\\w+", Pattern.UNICODE_CHARACTER_CLASS);

    private final ConcurrentMap<String, WordCount> countWordsByUser = new ConcurrentHashMap<>();

    @Override
    public List<PartialBotApiMethod> onEvent(Update event) {
        if (!event.hasMessage() || !event.getMessage().hasText()) {
            return Collections.emptyList();
        }

        Message message = event.getMessage();
        var userId = formatUserName(message.getFrom());
        int wordsCount = parseWords(message.getText()).size();

        var wc = countWordsByUser.merge(userId, WordCount.ofCount(wordsCount), (old, next) -> {
            if (Duration.between(old.timestamp, next.timestamp).compareTo(Duration.ofDays(1)) >= 0) {
                return next;
            } else {
                return old.add(next);
            }
        });

        String msg = "";
        if (wc.inRange(400)) {
            msg = "0YHQtdCz0L7QtNC90Y8g0JLRiyDQtNC+0YHRgtCw0YLQvtGH0L3QviDQvdCw0L/QuNGB0LDQu9C4LCDQvtGC0LTQvtGF0L3QuNGC0LUu";
        } else if (wc.inRange(600)) {
            msg = "0JLRiyDRg9C20LUg0LTQvtCy0L7Qu9GM0L3QviDQvNC90L7Qs9C+INC90LDQv9C40YHQsNC70LgsINC80L7QttC10YIg0L/QvtGA0LAg0L7RgdGC0LDQvdC+0LLQuNGC0YzRgdGPPw==";
        } else if (wc.inRange(1000)) {
            msg = "0L/QvtGB0LvQtdC00L3QtdC1INC/0YDQtdC00YPQv9GA0LXQttC00LXQvdC40LUsINCS0Ysg0YPQttC1INC/0L7Rh9GC0Lgg0LTQvtGB0YLQuNCz0LvQuCDQv9C10YDQstC+0LPQviDQvNC10YHRgtCwINCyINC90LjQutGH0LXQvNC90L7RgdGC0LgsINGF0LLQsNGC0LjRgi4=";
        } else {
            int limit = 1300;
            if (wc.inRange(limit) || wc.count > limit && (wc.count - wc.prevCount >= 10)) {
                msg = "KirQntCh0KLQkNCd0J7QktCY0KHQrCEhISoq";
            }
        }

        if (msg.isEmpty()) {
            return Collections.emptyList();
        }

        msg = String.join(", ", userId, new String(Base64.decodeBase64(msg)));
        return List.of(new SendMessage(message.getChatId(), msg).enableMarkdown(true));
    }

    // copy-paste
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

    // copy-paste
    private List<String> parseWords(String text) {
        var result = new ArrayList<String>();
        var matcher = WORD_PATTERN.matcher(text);
        while (matcher.find()) {
            result.add(matcher.group());
        }

        return result;
    }

    @Value
    private static class WordCount {
        public static WordCount ofCount(int count) {
            return new WordCount(count, 0, Instant.now());
        }

        int count;
        int prevCount;
        Instant timestamp;

        public WordCount add(WordCount wc) {
            return new WordCount(count + wc.count, count, timestamp);
        }

        public boolean inRange(int c) {
            return prevCount <= c && count > c;
        }
    }
}
