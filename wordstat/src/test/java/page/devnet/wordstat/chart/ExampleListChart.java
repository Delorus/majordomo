package page.devnet.wordstat.chart;

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

        list.add("Привет как дела? Я ушел очень далеко.");
        list.add("Очень далеко, это как?");
        list.add("Как как, вот так.");
        list.add("Я совсем забыл предупредить тебя, что дела очень хорошо");

        for (int i = 0; i < 10; i++) {
            listUser.add("user" + i);
            wordsFrequency.put(listUser.get(i), list);

        }
        Statistics statistics = new Statistics(wordStorage);
       // System.out.println(wordsFrequency);

        for (String s : wordsFrequency.keySet()) {
           // System.out.println("s " + s);
            Instant time = Instant.now();
           // System.out.println("time " + time);

            for (int i = 0; i < wordsFrequency.values().iterator().next().size(); i++) {
               // System.out.println(" value " + wordsFrequency.values().iterator().next().get(i));
                statistics.processText(s, time, wordsFrequency.values().iterator().next().get(i));

            }

        }
        var fromLastDay = ZonedDateTime.now().minusDays(1);
        List<Chart> top10WordsFromEachUserFromLastDay = statistics.getTop10UsedWordsFromEachUser(fromLastDay.toInstant());

        for (int i = 0; i < top10WordsFromEachUserFromLastDay.size(); i++) {
            Path path = Paths.get("/work/chart" + i + Instant.now()+ ".png");
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
