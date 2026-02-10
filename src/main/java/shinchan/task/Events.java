package shinchan.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a task that occurs over a specific time period.
 */
public class Events extends Task {

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("MMM dd yyyy");

    private static final String PREFIX = "[E]";
    private static final String FROM_OPEN = " (from: ";
    private static final String TO_MIDDLE = " to: ";
    private static final String CLOSE = ")";
    private final LocalDateTime startDateTime;
    private final LocalDateTime endDateTime;

    /**
     * Creates an event task with the given description and time period.
     *
     * @param description Description of the task
     * @param startDateTime Start date and time of the event
     * @param endDateTime End date and time of the event
     */
    public Events(String description, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        super(description);
        assert startDateTime != null : "Event start time should not be null";
        assert endDateTime != null : "Event end time should not be null";
        assert !endDateTime.isBefore(startDateTime) : "Event end time must not be before start time";
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
    }

    /**
     * Returns the start date and time of the event.
     *
     * @return Start date and time.
     */
    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    /**
     * Returns the end date and time of the event.
     *
     * @return End date and time.
     */
    public LocalDateTime getEndDateTime() {
        return endDateTime;
    }

    /**
     * Checks whether this event occurs on the specified date.
     *
     * <p>An event is considered to occur on a date if the date is
     * on or after the event start date and on or before the event end date.</p>
     *
     * @param date The date to check.
     * @return {@code true} if the event occurs on the given date; {@code false} otherwise.
     */
    public boolean occursOn(LocalDate date) {
        LocalDate startDate = startDateTime.toLocalDate();
        LocalDate endDate = endDateTime.toLocalDate();
        return (!date.isBefore(startDate)) && (!date.isAfter(endDate));
    }

    /**
     * Returns the string representation of an event task.
     *
     * @return Formatted event string.
     */
    @Override
    public String toString() {
        String startDate = startDateTime.format(DATE_FORMATTER);
        String startTime = TimeFormat.formatAmPm(startDateTime);
        String endDate = endDateTime.format(DATE_FORMATTER);
        String endTime = TimeFormat.formatAmPm(endDateTime);

        return PREFIX + super.toString()
                + FROM_OPEN + startDate + " " + startTime
                + TO_MIDDLE + endDate + " " + endTime
                + CLOSE;
    }
}
