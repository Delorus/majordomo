package page.devnet.database.entity;

import lombok.Data;

@Data
public class User {

    private String firstName;

    private String lastName;

    private String userName;

    public String getFormattedUsername() {
        var result = userName;
        if (firstName != null && !firstName.isEmpty()) {
            result = firstName;

            if (lastName != null && !lastName.isEmpty()) {
                result = result + " " + lastName;
            }
        }

        return result;
    }
}
