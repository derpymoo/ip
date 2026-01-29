/**
 * Represents a task that has a deadline.
 */
public class Deadlines extends Task {

    private String due;

    /**
     * Creates a deadline task with the given description and due date.
     *
     * @param description Description of the task
     * @param due Due date of the task
     */
    public Deadlines(String description, String due) {
        super(description);
        this.due = due;
    }

    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + due + ")";
    }
}
