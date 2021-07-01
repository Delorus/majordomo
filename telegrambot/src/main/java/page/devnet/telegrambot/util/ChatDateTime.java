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

        System.out.println(checkNextDay.isNegative());
        if (checkNextDay.isNegative()) {
            if (checkIsNewYear(fixPointTimeDayMessage)) {
                System.out.println("NY");
                return time.with(LocalDateTime.of(time.getYear() - 1,
                        Month.DECEMBER,
                        time.getMonth().maxLength(),
                        fixHour, 0, 0));

            } else {
                //TODO checktest
                System.out.println("else");
                //if duration is negative we need minus 1 day and 1 minus 1 month;
                return time.with(LocalDateTime.of(time.getYear(),
                        time.getMonth().minus(1),
                        time.getMonth().minus(1).maxLength(),
                        fixHour, 0, 0));
            }
        } else {
            return time.with(LocalDateTime.of(time.getYear(),
                    time.getMonth(),
                    time.getDayOfMonth(),
                    fixHour, 0, 0));

        }
    }

    private boolean checkIsNewYear(ZonedDateTime fixPointTimeMessage) {
        System.out.println(fixPointTimeMessage);
        System.out.println(Duration.ofHours(24).minus(Duration.between(fixPointTimeMessage, time)));
        System.out.println(fixPointTimeMessage.minusHours(24));

        System.out.println("NY2");
        return fixPointTimeMessage.minusHours(24).getYear() < fixPointTimeMessage.getYear();

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
