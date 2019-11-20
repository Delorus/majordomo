package page.devnet.wordstat.api;

import page.devnet.wordstat.chart.Chart;
import page.devnet.wordstat.chart.FrequentlyUsedWordsChart;
import page.devnet.wordstat.chart.XChartRenderer;
import page.devnet.wordstat.store.WordStorage;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * @author maksim
 * @since 19.11.2019
 */
public class Statistics {

    private static final Pattern WORD_PATTERN = Pattern.compile("\\w+", Pattern.UNICODE_CHARACTER_CLASS);

    private final WordStorage storage;

    public Statistics(WordStorage storage) {
        this.storage = storage;
    }

    public void processText(String userId, Instant date, String text) {
        Objects.requireNonNull(text);
        if (text.isEmpty()) {
            return;
        }

        var words = parseWords(text);

        storage.storeAll(userId, date, words);
    }

    private List<String> parseWords(String text) {
        var result = new ArrayList<String>();
        var matcher = WORD_PATTERN.matcher(text);
        while (matcher.find()) {
            result.add(matcher.group());
        }

        return result;
    }

    public Chart getTop10UserWordsFrom(Instant from) {
        List<String> words = storage.findAllWordsFrom(from);

        var chart = new FrequentlyUsedWordsChart();

        var wordCount = new HashMap<String, Integer>();
        for (String word : words) {
            wordCount.compute(word, (w, i) -> i == null ? 1 : i + 1);
        }
        wordCount.forEach(chart::addWord);

        chart.setAllWordsCount(words.size());

        return chart.renderBy(new XChartRenderer(), "from last day");
    }

    public List<String> flushAll() {
        return storage.flushAll();
    }
}
