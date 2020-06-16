package page.devnet.telegrambot.translate;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import page.devnet.pluginmanager.Plugin;
import page.devnet.telegrambot.util.CommandUtils;
import page.devnet.translate.TranslateService;
import page.devnet.translate.yandex.YandexTranslateService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author maksim
 * @since 23.03.19
 */
@Slf4j
public final class TranslateBotPlugin implements Plugin<Update, List<PartialBotApiMethod>> {

    //todo it's not thread safe
    private boolean stop = false;
    private final String nameTranslatePlugin = "transPlug";
    @Setter
    private CommandUtils commandUtils = new CommandUtils();

    public static TranslateBotPlugin newYandexTranslatePlugin() {
        TranslateService service = new YandexTranslateService(System.getenv("YNDX_TRNSL_API_KEY"));

        return new TranslateBotPlugin(service);
    }

    private final TranslateService service;

    public TranslateBotPlugin(TranslateService service) {
        this.service = service;
    }

    @Override
    public String getPluginId() {
        return nameTranslatePlugin;
    }

    @Override
    public List<PartialBotApiMethod> onEvent(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return Collections.emptyList();
        }

        return dispatch(update);
    }

    private List<PartialBotApiMethod> dispatch(Update update) {
        if (update.getMessage().isCommand()) {
            return executeCommand(update.getMessage());
        }

        if (stop) {
            return Collections.emptyList();
        }

        return translate(update);
    }

    private List<PartialBotApiMethod> executeCommand(Message message) {
        String command = commandUtils.normalizeCmdMsg(message.getText());
        switch (command) {
            case "stoptrans":
                stop = true;
                break;
            case "starttrans":
                stop = false;
                break;
        }
        return Collections.emptyList();
    }

    private List<PartialBotApiMethod> translate(Update update) {
        Message inMsg = update.getMessage();
        try {
            String en = service.transRuToEn(inMsg.getText());
            if (en.isEmpty()) {
                return Collections.emptyList();
            }

            Long chatID = inMsg.getChatId();

            String response = "*" + formatUserName(update.getMessage().getFrom()) + " wrote:*\n" + en;

            List<PartialBotApiMethod> result = new ArrayList<>();
            result.add(new DeleteMessage(chatID, inMsg.getMessageId()));
            result.add(new SendMessage(chatID, response).enableMarkdown(true));
            return result;
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            return Collections.singletonList(new SendMessage(inMsg.getChatId(), "Извините, произошла ошибка."));
        }
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
