package shinchan;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import shinchan.task.Task;
import shinchan.task.Todos;

/**
 * Tests for {@link TaskList}.
 */
public class TaskListTest {

    @Test
    public void addTask_sizeIncreases() {
        TaskList list = new TaskList();
        Task task = new Todos("read book");

        list.add(task);

        assertEquals(1, list.size());
        assertEquals(task, list.get(0));
    }

    @Test
    public void removeTask_returnsCorrectTaskAndShrinksList() {
        TaskList list = new TaskList();
        Task task1 = new Todos("a");
        Task task2 = new Todos("b");

        list.add(task1);
        list.add(task2);

        Task removed = list.remove(0);

        assertEquals(task1, removed);
        assertEquals(1, list.size());
        assertEquals(task2, list.get(0));
    }
}
