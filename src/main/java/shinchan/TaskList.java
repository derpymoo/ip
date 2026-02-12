package shinchan;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import shinchan.task.Deadlines;
import shinchan.task.Task;

/**
 * Encapsulates a list of tasks and provides operations on the list.
 */
public class TaskList {
    private final List<Task> tasks;

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Creates a task list initialized with existing tasks.
     *
     * @param initialTasks The tasks to copy into this list.
     */
    public TaskList(List<Task> initialTasks) {
        this.tasks = new ArrayList<>(initialTasks);
    }

    /**
     * Adds a task.
     *
     * @param task The task to add.
     */
    public void add(Task task) {
        tasks.add(task);
    }

    /**
     * Removes and returns the task at the given index.
     *
     * @param index Index of the task to remove (0-based).
     * @return The removed task.
     */
    public Task remove(int index) {
        return tasks.remove(index);
    }

    /**
     * Returns the task at the given index.
     *
     * @param index Index of the task to return (0-based).
     * @return The task.
     */
    public Task get(int index) {
        return tasks.get(index);
    }

    /**
     * Returns the number of tasks.
     *
     * @return The number of tasks.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns an unmodifiable view of the tasks, for safe display.
     *
     * @return An unmodifiable list view.
     */
    public List<Task> asUnmodifiableList() {
        return Collections.unmodifiableList(tasks);
    }

    /**
     * Returns the underlying mutable list, used by Storage for saving.
     *
     * @return The mutable task list.
     */
    public List<Task> asMutableList() {
        return tasks;
    }

    /**
     * Returns tasks whose descriptions contain the given keyword.
     *
     * @param keyword Keyword to search for.
     * @return List of matching tasks.
     */
    public List<Task> find(String keyword) {
        String needle = keyword.trim().toLowerCase();
        List<Task> matches = new ArrayList<>();

        for (Task task : tasks) {
            if (task.getDescription().toLowerCase().contains(needle)) {
                matches.add(task);
            }
        }

        return matches;
    }

    /**
     * Returns deadline tasks due within the next given number of days (inclusive).
     *
     * @param days Number of days from now to look ahead.
     * @return List of upcoming deadline tasks.
     */
    public List<Task> getUpcomingDeadlines(int days) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime future = now.plusDays(days);

        List<Task> upcoming = new ArrayList<>();

        for (Task task : tasks) {
            if (task instanceof Deadlines) {
                Deadlines deadlineTask = (Deadlines) task;
                LocalDateTime deadline = deadlineTask.getBy();

                if (!deadline.isBefore(now) && !deadline.isAfter(future)) {
                    upcoming.add(task);
                }
            }
        }

        return upcoming;
    }
}
