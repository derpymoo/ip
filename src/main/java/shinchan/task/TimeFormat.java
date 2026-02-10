package shinchan.task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Formats times in 12-hour lowercase am/pm format such as 8pm or 8:30pm.
 */
public class TimeFormat {

    private static final DateTimeFormatter hourMinuteAmPm =
            DateTimeFormatter.ofPattern("h:mma");

    private static final DateTimeFormatter hourAmPm =
            DateTimeFormatter.ofPattern("ha");

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
    static String formatAmPm(LocalDateTime dateTime) {
        String formatted;
        if (dateTime.getMinute() == 0) {
            formatted = dateTime.format(hourAmPm);
        } else {
            formatted = dateTime.format(hourMinuteAmPm);
        }
        return formatted.toLowerCase(); // 8pm / 8:30pm
    }
}
