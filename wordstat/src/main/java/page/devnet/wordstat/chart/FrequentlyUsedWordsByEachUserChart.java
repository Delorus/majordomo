package page.devnet.wordstat.chart;


import java.util.LinkedHashMap;

/**
 * create by
 * Konstantin
 */
public final class FrequentlyUsedWordsByEachUserChart implements Renderable {

    private final String user;
    private final LinkedHashMap<String, Integer> wordsUser;

    public FrequentlyUsedWordsByEachUserChart(String user, LinkedHashMap<String, Integer> wordsUser) {
        this.user = user;
        this.wordsUser = wordsUser;
    }

    @Override
    public Chart renderBy(XChartRenderer renderService, String titlePostfix) {
        XChartRenderer.BarChartData topEachUser = new XChartRenderer.BarChartData(user, wordsUser);
        return renderService.createBarChart("Top 10 words by " + user + ": " + titlePostfix, topEachUser);
    }
}
