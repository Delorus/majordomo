package page.devnet.telegrambot.convertercurrency;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.HttpClientBuilder;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import page.devnet.convertercurrency.ConverterCurrencyException;
import page.devnet.convertercurrency.ConverterCurrencyService;
import page.devnet.pluginmanager.Plugin;
import page.devnet.telegrambot.util.CommandUtils;
import page.devnet.telegrambot.util.ParserMessage;
import page.devnet.translate.TranslateService;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;

@Slf4j
public class CurrencyRatePlugin implements Plugin<Update, List<PartialBotApiMethod<?>>> {
    @Setter
    private CommandUtils commandUtils = new CommandUtils();
    private final ConverterCurrencyService [] services;

    public CurrencyRatePlugin(ConverterCurrencyService ... services) {
        log.info("Start Currency Rate plugin");
        this.services = services;
    }

    @Override
    public String getPluginId() {
        return "currencyPlug";
    }

    @Override
    public List<PartialBotApiMethod<?>> onEvent(Update update) {
        log.debug("wolfram plugin onEvent on Thread id: {}", Thread.currentThread().threadId());
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return Collections.emptyList();
        }
        if (update.getMessage().isCommand()) {
            return executeCommand(update.getMessage());
        }
        return List.of();
    }

    private List<PartialBotApiMethod<?>> executeCommand(Message message) {
        ParserMessage parserMessage = new ParserMessage();
        String command = commandUtils.normalizeCmdMsgWithParameter(message.getText());
        var commandParameter = parserMessage.getCommandParameterFromMessage(message.getText());
        var chatId = String.valueOf(message.getChatId());
        if (command.equals("convert")) {
            StringBuilder errorMessage = new StringBuilder();
            for (ConverterCurrencyService service : services) {
                try {
                    var result = service.convert(commandParameter);
                    if (!result.isEmpty()) {
                        return List.of(new SendMessage(chatId, result));
                    }
                }catch (ConverterCurrencyException e){
                    log.error("Error converting currency", e);
                    errorMessage.append(e).append("\n");
                }
            }
            return List.of(new SendMessage(chatId, "Sorry, currencies service not available." + errorMessage));
        } else {
            return Collections.emptyList();
        }
    }
}
