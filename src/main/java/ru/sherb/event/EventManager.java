package ru.sherb.event;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class EventManager {

    private final ScheduledExecutorService scheduler = new ScheduledThreadPoolExecutor(10); // TODO: 13.01.2018 вынести в настройки
    private final Notifier notifier;

    public EventManager(final Notifier notifier) {
        this.notifier = notifier;
    }

    public void startEvent(String name, Event event) {
        scheduler.schedule(() -> notifier.sendMessage(event.getDescription()), event.getDelay(), TimeUnit.SECONDS);
    }
}
