/**
 * Represents a task that occurs over a specific time period.
 */
public class Events extends Task {

    private String startTime;
    private String endTime;

    /**
     * Creates an event task with the given description and time period.
     *
     * @param description Description of the task
     * @param startTime Start time of the event
     * @param endTime End time of the event
     */
    public Events(String description, String startTime, String endTime) {
        super(description);
        this.startTime = startTime;
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + startTime + " to: " + endTime + ")";
    }
}
