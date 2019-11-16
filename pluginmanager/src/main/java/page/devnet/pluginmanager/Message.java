package page.devnet.pluginmanager;

/**
 * @author maksim
 * @since 10.11.2019
 */
public class Message {

    public static Message from(org.telegram.telegrambots.meta.api.objects.Message origin) {
        return new Message(origin);
    }

    private final org.telegram.telegrambots.meta.api.objects.Message origin;

    private Message(org.telegram.telegrambots.meta.api.objects.Message origin) {
        this.origin = origin;
    }

    //region Delegating

    public Long getChatId() {
        return origin.getChatId();
    }

    public boolean hasText() {
        return origin.hasText();
    }

    public boolean isCommand() {
        return origin.isCommand();
    }

    public String getText() {
        return origin.getText();
    }

    public User getFrom() {
        return User.from(origin.getFrom());
    }

    public Integer getMessageId() {
        return origin.getMessageId();
    }

    //endregion
}
