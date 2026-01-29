package shinchan.task;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Tests for Task string representation.
 */
public class TaskToStringTest {

    @Test
    public void toString_afterMarking_showsDoneMarker() {
        Task task = new Todos("read book");
        task.markAsDone();

        assertTrue(task.toString().contains("[X]"));
    }
}
