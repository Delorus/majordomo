package page.devnet.telegrambot.util;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZonedDateTime;


public class ChatDateTime {

    private final ZonedDateTime time;

    public ChatDateTime(ZonedDateTime time) {
        this.time = time;
    }

    /**
     * @param fixHour - from what time in 24 hours format we want to see message.
     * @return
     */
    public ZonedDateTime fromFixHoursTime(int fixHour) {

        ZonedDateTime fixPointTimeDayMessage = time.with(LocalDateTime.of(
                time.getYear(),
                time.getMonth(),
                time.getDayOfMonth(),
                fixHour, 0, 0));

        Duration checkNextDay = Duration.between(fixPointTimeDayMessage, time);

        if (checkNextDay.isNegative()) {
            if (checkIsNewYear(fixPointTimeDayMessage)) {
                return time.with(LocalDateTime.of(time.getYear() - 1,
                        Month.DECEMBER,
                        time.getMonth().maxLength(),
                        fixHour, 0, 0));

            } else {
                if (checkNextMonth(fixPointTimeDayMessage)) {
                    if (time.toLocalDate().isLeapYear()) {
                        return time.with(LocalDateTime.of(time.getYear(),
                                time.getMonth().minus(1),
                                time.getMonth().minus(1).maxLength(),
                                fixHour, 0, 0));
                    } else {
                        return time.with(LocalDateTime.of(time.getYear(),
                                time.getMonth().minus(1),
                                time.getMonth().minus(1).length(false),
                                fixHour, 0, 0));
                    }
                } else {
                    return time.with(LocalDateTime.of(time.getYear(),
                            time.getMonth(),
                            time.getDayOfMonth() - 1,
                            fixHour, 0, 0));
                }
            }
        } else {
            return time.with(LocalDateTime.of(time.getYear(),
                    time.getMonth(),
                    time.getDayOfMonth(),
                    fixHour, 0, 0));

        }
    }

    private boolean checkIsNewYear(ZonedDateTime fixPointTimeMessage) {
        return fixPointTimeMessage.minusHours(24).getYear() < fixPointTimeMessage.getYear();

    }

    private boolean checkNextMonth(ZonedDateTime fixPointTimeMessage) {
        return fixPointTimeMessage.minusHours(24).getMonth().getValue() < fixPointTimeMessage.getMonth().getValue();
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
