package page.devnet.telegrambot.translate;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import page.devnet.pluginmanager.Plugin;
import page.devnet.translate.TranslateException;
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
public final class TranslateBotPlugin implements Plugin<Update, List<BotApiMethod>> {

    public static TranslateBotPlugin newYandexTranslatePlugin() {
        TranslateService service = new YandexTranslateService(System.getenv("YNDX_TRNSL_API_KEY"));

        return new TranslateBotPlugin(service);
    }

    private final TranslateService service;

    public TranslateBotPlugin(TranslateService service) {
        this.service = service;
    }

    @Override
    public List<BotApiMethod> onEvent(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return Collections.emptyList();
        }

        return dispatch(update);
    }

    private List<BotApiMethod> dispatch(Update update) {
        return translate(update);
    }

    private List<BotApiMethod> translate(Update update) {
        Message inMsg = update.getMessage();
        try {
            String en = service.transRuToEn(inMsg.getText());

            Long chatID = inMsg.getChatId();

            String response = "*" + formatUserName(update.getMessage().getFrom()) + " wrote:*\n" + en;

            List<BotApiMethod> result = new ArrayList<>();
            result.add(new DeleteMessage(chatID, inMsg.getMessageId()));
            result.add(new SendMessage(chatID, response).enableMarkdown(true));
            return result;
        } catch (TranslateException e) {
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
