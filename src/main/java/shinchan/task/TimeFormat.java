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

    private TimeFormat() {
        // Utility class
    }

    static String formatAmPm(LocalDateTime dateTime) {
        String formatted;
        if (dateTime.getMinute() == 0) {
            formatted = dateTime.format(hourAmPm);     // e.g. 8PM
        } else {
            formatted = dateTime.format(hourMinuteAmPm); // e.g. 8:30PM
        }
        return formatted.toLowerCase(); // 8pm / 8:30pm
    }
}
