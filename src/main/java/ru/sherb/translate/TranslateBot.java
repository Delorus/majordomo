package ru.sherb.translate;

import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.sherb.translate.yandex.TranslateServiceImpl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * @author maksim
 * @since 01.03.19
 */
public final class TranslateBot extends TelegramWebhookBot {

    private final TranslateService service;

    private final Map<String, Function<Update, BotApiMethod>> commands = new HashMap<>();

    private final String name;
    private final String token;
    private final String path;

    private Long enCharID;
    private Long ruChatID;

    public TranslateBot(String name, String token, String path) {
        super();
        this.name = name;
        this.token = token;
        this.path = path;
        service = new TranslateServiceImpl("trnsl.1.1.20190302T173434Z.b9d42857e1f5463e.8fdc17ce5df7d4a0aee3a9be6ade62b96671e05f");

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

    @Override
    public BotApiMethod onWebhookUpdateReceived(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return null;
        }

        return dispatch(update);
    }

    private static final Pattern cyrillic = Pattern.compile("[А-Яа-я]");

    private BotApiMethod dispatch(Update update) {
        if (update.getMessage().isCommand()) {
            return executeCmd(update);
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

    private BotApiMethod translate(Update update) {
        if (filterMsg(update)) {
            return null;
        }

        Message inMsg = update.getMessage();
        try {
            String ru = service.transRuToEn(inMsg.getText());
            return new SendMessage(inMsg.getChatId(), ru);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            return new SendMessage(inMsg.getChatId(), "Извините, произошла ошибка.");
        }
    }

    private boolean filterMsg(Update update) {
        String text = update.getMessage().getText();

        return !cyrillic.matcher(text).find();
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
