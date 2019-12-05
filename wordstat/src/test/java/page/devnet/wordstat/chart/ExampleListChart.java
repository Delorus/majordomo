package page.devnet.wordstat.chart;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import page.devnet.wordstat.api.Statistics;
import page.devnet.wordstat.store.InMemoryWordStorage;
import page.devnet.wordstat.store.WordStorage;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ExampleListChart {

    WordStorage wordStorage;
    List<String> listUser;
    HashMap<String, List<String>> wordsFrequency;
    ArrayList<String> list;
    Statistics statistics;

    @BeforeEach
    void initTest() {
        wordStorage = new InMemoryWordStorage();
        listUser = new ArrayList<>();
        wordsFrequency = new HashMap<>();
        list = new ArrayList<>();
        statistics = new Statistics(wordStorage);
        list.add("Привет как дела? Я ушел очень далеко!");
        list.add("Очень далеко, это как?");
        list.add("Как как, вот так. Прекрасное яркое и светлое будущее!");
        list.add("Я совсем забыл предупредить тебя, что дела очень хорошо!");

        for (int i = 0; i < 10; i++) {
            listUser.add("user" + i);
            wordsFrequency.put(listUser.get(i), list);
        }
        ZoneId zoneId = ZoneId.of("Asia/Yekaterinburg");
        final Map<Instant, Integer> countRepeatInstantce = new HashMap<>();
        countRepeatInstantce.put(Clock.tickMillis(zoneId).instant(), 1);

        wordsFrequency.forEach((user, words) -> {

            words.forEach(word -> {
                Clock clock = Clock.tickMillis(zoneId);
                Instant date = clock.instant();
                if (countRepeatInstantce.containsKey(date)) {
                    Integer old = countRepeatInstantce.get(date);
                    countRepeatInstantce.put(date, old + 1);
                } else {
                    countRepeatInstantce.put(date, 1);
                }
                for (Map.Entry<Instant, Integer> entry : countRepeatInstantce.entrySet()) {
                    Instant key = entry.getKey();
                    int value = entry.getValue();
                    if (key.equals(date)) {
                        if (value > 1) {
                            statistics.processText(user, date.minusSeconds(10 * value), word);
                            break;
                        } else {
                            statistics.processText(user, date, word);
                            break;
                        }
                    }
                }

            });
        });
    }

    @Test
    void createChart() {

        var fromLastDay = ZonedDateTime.now().minusDays(1);
        List<Chart> top10WordsFromEachUserFromLastDay = statistics.getTop10UsedWordsFromEachUser(fromLastDay.toInstant());
        for (int i = 0; i < top10WordsFromEachUserFromLastDay.size(); i++) {
            Path path = Paths.get("/work/chart" + i + ".png");

            try {
                if (Files.exists(path)) {
                    Files.delete(path);
                }
                Files.createDirectories(path.getParent());
                Files.createFile(path);
                InputStream in = top10WordsFromEachUserFromLastDay.get(i).toInputStream();
                Files.copy(in, path, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
            }
            assertTrue(Files.exists(path));
        }
    }

    @Test
    void dataTest() {
        String checkStr = "привет, как, дела, я, ушел, очень, далеко, очень, далеко, это, как, как, как, вот, так, прекрасное, яркое, и, светлое, будущее, я, совсем, забыл, предупредить, тебя, что, дела, очень, хорошо";
        List<String> checkWords = Arrays.asList(checkStr.split("\\s*,\\s*"));
        Map<String, List<String>> userWordsByStorage = wordStorage.findAllWordsByUserFrom(Instant.now().minusSeconds(9000));
        for (Map.Entry<String, List<String>> entry : userWordsByStorage.entrySet()) {
            for (int i = 0; i < checkWords.size(); i++) {
                assertEquals(checkWords.get(i), entry.getValue().get(i));
            }
        }
    }


}
