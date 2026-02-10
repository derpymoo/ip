package shinchan.task;

/**
 * Represents a todo task without a specific date or time.
 */
public class Todos extends Task {

    /**
     * Creates a todo task with the given description.
     *
     * @param description Description of the task
     */
    public Todos(String description) {
        super(description);
        assert description != null : "Todo description should not be null";
    }

    /**
     * Returns the string representation of the todo.
     *
     * @return Formatted todo string.
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
