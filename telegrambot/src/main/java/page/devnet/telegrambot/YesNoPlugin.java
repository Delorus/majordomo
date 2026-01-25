package page.devnet.telegrambot;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.client.WebClient;
import lombok.Data;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.botapimethods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendAnimation;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import page.devnet.common.webclient.WebClientFactory;
import page.devnet.pluginmanager.Plugin;
import page.devnet.telegrambot.util.CommandUtils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;

/**
 * @author mshherbakov
 * @since 18.06.2021
 */
@Slf4j
public class YesNoPlugin implements Plugin<Update, List<PartialBotApiMethod<?>>> {

    private static final String API_URL = "https://yesno.wtf/api";
    private final WebClient client;
    private final ObjectMapper mapper = new ObjectMapper();
    private static final int HTTP_TIMEOUT = 10;

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
        log.info("Start Yes No plugin");
    }

    protected WebClient createWebClient() {
        return WebClientFactory.createWebClient(Vertx.vertx());
    }

    @Override
    public List<PartialBotApiMethod<?>> onEvent(Update event) {
        if (!event.hasMessage() || !event.getMessage().hasText()) {
            return Collections.emptyList();
        }

        if (event.getMessage().isCommand()) {
            return executeCommand(event.getMessage());
        }

        return Collections.emptyList();
    }

    private List<PartialBotApiMethod<?>> executeCommand(Message message) {

        var command = commandUtils.normalizeCmdMsg(message.getText());
        try {
            switch (command) {
                case "yes": {
                    var image = tryExecute("yes");
                    if (image == null) {
                        return Collections.emptyList();
                    }
                    return List.of(
                            new SendAnimation(String.valueOf(message.getChatId()), image)
                    );
                }
                case "no": {
                    var image = tryExecute("no");
                    if (image == null) {
                        return Collections.emptyList();
                    }
                    return List.of(
                            new SendAnimation(String.valueOf(message.getChatId()), image)
                    );
                }
                case "maybe": {
                    var image = tryExecute("maybe");
                    if (image == null) {
                        return Collections.emptyList();
                    }
                    return List.of(
                            new SendAnimation(String.valueOf(message.getChatId()), image)
                    );
                }
            }
        } catch (TimeoutException e) {
            log.error("Timeout occurred while fetching the image: {}", e.getMessage());
            return List.of(new SendMessage(message.getChatId().toString(), "The request to the external service timed out. Please try again later."));
        } catch (Exception e) {
            log.error("An error occurred: {}", e.getMessage());
            return List.of(new SendMessage(message.getChatId().toString(), "An error occurred while processing your request. Please try again later."));
        }
        return Collections.emptyList();
    }

    private InputFile tryExecute(String type) throws TimeoutException{
        CompletableFuture<InputFile> apiResponseCompletableFuture = new CompletableFuture<>();
        try {
            client.getAbs(API_URL)
                    .addQueryParam("force", type)
                    .send()
                    .onSuccess(resp -> {
                        if (resp.statusCode() == 200) {
                            try {
                                ApiResponse respModel = mapper.readValue(resp.bodyAsString(), ApiResponse.class);
                                client.getAbs(respModel.getImage()) // Отправляем GET-запрос по указанному URL
                                        .send()
                                        .onSuccess(imageResp -> {
                                            if (imageResp.statusCode() == 200) {
                                                Buffer body = imageResp.body(); // Получаем тело ответа
                                                InputStream inputStream = new ByteArrayInputStream(body.getBytes());
                                                InputFile inputFile = new InputFile(inputStream, respModel.getImage().substring(respModel.getImage().lastIndexOf("/") + 1));
                                                apiResponseCompletableFuture.complete(inputFile);
                                            } else {
                                                log.warn("Failed to get image response from yesno.wtf, status code: {}", imageResp.statusCode());
                                                apiResponseCompletableFuture.completeExceptionally(new Exception("Failed to get image response from yesno.wtf, status code: " + imageResp.statusCode()));
                                            }
                                        })
                                        .onFailure(e -> {
                                            log.warn("Failed to get image response from yesno.wtf {}", e.getMessage());
                                            apiResponseCompletableFuture.completeExceptionally(e);
                                        });
                            } catch (JsonProcessingException ex) {
                                log.error("Failed to parse response from yesno.wtf {}", ex.getMessage());
                                apiResponseCompletableFuture.completeExceptionally(ex);
                            }
                        } else {
                            apiResponseCompletableFuture.completeExceptionally(new Exception("Failed to get response from yesno.wtf, code: " + resp.statusCode()));
                        }
                    })
                    .onFailure(e -> {
                        log.warn("Failed to get response from yesno.wtf {}", e.getMessage());
                        apiResponseCompletableFuture.completeExceptionally(e);
                    });
            return apiResponseCompletableFuture.
                    orTimeout(HTTP_TIMEOUT, TimeUnit.SECONDS)
                    .exceptionally(e -> {
                        log.error("Timeout for YesNo requst. ", e);
                        throw new CompletionException(new TimeoutException("Request to YesNo service timed out."));
                    })
                    .get();
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            if (cause instanceof TimeoutException) {
                // таймаут
                log.error("Request to external service timed out.", cause);
                throw new TimeoutException("Request to external service timed out.");
            } else {
                // Обработка других ошибок
                log.error("Error during processing: ", cause);
                throw new RuntimeException("Error during processing:", e);

            }
        } catch (InterruptedException e) {
            log.error("Failed to process response from yesno.wtf {}", e.getMessage());
            throw new RuntimeException("Error during the processing of the response.", e);
        }
    }
}
