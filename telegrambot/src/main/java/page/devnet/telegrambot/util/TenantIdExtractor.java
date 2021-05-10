package page.devnet.telegrambot.util;

import org.telegram.telegrambots.meta.api.objects.Update;
import page.devnet.pluginmanager.MultiTenantPluginManager;

import java.util.function.Function;

/**
 * @author sherb
 * @since 10.05.2021
 */
public class TenantIdExtractor implements Function<Update, String> {

    @Override
    public String apply(Update update) {
        if (update.hasMessage()) {
            return String.valueOf(update.getMessage().getChatId());
        }
        if (update.hasEditedMessage()) {
            return String.valueOf(update.getEditedMessage().getChatId());
        }
        if (update.hasChannelPost()) {
            return String.valueOf(update.getChannelPost().getChatId());
        }
        if (update.hasEditedChannelPost()) {
            return String.valueOf(update.getEditedChannelPost().getChatId());
        }
        if (update.hasCallbackQuery() && update.getCallbackQuery().getMessage() != null) {
            return String.valueOf(update.getCallbackQuery().getMessage().getChatId());
        }
//        if (update.hasChatMember()) {
//            return String.valueOf(update.getChatMember().getChat().getId());
//        }
//        if (update.hasMyChatMember()) {
//            return String.valueOf(update.getMyChatMember().getChat().getId());
//        }

        return MultiTenantPluginManager.NO_TENANT;
    }
}
