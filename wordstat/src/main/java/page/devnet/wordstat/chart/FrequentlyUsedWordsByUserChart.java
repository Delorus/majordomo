package page.devnet.wordstat.chart;

import lombok.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author maksim
 * @since 21.11.2019
 */
public final class FrequentlyUsedWordsByUserChart implements Renderable {

    @Value
    private static class UserWordCount {
        String user;
        int allWords;
        int uniqueWords;
    }
    private final List<UserWordCount> userWordCounts = new ArrayList<>();

    public void addUser(String user, int allWordsCount, int uniqueWordsCount) {
        userWordCounts.add(new UserWordCount(user, allWordsCount, uniqueWordsCount));
    }

    @Override
    public Chart renderBy(XChartRenderer renderer, String titlePostfix) {
        var allWords = userWordCounts.stream()
                .collect(Collectors.toMap(UserWordCount::getUser, UserWordCount::getAllWords));
        var uniqueWords = userWordCounts.stream()
                .collect(Collectors.toMap(UserWordCount::getUser, UserWordCount::getUniqueWords));

        XChartRenderer.BarChartData all_words = new XChartRenderer.BarChartData("All words", allWords);
        XChartRenderer.BarChartData unique_words = new XChartRenderer.BarChartData("Unique words", uniqueWords);

        return renderer.createBarChart("Use of words by users: " + titlePostfix, all_words, unique_words);
    }
}
