package ru.sherb.event;

import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EventTest {

    @Test
    void getDelay() {
        Event event = new Event(EventType.task, ZonedDateTime.now().plusHours(1), "test event", 0);
        assertEquals(TimeUnit.HOURS.toSeconds(1) - 1, event.getDelay());
    }
}