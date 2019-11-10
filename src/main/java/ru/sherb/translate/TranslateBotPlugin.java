package ru.sherb.translate;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.sherb.bot.BotPlugin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * @author maksim
 * @since 23.03.19
 */
@Slf4j
public final class TranslateBotPlugin implements BotPlugin {

    private final TranslateService service;

    private final Map<String, Function<Update, BotApiMethod>> commands = new HashMap<>();

    private Long enCharID;
    private Long ruChatID;

    public TranslateBotPlugin(TranslateService service) {
        this.service = service;

        commands.put("ru_chat", upd -> {
            Long chatId = upd.getMessage().getChatId();
            if (chatId.equals(this.ruChatID)) {
                return new SendMessage(chatId, "Прошу прощения, но этот чат и так обозначен как русский.");
            } else if (chatId.equals(this.enCharID)) {
                this.ruChatID = chatId;
                return new SendMessage(chatId, "Ваше указание выполнено, теперь этот чат определяется как русский и английский.");
            } else {
                this.ruChatID = chatId;
                return new SendMessage(chatId, "Ваше указание выполнено, теперь этот чат определяется как русский.");
            }
        });
        commands.put("en_chat", upd -> {
            Long chatId = upd.getMessage().getChatId();
            if (chatId.equals(this.enCharID)) {
                return new SendMessage(chatId, "I apologize, but this chat is denoted as english.");
            } else if (chatId.equals(this.ruChatID)) {
                this.enCharID = chatId;
                return new SendMessage(chatId, "Your instruction is fulfilled, now this chat is defined as english and russian.");
            } else {
                this.enCharID = chatId;
                return new SendMessage(chatId, "Your instruction is fulfilled, now this chat is defined as english.");
            }
        });
    }

    public List<BotApiMethod> onUpdate(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return Collections.emptyList();
        }

        return dispatch(update);
    }

    private List<BotApiMethod> dispatch(Update update) {
        if (update.getMessage().isCommand()) {
            return Collections.singletonList(executeCmd(update));
        } else {
            return translate(update);
        }
    }

    private BotApiMethod executeCmd(Update upd) {
        String command = normalizeCmdMsg(upd.getMessage().getText());
        return commands.getOrDefault(command, __ ->
                new SendMessage(upd.getMessage().getChatId(), "Извиняюсь, но я не понимаю о чем Вы меня просите.")
        ).apply(upd);
    }

    private String normalizeCmdMsg(String text) {
        int begin = 0;
        int end = text.length();

        if (text.startsWith("/")) {
            begin = 1;
        }

        if (text.contains("@")) {
            end = text.indexOf('@');
        }

        return text.substring(begin, end);
    }

    //todo replace BotApiMethod to special class (e.g. TelegramAnswer)
    private List<BotApiMethod> translate(Update update) {
        if (!isSupportedMsg(update)) {
            return Collections.emptyList();
        }

        Message inMsg = update.getMessage();
        try {
            String en = service.transRuToEn(inMsg.getText());

            Long chatID = this.enCharID != null
                    ? this.enCharID
                    : inMsg.getChatId();

            String response = "*" + formatUserName(update.getMessage().getFrom()) + " wrote:*\n" + en;

            List<BotApiMethod> result = new ArrayList<>();
            if (this.enCharID == null) {
                result.add(new DeleteMessage(chatID, inMsg.getMessageId()));
            }
            result.add(new SendMessage(chatID, response).enableMarkdown(true));
            return result;
        } catch (IOException e) {
            log.error(e.getMessage(),e);
            return Collections.singletonList(new SendMessage(inMsg.getChatId(), "Извините, произошла ошибка."));
        }
    }

    private static final Pattern cyrillic = Pattern.compile("[А-Яа-я]");

    private boolean isSupportedMsg(Update update) {
        String text = update.getMessage().getText();

        return cyrillic.matcher(text).find();
    }

    private String formatUserName(User user) {
        String name = user.getUserName();
        if (user.getFirstName() != null && !user.getFirstName().isEmpty()) {
            name = user.getFirstName();

            if (user.getLastName() != null && !user.getLastName().isEmpty()) {
                name += " " + user.getLastName();
            }
        }

        return name;
    }
}
