package telegramapi;

/**
 * @author maksim
 * @since 10.11.2019
 */
public class User {

    public static User from(org.telegram.telegrambots.meta.api.objects.User origin) {
        return new User(origin);
    }

    private final org.telegram.telegrambots.meta.api.objects.User origin;

    public User(org.telegram.telegrambots.meta.api.objects.User origin) {
        this.origin = origin;
    }

    //region Delegating

    public String getUserName() {
        return origin.getUserName();
    }

    public String getFirstName() {
        return origin.getFirstName();
    }

    public String getLastName() {
        return origin.getLastName();
    }

    //endregion
}
