package page.devnet.telegrambot;

import lombok.Setter;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import page.devnet.database.RepositoryManager;
import page.devnet.pluginmanager.Plugin;
import page.devnet.pluginmanager.PluginManager;
import page.devnet.telegrambot.translate.TranslateBotPlugin;
import page.devnet.telegrambot.util.CommandUtils;
import page.devnet.wordstat.api.Statistics;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class AdministrationPlugin implements Plugin<Update, List<PartialBotApiMethod>> {

    private final String nameAdministrationPlugin = "adminPlug";

    private PluginManager pluginManager;
    private RepositoryManager repositoryManager;

    public AdministrationPlugin(PluginManager<Update, List<PartialBotApiMethod>> pluginManager, RepositoryManager repositoryManager) {
        this.pluginManager = pluginManager;
        this.repositoryManager = repositoryManager;
    }

    @Override
    public String getPluginId() {
        return nameAdministrationPlugin;
    }

    @Setter
    private CommandUtils commandUtils = new CommandUtils();

    @Override
    public List<PartialBotApiMethod> onEvent(Update event) {
        if (!event.hasMessage() || !event.getMessage().hasText()) {
            return Collections.emptyList();
        }

        if (event.getMessage().isCommand()) {
            try {
                executeCommand(event.getMessage());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return Collections.emptyList();
        }

        return null;
    }
    private List<PartialBotApiMethod> executeCommand(Message message) throws IOException {
        var text = commandUtils.normalizeCmdMsg(message.getText());
        switch (text) {
            case "addStatsPlug":
                pluginManager.enablePlugin(new WordStatisticPlugin(new Statistics(repositoryManager.getWordStorageRepository()), repositoryManager.getUserRepository()));
                return Collections.emptyList();
            case "addTransPlug":
                pluginManager.enablePlugin(TranslateBotPlugin.newYandexTranslatePlugin());
                return Collections.emptyList();
            case "addLimitPlug":
                pluginManager.enablePlugin(new WordLimiterPlugin(repositoryManager.getUnsubscribeRepository()));
                return Collections.emptyList();
            case "delStatsPlug":
                pluginManager.disablePlugin("statsPlug");
                return Collections.emptyList();
            case "delTransCliPlug":
                pluginManager.disablePlugin("transPlug");
                return Collections.emptyList();
            case "delLimitPlug":
                pluginManager.disablePlugin("transPlug");
                return Collections.emptyList();
        }
        return Collections.emptyList();
    }
}
