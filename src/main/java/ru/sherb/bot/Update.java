package ru.sherb.bot;

/**
 * @author maksim
 * @since 10.11.2019
 */
public class Update {

    public static Update from(org.telegram.telegrambots.meta.api.objects.Update origin) {
        return new Update(origin);
    }

    private final org.telegram.telegrambots.meta.api.objects.Update origin;

    private Update(org.telegram.telegrambots.meta.api.objects.Update origin) {
        this.origin = origin;
    }

    //region Delegating

    public boolean hasMessage() {
        return origin.hasMessage();
    }

    public Message getMessage() {
        return Message.from(origin.getMessage());
    }

    //endregion

}
