package ru.sherb.bot;

import lombok.Builder;
import lombok.Value;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author maksim
 * @since 01.03.19
 */
public final class BotManager {

    @Value
    @Builder
    public static class Setting {
        String name;
        String token;
        String path;
    }

    private final String name;
    private final String token;
    private final String path;
    private final List<BotPlugin> plugins = new ArrayList<>();

    public BotManager(Setting setting, BotPlugin plugin, BotPlugin... plugins) {
        super();
        this.name = setting.name;
        this.token = setting.token;
        this.path = setting.path;
        this.plugins.add(plugin);
        this.plugins.addAll(Arrays.asList(plugins));
    }

    public TelegramWebhookBot atProductionBotManager() {
        return new ProdBotManager();
    }

    public TelegramLongPollingBot atDevBotManager() {
        return new DevBotManager();
    }

    private class ProdBotManager extends TelegramWebhookBot {

        @Override
        public BotApiMethod onWebhookUpdateReceived(Update update) {
            for (BotPlugin plugin : plugins) {
                List<BotApiMethod> response = plugin.onUpdate(update);
                execute(response);
            }
            return null; //todo return last msg
        }

        private void execute(List<BotApiMethod> response) {
            try {
                for (BotApiMethod method : response) {
                    execute(method);
                }
            } catch (TelegramApiException e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        }

        @Override
        public String getBotUsername() {
            return name;
        }

        @Override
        public String getBotToken() {
            return token;
        }

        @Override
        public String getBotPath() {
            return path;
        }
    }

    private class DevBotManager extends TelegramLongPollingBot {

        @Override
        public void onUpdateReceived(Update update) {
            for (BotPlugin plugin : plugins) {
                List<BotApiMethod> response = plugin.onUpdate(update);
                execute(response);
            }
        }

        private void execute(List<BotApiMethod> response) {
            try {
                for (BotApiMethod method : response) {
                    execute(method);
                }
            } catch (TelegramApiException e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        }

        @Override
        public String getBotUsername() {
            return name;
        }

        @Override
        public String getBotToken() {
            return token;
        }
    }
}
