package page.devnet.wordstat.chart;

import lombok.Value;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

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
                .filter(wc -> wc.word.length() >= 3) //todo hotfix
                .sorted(Comparator.comparing(WordCount::getNumberOfUses).reversed());
        if (limit != 0) {
            sortedStream = sortedStream.limit(limit);
        }

        XChartRenderer.PieChartData[] data = sortedStream
                .map(wc -> new XChartRenderer.PieChartData(wc.word, calcPercent(wc.numberOfUses)))
                .toArray(XChartRenderer.PieChartData[]::new);

        return renderer.createPieChart("Word frequency: " + titlePostfix, data);
    }

    @Override
    public List<Chart> renderList(XChartRenderer rendererService, String titlePostfix) {
        return null;
    }

    private double calcPercent(int numberOfUses) {
        return (double) numberOfUses / totalWords;
    }
}
