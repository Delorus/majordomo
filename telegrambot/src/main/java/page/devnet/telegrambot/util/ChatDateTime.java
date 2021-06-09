package page.devnet.telegrambot.util;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

public class ChatDateTime {

    private final ZonedDateTime time;

    public ChatDateTime(ZonedDateTime time) {
        this.time = time;
    }

    public ZonedDateTime fromFixHoursTime(int fixHour) {
        //is point of time we want in hour
        ZonedDateTime fixPointTimeDayMessage = time.with(LocalDateTime.of(
                time.getYear(),
                time.getMonth(),
                time.getDayOfMonth(),
                fixHour,
                0,
                0));

        Duration checkNextDay = Duration.between(fixPointTimeDayMessage, time);

        if (checkNextDay.isNegative()) {
            if (checkIsNewYear()) {
                return time.with(LocalDateTime.of(time.getYear() - 1,
                        time.getMonth().minus(1),
                        time.getMonth().maxLength(),
                        fixHour,
                        0,
                        0));

            }
            //if duration is negative we need minus 1 day;
            return time.with(LocalDateTime.of(time.getYear(),
                    time.getMonth(),
                    time.getDayOfMonth() - 1,
                    fixHour,
                    0,
                    0));
        } else {
            return time.with(LocalDateTime.of(time.getYear(),
                    time.getMonth(),
                    time.getDayOfMonth(),
                    fixHour, 0, 0));

        }
    }

    private boolean checkIsNewYear() {
        return time.getDayOfMonth() == 1;

    }

    public ZonedDateTime minusYears(long years) {
        return time.minusYears(years);
    }

    public ZonedDateTime minusMonths(long months) {
        return time.minusMonths(months);
    }

    public ZonedDateTime minusWeeks(long weeks) {
        return time.minusWeeks(weeks);
    }

    public ZonedDateTime minusDays(long days) {
        return time.minusDays(days);
    }

    public ZonedDateTime minusHours(long hours) {
        return time.minusHours(hours);
    }

    public ZonedDateTime minusMinutes(long minutes) {
        return time.minusMinutes(minutes);
    }

    public ZonedDateTime minusSeconds(long seconds) {
        return time.minusSeconds(seconds);
    }

    public ZonedDateTime minusNanos(long nanos) {
        return time.minusNanos(nanos);
    }
}
