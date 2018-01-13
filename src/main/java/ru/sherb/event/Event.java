package ru.sherb.event;

import java.io.Serializable;
import java.time.Duration;
import java.time.ZonedDateTime;

public class Event implements Serializable {
    private final EventType type;
    private final ZonedDateTime time;
    private final String description;
    private final int repeatEvery;

    Event(final EventType type, final ZonedDateTime time, final String description,
          final int repeatEvery) {
        this.type = type;
        this.time = time;
        this.description = description;
        this.repeatEvery = repeatEvery;
    }

    public EventType getType() {
        return type;
    }

    public ZonedDateTime getTime() {
        return time;
    }

    public String getDescription() {
        return description;
    }

    public boolean isRepeatable() {
        return repeatEvery != 0;
    }

    public int getRepeatEvery() {
        return repeatEvery;
    }

    public long getDelay() {
        Duration between = Duration.between(ZonedDateTime.now(), time);
        return between.getSeconds();
    }
    // TODO: 13.01.2018 write serializable agent
}
