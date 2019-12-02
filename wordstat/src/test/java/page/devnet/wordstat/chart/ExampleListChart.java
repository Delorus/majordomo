package page.devnet.wordstat.chart;

import org.apache.logging.log4j.core.util.JsonUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import page.devnet.wordstat.api.Statistics;
import page.devnet.wordstat.store.InMemoryWordStorage;
import page.devnet.wordstat.store.WordStorage;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.*;

public class ExampleListChart {


    @Test
    void createChart() {
        List<String> listUser = new ArrayList<>();
        HashMap<String, List<String>> wordsFrequency = new HashMap<>();
        WordStorage wordStorage = new InMemoryWordStorage();
        ArrayList<String> list = new ArrayList<>();
        Statistics statistics = new Statistics(wordStorage);

        list.add("Привет как дела? Я ушел очень далеко!");
        list.add("Очень далеко, это как?");
        list.add("Как как, вот так. Прекрасное яркое и светлое будущее!");
        list.add("Я совсем забыл предупредить тебя, что дела очень хорошо!");

        for (int i = 0; i < 10; i++) {
            listUser.add("user" + i);
            wordsFrequency.put(listUser.get(i), list);
        }

        wordsFrequency.forEach((user, words) -> {
            words.forEach(word -> {
                //TODO Think about time because words wrong from user
                Instant date = Instant.now();
                statistics.processText(user, date, word);
                date.plusMillis(1000);
            });
        });
        var fromLastDay = ZonedDateTime.now().minusDays(1);
        List<Chart> top10WordsFromEachUserFromLastDay = statistics.getTop10UsedWordsFromEachUser(fromLastDay.toInstant());

        for (int i = 0; i < top10WordsFromEachUserFromLastDay.size(); i++) {
            Path path = Paths.get("/work/chart" + i+ ".png");
            Files file = null;
            try {
                if (file.exists(path)) {
                    file.delete(path);
                }
                file.createDirectories(path.getParent());
                file.createFile(path);
                InputStream in = top10WordsFromEachUserFromLastDay.get(i).toInputStream();
                Files.copy(in, path, StandardCopyOption.REPLACE_EXISTING);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }
}
