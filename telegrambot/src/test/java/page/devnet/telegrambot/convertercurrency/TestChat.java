package page.devnet.telegrambot.convertercurrency;

import org.telegram.telegrambots.meta.api.objects.Chat;

public class TestChat extends Chat {
    private Long id;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }
}