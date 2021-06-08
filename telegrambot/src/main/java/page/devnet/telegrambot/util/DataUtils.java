package page.devnet.telegrambot.util;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

public class DataUtils {

    private final ZonedDateTime messageTime;
    private final int fixHour;

    public DataUtils(ZonedDateTime messageTime, int fixHour) {
        this.messageTime = messageTime;
        this.fixHour = fixHour;
    }

    public ZonedDateTime getMessageFromFixHoursTime() {

        //is point of time we want in hour
        ZonedDateTime fixPointTimeDayMessage = messageTime.with(LocalDateTime.of(
                messageTime.getYear(),
                messageTime.getMonth(),
                messageTime.getDayOfMonth(),
                fixHour,
                0,
                0));

        Duration checkNextDay = Duration.between(fixPointTimeDayMessage, messageTime);

        if (checkNextDay.isNegative()) {
            if (checkIsNewYear()) {
                return messageTime.with(LocalDateTime.of(messageTime.getYear() - 1,
                        messageTime.getMonth().minus(1),
                        messageTime.getMonth().maxLength(),
                        fixHour,
                        0,
                        0));

            }
            //if duration is negative we need minus 1 day;
            return messageTime.with(LocalDateTime.of(messageTime.getYear(),
                    messageTime.getMonth(),
                    messageTime.getDayOfMonth() - 1,
                    fixHour,
                    0,
                    0));
        } else {
            return messageTime.with(LocalDateTime.of(messageTime.getYear(),
                    messageTime.getMonth(),
                    messageTime.getDayOfMonth(),
                    fixHour, 0, 0));

        }
    }

    private boolean checkIsNewYear() {
        return messageTime.getDayOfMonth() == 1;

    }


}
