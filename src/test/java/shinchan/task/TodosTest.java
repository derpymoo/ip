package shinchan.task;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Tests for {@link Todos}.
 */
public class TodosTest {

    @Test
    public void toString_containsTodoMarker() {
        Todos todo = new Todos("read");

        assertTrue(todo.toString().contains("[T]"));
    }
}
