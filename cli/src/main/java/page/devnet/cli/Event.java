package page.devnet.cli;

import java.util.Objects;

/**
 * @author maksim
 * @since 17.11.2019
 */
public class Event {

    private final String line;

    public Event(String line) {
        Objects.requireNonNull(line);

        this.line = line;
    }

    public boolean isEmpty() {
        return line.isEmpty();
    }


    public boolean isCommand() {
        return line.startsWith(":");
    }

    public String getText() {
        return line;
    }
}
