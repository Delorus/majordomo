package page.devnet.telegrambot;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.core.Vertx;
import io.vertx.ext.web.client.WebClient;
import lombok.Data;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import page.devnet.common.webclient.WebClientFactory;
import page.devnet.pluginmanager.Plugin;
import page.devnet.telegrambot.util.CommandUtils;
import page.devnet.vertxtgbot.tgapi.SendExternalAnimation;

import java.util.Collections;
import java.util.List;

/**
 * @author mshherbakov
 * @since 18.06.2021
 */
@Slf4j
public class YesNoPlugin implements Plugin<Update, List<PartialBotApiMethod<?>>> {

    private static final String API_URL = "https://yesno.wtf/api";
    private final WebClient client;
    private final ObjectMapper mapper = new ObjectMapper();

    @Data
    private static class ApiResponse {
        private String answer;
        private Boolean forced;
        private String image;
    }

    @Override
    public String getPluginId() {
        return "yesnoPlug";
    }

    @Setter
    private CommandUtils commandUtils = new CommandUtils();

    public YesNoPlugin() {
        this.client = createWebClient();
    }

    protected WebClient createWebClient() {
        return WebClientFactory.createWebClient(Vertx.vertx());
    }

    @Override
    public List<PartialBotApiMethod<?>> onEvent(Update event) {
        if (!event.hasMessage() || !event.getMessage().hasText()) {
            return Collections.emptyList();
        }

        if (!event.getMessage().isCommand()) {
            return Collections.emptyList();
        }

        return executeCommand(event.getMessage());
    }

    private List<PartialBotApiMethod<?>> executeCommand(Message message) {
        final var msgId = message.getMessageId();

        var command = commandUtils.normalizeCmdMsg(message.getText());
        switch (command) {
            case "yes": {
                var image = tryExecute("yes");
                if (image == null) {
                    return Collections.emptyList();
                }

                return List.of(
                        new SendExternalAnimation(String.valueOf(message.getChatId()), image)
                );
            }
            case "no": {
                var image = tryExecute("no");
                if (image == null) {
                    return Collections.emptyList();
                }

                return List.of(
                        new SendExternalAnimation(String.valueOf(message.getChatId()), image)
                );
            }
            case "maybe": {
                var image = tryExecute("maybe");
                if (image == null) {
                    return Collections.emptyList();
                }

                return List.of(
                        new SendExternalAnimation(String.valueOf(message.getChatId()), image)
                );
            }
        }

        return Collections.emptyList();
    }

    private String tryExecute(String type) {
        try {
            var response = client.getAbs(API_URL)
                    .addQueryParam("force", type)
                    .send()
                    .onFailure(e -> log.warn("Failed to get response from yesno.wtf", e))
                    .result();

            if (response.statusCode() != 200) {
                log.warn("Failed to get response from yesno.wtf, status code: {}", response.statusCode());
                return null;
            }

            ApiResponse resp = mapper.readValue(response.bodyAsString(), ApiResponse.class);
            return resp.image;
        } catch (Exception e) {
            log.warn("Failed to process response from yesno.wtf", e);
            return null;
        }
    }
}
