package page.devnet.telegrambot;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import page.devnet.hacks.BufferPipedInputStream;
import page.devnet.pluginmanager.Plugin;
import page.devnet.telegrambot.util.CommandUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

/**
 * @author mshherbakov
 * @since 18.06.2021
 */
@Slf4j
public class YesNoPlugin implements Plugin<Update, List<PartialBotApiMethod<?>>> {

    private static final String API_URL = "https://yesno.wtf";
    private final HttpClient client;
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
        this.client = HttpClients.createMinimal();
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

                return List.of(
                        new SendVideo(String.valueOf(message.getChatId()), new InputFile(image, "yes"))
                );
            }
            case "no": {
                var image = tryExecute("no");

                return List.of(
                        new SendVideo(String.valueOf(message.getChatId()), new InputFile(image, "yes"))
                );
            }
            case "maybe": {
                var image = tryExecute("maybe");

                return List.of(
                        new SendVideo(String.valueOf(message.getChatId()), new InputFile(image, "yes"))
                );
            }
        }

        return Collections.emptyList();
    }

    private InputStream tryExecute(String type) {
        try {
            HttpResponse response = client.execute(HttpHost.create(API_URL), new HttpGet("/api?force=" + type));
            try (InputStreamReader reader = new InputStreamReader(response.getEntity().getContent(), StandardCharsets.UTF_8)) {
                ApiResponse resp = mapper.readValue(reader, ApiResponse.class);

                byte[] image = client.execute(new HttpGet(resp.image)).getEntity().getContent().readAllBytes();
                return BufferPipedInputStream.fromBytes(image);
            }
        } catch (IOException e) {
            log.warn(e.getMessage(), e);
            return new ByteArrayInputStream(new byte[0]);
        }
    }
}
