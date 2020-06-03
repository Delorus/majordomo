package page.devnet.database.entity;

import lombok.Data;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Data
public class Message {

    private String idUser;

    private List<String> message;

    private Instant instant;

    public Message(String userId, Instant date, List<String> words) {
    }
}
