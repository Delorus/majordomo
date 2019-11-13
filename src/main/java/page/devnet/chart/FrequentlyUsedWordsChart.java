package page.devnet.chart;

import lombok.Value;

import java.util.ArrayList;
import java.util.List;

/**
 * @author maksim
 * @since 23.03.19
 */
final class FrequentlyUsedWordsChart implements Renderable {

    @Value
    private static class WordCount {
        String word;
        int numberOfUses;
    }

    private final List<WordCount> wordCounts = new ArrayList<>();

    private int totalWords;

    public void addWord(String word, int count) {
        wordCounts.add(new WordCount(word, count));
    }

    public void addAllWords(int count) {
        totalWords = count;

        assert totalWords >= wordCounts.stream().map(WordCount::getNumberOfUses).count();
    }

    @Override
    public Chart renderBy(XChartRenderer renderer) {
        XChartRenderer.ChartData[] data = wordCounts.stream()
                                                    .map(wc -> new XChartRenderer.ChartData(wc.word, calcPercent(wc.numberOfUses)))
                                                    .toArray(XChartRenderer.ChartData[]::new);

        return renderer.createPieChart("Word frequency", data);
    }

    private double calcPercent(int numberOfUses) {
        return (double) numberOfUses / totalWords;
    }
}