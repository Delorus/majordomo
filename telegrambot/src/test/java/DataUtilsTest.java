import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import page.devnet.telegrambot.util.DataUtils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class DataUtilsTest {

    ZoneId timeZoneYekaterinburg;
    ZonedDateTime zonedDateTimeTest;

    @BeforeEach
    void initTest() {
        timeZoneYekaterinburg = ZoneId.of("Asia/Yekaterinburg");
        zonedDateTimeTest = ZonedDateTime.now(timeZoneYekaterinburg);

    }

    @Test
    void checkDataUtilsIfTimeIsAfterFixPoint() {
        ZonedDateTime dateTimeTestIsAfterFixPoint = ZonedDateTime.parse("2018-01-02T06:00:00+05:00[Asia/Yekaterinburg]");
        ZonedDateTime dateTimeFinalTestIsAfterFixPoint = ZonedDateTime.of(LocalDate.of(2018, 1, 2),
                LocalTime.of(5, 0, 0), timeZoneYekaterinburg);
        DataUtils dataUtils = new DataUtils(dateTimeTestIsAfterFixPoint, 5);
        Assertions.assertEquals(dateTimeFinalTestIsAfterFixPoint, dataUtils.getMessageFromFixHoursTime());
    }

    @Test
    void checkDataUtilsIfTimeIsBeforeFixPoint() {
        ZonedDateTime dateTimeTestIsBeforeFixPoint = ZonedDateTime.parse("2018-01-02T04:00:00+05:00[Asia/Yekaterinburg]");
        ZonedDateTime dateTimeFinalTestIsBeforeFixPoint = ZonedDateTime.of(LocalDate.of(2018, 1, 1),
                LocalTime.of(5, 0, 0), timeZoneYekaterinburg);
        DataUtils dataUtils = new DataUtils(dateTimeTestIsBeforeFixPoint, 5);
        Assertions.assertEquals(dateTimeFinalTestIsBeforeFixPoint, dataUtils.getMessageFromFixHoursTime());
    }

    @Test
    void checkDataUtilsIfTimeIsNewYear() {
        ZonedDateTime dateTimeTestIsNewYear = ZonedDateTime.parse("2018-01-01T04:00:00+05:00[Asia/Yekaterinburg]");
        ZonedDateTime dateTimeFinalTestIsNewYear = ZonedDateTime.of(LocalDate.of(2017, 12, 31),
                LocalTime.of(5, 0, 0), timeZoneYekaterinburg);
        DataUtils dataUtils = new DataUtils(dateTimeTestIsNewYear, 5);
        Assertions.assertEquals(dateTimeFinalTestIsNewYear, dataUtils.getMessageFromFixHoursTime());
    }

}
