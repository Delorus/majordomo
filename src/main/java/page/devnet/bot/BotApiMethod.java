package page.devnet.bot;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;

/**
 * @author maksim
 * @since 10.11.2019
 */
public class BotApiMethod {

    public static BotApiMethod from(org.telegram.telegrambots.meta.api.methods.BotApiMethod<?> origin) {
        return new BotApiMethod(origin);
    }

    public static BotApiMethod newSendMessage(Long chatId, String message) {
        return new BotApiMethod(new SendMessage(chatId, message));
    }

    public static BotApiMethod newSendMarkdownMessage(Long chatId, String message) {
        return new BotApiMethod(new SendMessage(chatId, message).enableMarkdown(true));
    }

    public static BotApiMethod newDeleteMessage(Long chatId, Integer messageId) {
        return new BotApiMethod(new DeleteMessage(chatId, messageId));
    }

    private final org.telegram.telegrambots.meta.api.methods.BotApiMethod<?> origin;

    private BotApiMethod(org.telegram.telegrambots.meta.api.methods.BotApiMethod<?> origin) {
        this.origin = origin;
    }


    public org.telegram.telegrambots.meta.api.methods.BotApiMethod<?> unwrap() {
        return origin;
    }
}
