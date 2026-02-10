package shinchan.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a task that has a deadline.
 */
public class Deadlines extends Task {

    private static final DateTimeFormatter dateFormatter =
            DateTimeFormatter.ofPattern("MMM dd yyyy");

    private final LocalDateTime dueDateTime;

    /**
     * Creates a deadline task with the given description and due date-time.
     *
     * @param description Description of the task.
     * @param dueDateTime Due date and time of the task.
     */
    public Deadlines(String description, LocalDateTime dueDateTime) {
        super(description);
        assert dueDateTime != null : "Deadline time should not be null";
        this.dueDateTime = dueDateTime;
    }

    /**
     * Returns the due date and time of this deadline task.
     *
     * @return Due date and time.
     */
    public LocalDateTime getDueDateTime() {
        return dueDateTime;
    }

    /**
     * Returns the due date of this deadline task.
     *
     * @return Due date.
     */
    public LocalDate getDueDate() {
        return dueDateTime.toLocalDate();
    }

    /**
     * Returns the string representation of a deadline task.
     *
     * @return Formatted deadline string.
     */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: "
                + dueDateTime.format(dateFormatter) + " " + TimeFormat.formatAmPm(dueDateTime) + ")";
    }
}
