package page.devnet.cli.translate;

import page.devnet.cli.Commandable;
import page.devnet.cli.Event;
import page.devnet.pluginmanager.Plugin;
import page.devnet.translate.TranslateService;
import page.devnet.translate.yandex.YandexTranslateService;

import java.util.Map;

/**
 * @author maksim
 * @since 16.11.2019
 */
public class TranslateCliPlugin implements Plugin<Event, String>, Commandable {

    private final String nameWordTranslatePlugin = "transPlug";

    @Override
    public String getPluginId() {
        return nameWordTranslatePlugin;
    }

    public static TranslateCliPlugin newYandexTranslatePlugin() {
        TranslateService service = new YandexTranslateService(System.getenv("YNDX_TRNSL_API_KEY"));

        return new TranslateCliPlugin(service);
    }

    private final TranslateService service;

    private TranslateMode translateMode = TranslateMode.NONE;

    private enum TranslateMode {
        EN, RU, NONE
    }

    public TranslateCliPlugin(TranslateService service) {
        this.service = service;
    }


    @Override
    public String onEvent(Event event) {
        if (event.isEmpty()) {
            return "";
        }

        return dispatch(event);
    }

    private String dispatch(Event event) {
        if (event.isCommand()) {
            return executeCommand(event);
        }

        return translate(event);
    }

    private String executeCommand(Event event) {
        switch (event.getText()) {
            case ":en":
                translateMode = TranslateMode.EN;
                return "set up translate to English";
            case ":ru":
                translateMode = TranslateMode.RU;
                return "set up translate to Russian";
            case ":stopTranslate":
                translateMode = TranslateMode.NONE;
                return "stop translating";
            default:
                return executeInlineCommand(event);
        }
    }

    private String executeInlineCommand(Event event) {
        var oldMode = translateMode;
        var text = event.getText();
        if (text.startsWith(":ru ")) {
            translateMode = TranslateMode.RU;
        } else if (text.startsWith(":en ")) {
            translateMode = TranslateMode.EN;
        } else {
            return "";
        }

        var translated = translate(event);
        translateMode = oldMode;
        return translated;
    }

    private String translate(Event event) {
        String text = event.getText();

        switch (translateMode) {
            case EN:
                return service.transRuToEn(text);
            case RU:
                return service.transEnToRu(text);
            case NONE:
                return "";
        }
        return "";
    }

    @Override
    public String serviceName() {
        return "Translate service";
    }

    @Override
    public Map<String, String> commandDescriptionList() {
        return Map.of(
                ":en", "translate all line after command to English. If you write text on the same line after command, only it will be translated",
                ":ru", "translate all line after command to Russian. If you write text on the same line after command, only it will be translated",
                ":stopTranslate", "stop translating");
    }
}
