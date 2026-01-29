package shinchan.task;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Tests for {@link Task}.
 */
public class TaskTest {

    @Test
    public void markAndUnmark_updatesDoneState() {
        Task task = new Todos("read");

        assertFalse(task.isDone());

        task.markAsDone();
        assertTrue(task.isDone());

        task.markAsUndone();
        assertFalse(task.isDone());
    }
}

