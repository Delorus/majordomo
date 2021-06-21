package page.devnet.wordstat.chart;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import page.devnet.database.DataSource;
import page.devnet.database.RepositoryFactory;
import page.devnet.database.repository.WordStorageRepository;
import page.devnet.database.repository.impl.WordStorageImpl;
import page.devnet.wordstat.api.Statistics;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ListChartTest {

    //WordStorage wordStorage;
    WordStorageRepository wordStorageRepository;
    List<String> listUser;
    HashMap<String, List<String>> wordsFrequency;
    ArrayList<String> list;
    Statistics statistics;
    DataSource dataSource = DataSource.inMemory();
    RepositoryFactory repositoryFactory = RepositoryFactory.simple(dataSource);
    String checkStr;
    List<String> checkWords;

    @BeforeEach
    void initTest() {
        wordStorageRepository = new WordStorageImpl(dataSource);
        listUser = new ArrayList<>();
        wordsFrequency = new HashMap<>();
        list = new ArrayList<>();
        statistics = new Statistics(repositoryFactory.buildWordStorageRepository());
        list.add("Привет как дела? Я ушел очень далеко!");
        list.add("Очень далеко, это как?");
        list.add("Как как, вот так. Прекрасное яркое и светлое будущее!");
        list.add("Я совсем забыл предупредить тебя, что дела очень хорошо!");
        checkStr = "привет, как, дела, я, ушел, очень, далеко, очень, далеко, это, как, как, как, вот, так, прекрасное, яркое, и, светлое, будущее, я, совсем, забыл, предупредить, тебя, что, дела, очень, хорошо";
        checkWords = Arrays.asList(checkStr.split("\\s*,\\s*"));
        for (int i = 0; i < 10; i++) {
            listUser.add("user" + i);
            wordsFrequency.put(listUser.get(i), list);
        }
        ZoneId zoneId = ZoneId.of("Asia/Yekaterinburg");
        final Map<Instant, Integer> countRepeatInstance = new HashMap<>();
        countRepeatInstance.put(Clock.tickMillis(zoneId).instant(), 1);
        wordsFrequency.forEach((user, words) -> {

            words.forEach(word -> {
                Clock clock = Clock.tickMillis(zoneId);
                Instant date = clock.instant();
                if (countRepeatInstance.containsKey(date)) {
                    Integer old = countRepeatInstance.get(date);
                    countRepeatInstance.put(date, old + 1);
                } else {
                    countRepeatInstance.put(date, 1);
                }
                for (Map.Entry<Instant, Integer> entry : countRepeatInstance.entrySet()) {
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
    void dataTestFindAllWordsByUserFrom() {
        // 9000 for the catch all test instant.
        Map<String, List<String>> userWordsByStorage = repositoryFactory.buildWordStorageRepository().findAllWordsByUserFrom(Instant.now().minusSeconds(9000));
        for (Map.Entry<String, List<String>> entry : userWordsByStorage.entrySet()) {
            for (int i = 0; i < checkWords.size(); i++) {
                assertEquals(checkWords.get(i), entry.getValue().get(i));
            }
        }
    }
}
