package page.devnet.telegrambot;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import page.devnet.pluginmanager.Plugin;
import page.devnet.telegrambot.util.CommandUtils;
import page.devnet.telegrambot.util.ParserMessage;

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
        RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(5000)
                .setConnectionRequestTimeout(5000)
                .setSocketTimeout(5000).build();
        client = HttpClientBuilder.create()
                .setDefaultRequestConfig(config)
                .build();
        log.info("Start Wolfram Alpha plugin");
    }

    @Override
    public String getPluginId() {
        return "wolframalphaPlug";
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
        if (command.equals("wolfram")) {
            try {
                String result = requestToWolfram(commandParameter);
                return List.of(new SendMessage(chatId, result));
            } catch (IOException | URISyntaxException e) {
                log.error("Error in execute command message {}, error {}", e.getMessage(), e);
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
            log.error("Error uri syntax in requestToWolfram, message: {}, error {}", e.getMessage(), e);
            throw e;
        }

        HttpGet get = new HttpGet(uri);
        get.setHeader("Content-Type", "text/plain;charset=utf-8");
        log.info("Wolfram try execute request");
        HttpResponse response = tryExecute(get);
        log.info("Wolfram end execute request");
        StringBuilder result = new StringBuilder();
        int responseCode = response.getStatusLine().getStatusCode();
        switch (responseCode) {
            case 200 -> {
                log.info("Response code 200");
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
            }
            case 400 -> {
                log.error("Error while requesting WolframAlpha: code {}, message: {} ", 400, response.getStatusLine().getReasonPhrase());
                result.append(response.getStatusLine().getReasonPhrase());
                return result.toString();
            }
            case  501 ->  {
                log.error("Error while requesting WolframAlpha: code {}, message: {} ", 501, response.getStatusLine().getReasonPhrase());
                result.append(response.getStatusLine().getReasonPhrase());
                return result.toString();
            }
            default -> {
                log.error("Error while requesting WolframAlpha: code {}, message: {} ", responseCode, response.getStatusLine().getReasonPhrase());
                result.append("Error code: ").append(responseCode).append(", reason: ").append(response.getStatusLine().getReasonPhrase());
                return result.toString();
            }
        }
    }

    private HttpResponse tryExecute(HttpGet get) throws IOException {
        try {
            return client.execute(get);
        } catch (IOException e) {
            log.error("Error while requesting execute WolframAlpha: {}", e.getMessage());
            throw e;
        }
    }
}
