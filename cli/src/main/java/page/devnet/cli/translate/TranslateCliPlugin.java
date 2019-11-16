package page.devnet.cli.translate;

import page.devnet.cli.Commandable;
import page.devnet.pluginmanager.BotApiMethod;
import page.devnet.pluginmanager.Message;
import page.devnet.pluginmanager.Plugin;
import page.devnet.pluginmanager.Update;
import page.devnet.translate.TranslateException;
import page.devnet.translate.TranslateService;
import page.devnet.translate.yandex.YandexTranslateService;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author maksim
 * @since 16.11.2019
 */
public class TranslateCliPlugin implements Plugin, Commandable {

    public static TranslateCliPlugin newYandexTranslatePlugin() {
        TranslateService service = new YandexTranslateService(System.getenv("YNDX_TRNSL_API_KEY"));

        return new TranslateCliPlugin(service);
    }

    private final TranslateService service;

    public TranslateCliPlugin(TranslateService service) {
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

            return List.of(BotApiMethod.newSendMarkdownMessage(1l, en));
        } catch (TranslateException e) {
            System.err.println(e);
            return Collections.singletonList(BotApiMethod.newSendMessage(inMsg.getChatId(), "Извините, произошла ошибка."));
        }
    }

    @Override
    public String serviceName() {
        return "Translate service";
    }

    @Override
    public Map<String, String> commandDescriptionList() {
        return Map.of(
                ":en", "translate all line after command to English",
                ":stopTranslate", "stop translating");
    }
}
