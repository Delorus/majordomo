package page.devnet.app.translate;

import lombok.extern.slf4j.Slf4j;
import page.devnet.pluginmanager.BotApiMethod;
import page.devnet.pluginmanager.Message;
import page.devnet.pluginmanager.Plugin;
import page.devnet.pluginmanager.Update;
import page.devnet.pluginmanager.User;
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
public final class TranslateBotPlugin implements Plugin {

    public static TranslateBotPlugin newYandexTranslatePlugin() {
        TranslateService service = new YandexTranslateService(System.getenv("YNDX_TRNSL_API_KEY"));

        return new TranslateBotPlugin(service);
    }

    private final TranslateService service;

    public TranslateBotPlugin(TranslateService service) {
        this.service = service;
    }

    @Override
    public List<BotApiMethod> onUpdate(Update update) {
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
            result.add(BotApiMethod.newDeleteMessage(chatID, inMsg.getMessageId()));
            result.add(BotApiMethod.newSendMarkdownMessage(chatID, response));
            return result;
        } catch (TranslateException e) {
            log.error(e.getMessage(),e);
            return Collections.singletonList(BotApiMethod.newSendMessage(inMsg.getChatId(), "Извините, произошла ошибка."));
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
