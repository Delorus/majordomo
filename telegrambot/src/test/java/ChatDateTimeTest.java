import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import page.devnet.telegrambot.util.ChatDateTime;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class ChatDateTimeTest {

    ZoneId timeZoneYekaterinburg;
    ZonedDateTime zonedDateTimeTest;

    @BeforeEach
    void initTest() {
        timeZoneYekaterinburg = ZoneId.of("Asia/Yekaterinburg");
        zonedDateTimeTest = ZonedDateTime.now(timeZoneYekaterinburg);

    }

    @Test
    void checkDataUtilsIfTimeIsNewYear() {
        ZonedDateTime dateTimeTestIsNewYear = ZonedDateTime.parse("2018-01-01T02:00:00+05:00[Asia/Yekaterinburg]");
        ZonedDateTime dateTimeFinalTestIsNewYear = ZonedDateTime.of(LocalDate.of(2017, 12, 31),
                LocalTime.of(3, 0, 0), timeZoneYekaterinburg);
        ChatDateTime chatDateTime = new ChatDateTime(dateTimeTestIsNewYear);
        Assertions.assertEquals(dateTimeFinalTestIsNewYear, chatDateTime.fromFixHoursTime(3));
    }

    @Test
    void checkDataUtilsIfHourIsAfterFixPoint() {
        ZonedDateTime dateTimeTestIsAfterFixPoint = ZonedDateTime.parse("2018-01-02T06:00:00+05:00[Asia/Yekaterinburg]");
        ZonedDateTime dateTimeFinalTestIsAfterFixPoint = ZonedDateTime.of(LocalDate.of(2018, 1, 2),
                LocalTime.of(5, 0, 0), timeZoneYekaterinburg);
        ChatDateTime chatDateTime = new ChatDateTime(dateTimeTestIsAfterFixPoint);
        Assertions.assertEquals(dateTimeFinalTestIsAfterFixPoint, chatDateTime.fromFixHoursTime(5));
    }

    @Test
    void checkDataUtilsIfHourIsBeforeFixPoint() {
        ZonedDateTime dateTimeTestIsBeforeFixPoint = ZonedDateTime.parse("2018-01-06T04:00:00+05:00[Asia/Yekaterinburg]");
        ZonedDateTime dateTimeFinalTestIsBeforeFixPoint = ZonedDateTime.of(LocalDate.of(2018, 1, 5),
                LocalTime.of(5, 0, 0), timeZoneYekaterinburg);
        ChatDateTime chatDateTime = new ChatDateTime(dateTimeTestIsBeforeFixPoint);
        Assertions.assertEquals(dateTimeFinalTestIsBeforeFixPoint, chatDateTime.fromFixHoursTime(5));
    }

    @Test
    void checkDataUtilsIfTimeIsNextMonth() {
        ZonedDateTime dateTimeTestIsNewMonth = ZonedDateTime.parse("2021-05-01T02:00:00+05:00[Asia/Yekaterinburg]");
        ZonedDateTime dateTimeFinalTestIsNewMonth = ZonedDateTime.of(LocalDate.of(2021, 4, 30),
                LocalTime.of(3, 0, 0), timeZoneYekaterinburg);
        ChatDateTime chatDateTime = new ChatDateTime(dateTimeTestIsNewMonth);
        System.out.println(dateTimeTestIsNewMonth);
        System.out.println(dateTimeFinalTestIsNewMonth);
        Assertions.assertEquals(dateTimeFinalTestIsNewMonth, chatDateTime.fromFixHoursTime(3));

    }

}
