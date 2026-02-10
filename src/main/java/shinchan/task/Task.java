package shinchan.task;

/**
 * Represents a task with a description and completion status.
 */
public class Task {

    private String description;
    private boolean isDone;

    /**
     * Creates a task with the given description.
     *
     * @param description Description of the task
     */
    public Task(String description) {
        assert description != null : "Task description should not be null";
        this.description = description;
        this.isDone = false;
    }

    /**
     * Returns the status icon of the task.
     *
     * @return "X" if the task is done, otherwise a blank space
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /**
     * Marks the task as done.
     */
    public void markAsDone() {
        assert !isDone : "Task should not already be marked done";
        isDone = true;
    }

    /**
     * Marks the task as not done.
     */
    public void markAsUndone() {
        assert isDone : "Task should be done before marking undone";
        isDone = false;
    }

    /**
     * Returns the description of the task.
     *
     * @return Task description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns whether the task is marked as done.
     *
     * @return {@code true} if done, otherwise {@code false}.
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Returns the string representation of the task.
     *
     * @return Formatted task string.
     */
    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }
}
