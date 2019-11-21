package page.devnet.wordstat.api;

import page.devnet.wordstat.chart.Chart;
import page.devnet.wordstat.chart.FrequentlyUsedWordsByUserChart;
import page.devnet.wordstat.chart.FrequentlyUsedWordsChart;
import page.devnet.wordstat.chart.XChartRenderer;
import page.devnet.wordstat.normalize.Normalizer;
import page.devnet.wordstat.store.WordStorage;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author maksim
 * @since 19.11.2019
 */
public class Statistics {

    private static final Pattern WORD_PATTERN = Pattern.compile("\\w+", Pattern.UNICODE_CHARACTER_CLASS);
    private static final Pattern RUSSIAN_WORD = Pattern.compile("^[А-Яа-я]+$");
    private static final Pattern ENGLISH_WORD = Pattern.compile("^[A-Za-z]+$");

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

        Normalizer russian = Normalizer.forRussian();
        List<String> russiansWords = words.stream()
                .filter(RUSSIAN_WORD.asMatchPredicate())
                .map(russian::normalize)
                .collect(Collectors.toList());

        Normalizer english = Normalizer.forEnglish();
        List<String> englishWords = words.stream()
                .filter(ENGLISH_WORD.asMatchPredicate())
                .map(english::normalize)
                .collect(Collectors.toList());

        russiansWords.addAll(englishWords);
        storage.storeAll(userId, date, russiansWords);
    }

    private List<String> parseWords(String text) {
        var result = new ArrayList<String>();
        var matcher = WORD_PATTERN.matcher(text);
        while (matcher.find()) {
            result.add(matcher.group());
        }

        return result;
    }

    public Chart getTop10UsedWordsFrom(Instant from) {
        List<String> words = storage.findAllWordsFrom(from);

        var chart = new FrequentlyUsedWordsChart();

        calcWordFrequency(words).forEach(chart::addWord);

        chart.setAllWordsCount(words.size());
        chart.setLimit(10);

        return chart.renderBy(new XChartRenderer(), "from last day");
    }

    private HashMap<String, Integer> calcWordFrequency(List<String> words) {
        var wordCount = new HashMap<String, Integer>();
        for (String word : words) {
            wordCount.compute(word, (w, i) -> i == null ? 1 : i + 1);
        }
        return wordCount;
    }

    public List<String> flushAll() {
        return storage.flushAll();
    }

    public Chart getWordsCountByUserFrom(Instant from) {
        Map<String, List<String>> userToWords =  storage.findAllWordsByUserFrom(from);

        var chart = new FrequentlyUsedWordsByUserChart();

        userToWords.forEach((user, words) -> {
            HashMap<String, Integer> wordFrequency = calcWordFrequency(words);
            long unique = wordFrequency.entrySet()
                    .stream()
                    .filter(entry -> entry.getValue() == 1)
                    .count();
            int all = wordFrequency.size();
            chart.addUser(user, all, (int) unique);
        });

        return chart.renderBy(new XChartRenderer(), "from last day");
    }
}