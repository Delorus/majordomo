package page.devnet.vertxtgbot.tgapi;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendAnimation;
import org.telegram.telegrambots.meta.api.objects.ApiResponse;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiValidationException;

import java.io.IOException;

/**
 * @author mshherbakov
 * @since 18.06.2021
 */
@EqualsAndHashCode(callSuper = false)
@Getter
@Setter
@ToString
@Builder
public class SendExternalAnimation extends BotApiMethod<Message> {

    @JsonProperty("chat_id")
    private final String chatId;

    @JsonProperty("animation")
    private final String animationUrl;

    public SendExternalAnimation(String chatId, String url) {
        this.chatId = chatId;
        this.animationUrl = url;
    }

    @Override
    public String getMethod() {
        return SendAnimation.PATH;
    }

    @Override
    public Message deserializeResponse(String answer) throws TelegramApiRequestException {
        try {
            ApiResponse<Message> result = OBJECT_MAPPER.readValue(answer, new TypeReference<>() { });
            if (result.getOk()) {
                return result.getResult();
            } else {
                throw new TelegramApiRequestException("Error sending animation", result);
            }
        } catch (IOException e) {
            throw new TelegramApiRequestException("Unable to deserialize response", e);
        }
    }

    @Override
    public void validate() throws TelegramApiValidationException {
        if (chatId == null || chatId.isEmpty()) {
            throw new TelegramApiValidationException("ChatId parameter can't be empty", this);
        }

        if (animationUrl == null || animationUrl.isEmpty()) {
            throw new TelegramApiValidationException("Animation parameter can't be empty", this);
        }
    }
}
