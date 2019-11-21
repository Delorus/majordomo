package page.devnet.wordstat.chart;

import lombok.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;

/**
 * @author maksim
 * @since 23.03.19
 */
public final class FrequentlyUsedWordsChart implements Renderable {

    @Value
    private static class WordCount {
        String word;
        int numberOfUses;
    }

    private final List<WordCount> wordCounts = new ArrayList<>();

    private int totalWords;
    private int limit = 0;

    public void addWord(String word, int count) {
        wordCounts.add(new WordCount(word, count));
    }

    public void setAllWordsCount(int count) {
        totalWords = count;

        assert totalWords >= wordCounts.stream().map(WordCount::getNumberOfUses).count();
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    @Override
    public Chart renderBy(XChartRenderer renderer, String titlePostfix) {
        var sortedStream = wordCounts.stream()
                .sorted(Comparator.comparing(WordCount::getNumberOfUses).reversed());
        if (limit != 0) {
            sortedStream = sortedStream.limit(limit);
        }

        XChartRenderer.ChartData[] data = sortedStream
                .map(wc -> new XChartRenderer.ChartData(wc.word, calcPercent(wc.numberOfUses)))
                .toArray(XChartRenderer.ChartData[]::new);

        return renderer.createPieChart("Word frequency: " + titlePostfix, data);
    }

    private double calcPercent(int numberOfUses) {
        return (double) numberOfUses / totalWords;
    }
}
