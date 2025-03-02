package page.devnet.telegrambot;

import io.vertx.core.Vertx;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.codec.BodyCodec;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import page.devnet.common.webclient.WebClientFactory;
import page.devnet.pluginmanager.Plugin;
import page.devnet.telegrambot.util.CommandUtils;
import page.devnet.telegrambot.util.ParserMessage;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
public class WolframAlphaBotPlugin implements Plugin<Update, List<PartialBotApiMethod<?>>> {
    private static final int HTTP_TIMEOUT = 5000;
    private static final String API_URL = "http://api.wolframalpha.com/v1/result";

    @Setter
    private CommandUtils commandUtils = new CommandUtils();
    private final WebClient client;
    private final String apiKey;

    public WolframAlphaBotPlugin(Vertx vertx) {
        this.client = WebClientFactory.createWebClient(vertx, HTTP_TIMEOUT);
        this.apiKey = System.getenv("WOLFRAM_API_KEY");
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
            } catch (Exception e) {
                log.error("Error in execute command: {}", e.getMessage());
                return List.of(new SendMessage(chatId, e.getMessage()));
            }
        }
        return Collections.emptyList();
    }

    private String requestToWolfram(String request) {
        CompletableFuture<String> future = new CompletableFuture<>();

        client.getAbs(API_URL)
            .addQueryParam("appid", apiKey)
            .addQueryParam("i", request)
            .putHeader("Content-Type", "text/plain;charset=utf-8")
            .as(BodyCodec.string())
            .send()
            .onSuccess(response -> {
                int statusCode = response.statusCode();
                String responseBody = response.body();

                switch (statusCode) {
                    case 200 -> {
                        log.info("Response code 200");
                        future.complete(responseBody);
                    }
                    case 400 -> {
                        String error = "Error while requesting WolframAlpha: code 400";
                        log.error(error);
                        future.complete(error);
                    }
                    case 501 -> {
                        String error = "Error while requesting WolframAlpha: code 501";
                        log.error(error);
                        future.complete(error);
                    }
                    default -> {
                        String error = String.format("Error while requesting WolframAlpha: code %d", statusCode);
                        log.error(error);
                        future.complete(error);
                    }
                }
            })
            .onFailure(error -> {
                String message = error.getMessage();
                log.error("Error while requesting WolframAlpha: {}", message);
                future.complete("Error to execute request: " + message);
            });

        try {
            return future.get(HTTP_TIMEOUT, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            log.error("Failed to get response: {}", e.getMessage());
            return "Failed to get response: " + e.getMessage();
        }
    }
}
