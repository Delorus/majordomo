package page.devnet.wordstat.api;

import page.devnet.wordstat.chart.*;
import page.devnet.wordstat.normalize.Normalizer;
import page.devnet.wordstat.store.WordStorage;

import java.time.Instant;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author maksim
 * @since 19.11.2019
 */
public final class Statistics {

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

        XChartRenderer renderer = new XChartRenderer();
        return renderer.render(chart, "from last day");
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

    //TODO
    public Chart getTop10UsedWordsFromEachUser(Instant from){
        Map<String,List<String>> userToWords = storage.findAllWordsByUserFrom(from);

        FrequentlyUsedWordsByEachUserChart chart = new FrequentlyUsedWordsByEachUserChart();

        userToWords.forEach((user, words)->{
            //частота слов
            HashMap<String, Integer> wordFrequency = calcWordFrequency(words);
            List<Integer> listtosort =  new ArrayList<Integer>(wordFrequency.values());
            Collections.sort(listtosort, Collections.reverseOrder());

            HashMap<String, Integer> finalTop10words = new HashMap<>();
            //sort and get first top 10
            for (int i=0; i<10; i++) {
                for (Map.Entry<String, Integer> entry : wordFrequency.entrySet()) {
                    if (entry.getValue().equals(listtosort.get(i))) {
                        finalTop10words.put(entry.getKey(), listtosort.get(i));
                    }
                }
            }
            chart.addUser(user,finalTop10words);
        });
        XChartRenderer renderer = new XChartRenderer();
        return null;
    }
    public Chart getWordsCountByUserFrom(Instant from) {
        Map<String, List<String>> userToWords =  storage.findAllWordsByUserFrom(from);

        var chart = new FrequentlyUsedWordsByUserChart();

        userToWords.forEach((user, words) -> {
            HashMap<String, Integer> wordFrequency = calcWordFrequency(words);
            long unique = wordFrequency.size();
            int all = wordFrequency.values().stream().mapToInt(Integer::intValue).sum();
            if (all != 0) {
                chart.addUser(user, all, (int) unique);
            }
        });
        XChartRenderer renderer = new XChartRenderer();
        return renderer.render(chart, "from last day");
    }
}
