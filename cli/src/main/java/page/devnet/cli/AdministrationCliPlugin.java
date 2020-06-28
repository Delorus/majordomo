package page.devnet.cli;

import lombok.extern.slf4j.Slf4j;
import page.devnet.cli.translate.TranslateCliPlugin;
import page.devnet.database.RepositoryManager;
import page.devnet.pluginmanager.Plugin;
import page.devnet.pluginmanager.PluginManager;
import page.devnet.wordstat.api.Statistics;

import java.io.IOException;
import java.util.Map;

@Slf4j
public class AdministrationCliPlugin implements Plugin<Event, String>, Commandable {

    private final String nameAdministrationPlugin = "adminPlug";

    private final PluginManager plugManager;
    private final RepositoryManager repositoryManager;

    public AdministrationCliPlugin(PluginManager plugManager, RepositoryManager repositoryManager) {
        this.plugManager = plugManager;
        this.repositoryManager = repositoryManager;
    }

    @Override
    public String getPluginId() {
        return nameAdministrationPlugin;
    }

    @Override
    public String onEvent(Event event) {
        if (event.isEmpty()) {
            return "";
        }

        if (event.isCommand()) {
            try {
                return executeCommand(event);
            } catch (IOException e) {
                log.error(e.getMessage(), e);
                return "Sorry, something wrong with send chart: " + e.getMessage();
            }
        }
        return "";
    }
    private String executeCommand(Event event) throws IOException {
        String text = event.getText();
        switch (text) {
            case ":addStatsPlug":
                plugManager.addPlugin(new WordStatisticPlugin(new Statistics(repositoryManager.getWordStorageRepository())));
                return "";
            case ":addTransCliPlug":
                plugManager.addPlugin(TranslateCliPlugin.newYandexTranslatePlugin());
                return "";
            case ":deleteStatsPlug":
                plugManager.deletePlugin("statsPlug");
                return "";
            case ":deleteTransCliPlug":
                plugManager.deletePlugin("transCliPlug");
                return "";
            case ":workPlug":
                plugManager.getWorkPlug();
                return "";
        }
        return "";

    }

    @Override
    public String serviceName() {
        return "Administration plugin";
    }

    @Override
    public Map<String, String> commandDescriptionList() {
        return Map.of(
                ":addStatsPlug", "add wordStatisticsPlugin",
                ":deleteStatsPlug", "delete wordStatisticsPlugin",
                ":addTransCliPlug", "add wordLimitPlugin",
                ":deleteTransCliPlug", "delete wordLimitPlugin",
                ":workPlug", "get work Plugin"

        );
    }
}