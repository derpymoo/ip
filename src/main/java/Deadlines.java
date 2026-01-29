import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Represents a task that has a deadline.
 */
public class Deadlines extends Task {

    private static final DateTimeFormatter displayFormatter =
            DateTimeFormatter.ofPattern("MMM dd yyyy");

    private LocalDate dueDate;

    /**
     * Creates a deadline task with the given description and due date.
     *
     * @param description Description of the task
     * @param dueDate Due date of the task
     */
    public Deadlines(String description, LocalDate dueDate) {
        super(description);
        this.dueDate = dueDate;
    }

    LocalDate getDueDate() {
        return dueDate;
    }

    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + dueDate.format(displayFormatter) + ")";
    }
}
