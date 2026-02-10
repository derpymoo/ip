package shinchan.task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Formats times in 12-hour lowercase am/pm format such as 8pm or 8:30pm.
 */
public final class TimeFormat {

    private static final DateTimeFormatter HOUR_AM_PM =
            DateTimeFormatter.ofPattern("ha");
    private static final DateTimeFormatter HOUR_MINUTE_AM_PM =
            DateTimeFormatter.ofPattern("h:mma");

    /**
     * Prevents instantiation of this utility class.
     */
    private TimeFormat() {
        // Utility class
    }

    /**
     * Formats the given date-time into a 12-hour lowercase am/pm string.
     * Omits minutes when the minute component is zero (e.g., {@code 8pm}).
     *
     * @param dateTime Date-time to format.
     * @return Formatted time string.
     */
    public static String formatAmPm(LocalDateTime dateTime) {
        assert dateTime != null : "Date-time to format should not be null";

        DateTimeFormatter formatter =
                dateTime.getMinute() == 0 ? HOUR_AM_PM : HOUR_MINUTE_AM_PM;

        return dateTime.format(formatter).toLowerCase(Locale.ROOT);
    }
}
