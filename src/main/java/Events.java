import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a task that occurs over a specific time period.
 */
public class Events extends Task {

    private static final DateTimeFormatter dateFormatter =
            DateTimeFormatter.ofPattern("MMM dd yyyy");

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
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
    }

    LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    LocalDateTime getEndDateTime() {
        return endDateTime;
    }

    boolean occursOn(LocalDate date) {
        LocalDate startDate = startDateTime.toLocalDate();
        LocalDate endDate = endDateTime.toLocalDate();
        return (!date.isBefore(startDate)) && (!date.isAfter(endDate));
    }

    @Override
    public String toString() {
        return "[E]" + super.toString()
                + " (from: " + startDateTime.format(dateFormatter) + " " + TimeFormat.formatAmPm(startDateTime)
                + " to: " + endDateTime.format(dateFormatter) + " " + TimeFormat.formatAmPm(endDateTime) + ")";
    }
}
