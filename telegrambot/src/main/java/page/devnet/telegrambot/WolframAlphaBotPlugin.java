package page.devnet.telegrambot;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClients;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import page.devnet.pluginmanager.Plugin;
import page.devnet.telegrambot.util.CommandUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

@Slf4j
public class WolframAlphaBotPlugin implements Plugin<Update, List<PartialBotApiMethod<?>>> {
    @Setter
    private CommandUtils commandUtils = new CommandUtils();
    private final HttpClient client;

    public WolframAlphaBotPlugin() {
        client = HttpClients.createMinimal();
    }

    @Override
    public String getPluginId() {
        return "wolframalphaPlug";
    }

    @Override
    public List<PartialBotApiMethod<?>> onEvent(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return Collections.emptyList();
        }
        if (update.getMessage().isCommand()) {
            return executeCommand(update);
        }
        return List.of();
    }

    private List<PartialBotApiMethod<?>> executeCommand(Update update) {
        Message message = update.getMessage();
        String command = commandUtils.normalizeCmdMsg(message.getText());
        var chatId = String.valueOf(message.getChatId());
        if (command.equals("wolfram")) {
            try {
                String result = requestToWolfram(message.getText());
                return List.of(new SendMessage(chatId, result));
            } catch (IOException | URISyntaxException e) {
                log.error(e.getMessage(), e);
                return List.of(new SendMessage(chatId, e.getMessage()));
            }
        }
        return Collections.emptyList();
    }


    private String requestToWolfram(String request) throws URISyntaxException, IOException {
        URI uri;
        try {
            String wolframAlphaUri = "http://api.wolframalpha.com/v1/result";
            uri = new URIBuilder(wolframAlphaUri)
                    .addParameter("appid", System.getenv("WOLFRAM_API_KEY"))
                    .addParameter("i", request)
                    .build();
        } catch (URISyntaxException e) {
            log.error(e.getMessage(), e);
            throw e;
        }

        HttpGet get = new HttpGet(uri);
        get.setHeader("Content-Type", "text/plain;charset=utf-8");

        HttpResponse response = tryExecute(get, request);
        StringBuilder result = new StringBuilder();
        if (response.getStatusLine().getStatusCode() ==  200) {
            try (InputStreamReader reader = new InputStreamReader(response.getEntity().getContent(), StandardCharsets.UTF_8)) {
                int b;
                while ((b = reader.read()) != -1) {
                    result.append((char) b);
                }
            } catch (IOException e) {
                log.error(e.getMessage(), e);
                throw e;
            }
            return result.toString();
        }else {
            return "";
        }
    }
    private HttpResponse tryExecute(HttpGet get, String text) throws IOException {
        try {
            return client.execute(get);
        } catch (IOException e) {
            log.error("Error while requesting execute WolframAlpha: {}", text);
            throw e;
        }
    }
}
