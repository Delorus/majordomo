package ru.sherb.chart;

import lombok.Value;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import static ru.sherb.chart.XChartRenderer.ChartData;

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

    public void addWord(String word, int count) {
        wordCounts.add(new WordCount(word, count));
    }

    public void addAllWords(int count) {
        totalWords = count;

        assert totalWords >= wordCounts.stream().map(WordCount::getNumberOfUses).count();
    }

    @Override
    public BufferedImage renderBy(XChartRenderer renderer) {
        ChartData[] data = wordCounts.stream()
                .map(wc -> new ChartData(wc.word, calcPercent(wc.numberOfUses)))
                .toArray(ChartData[]::new);

        return renderer.createPieChart("Word frequency", data);
    }

    private double calcPercent(int numberOfUses) {
        return (double) numberOfUses / totalWords;
    }
}
