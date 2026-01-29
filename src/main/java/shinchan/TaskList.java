package shinchan;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
        String trimmed = keyword.trim();
        List<Task> matches = new ArrayList<>();

        String needle = trimmed.toLowerCase();
        for (Task task : tasks) { // replace `tasks` with your internal list name
            if (task.getDescription().toLowerCase().contains(needle)) {
                matches.add(task);
            }
        }
        return matches;
    }
}
