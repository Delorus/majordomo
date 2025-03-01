package page.devnet.telegrambot.convertercurrency;

import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;

public class TestMessage extends Message {
    private String text;
    private Chat chat;

    @Override
    public boolean hasText() {
        return text != null && !text.isEmpty();
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public void setText(String text) {
        this.text = text;
    }

    @Override
    public Chat getChat() {
        return chat;
    }

    @Override
    public void setChat(Chat chat) {
        this.chat = chat;
    }

    @Override
    public boolean isCommand() {
        return hasText() && text.startsWith("/");
    }

    @Override
    public Long getChatId() {
        return chat != null ? chat.getId() : null;
    }
}
