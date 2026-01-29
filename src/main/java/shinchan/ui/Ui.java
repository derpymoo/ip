package shinchan.ui;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

import shinchan.task.Task;

/**
 * Handles user interaction: reading commands and showing messages.
 */
public class Ui {
    private static final String LINE = "----------------------------------------";

    private final Scanner scanner;

    /**
     * Creates a Ui object with a scanner reading from standard input.
     */
    public Ui() {
        this.scanner = new Scanner(System.in);
    }

    /**
     * Shows the welcome message.
     */
    public void showWelcome() {
        showLine();
        System.out.println("Hello! I'm Shinchan!");
        System.out.println("What can I do for you?");
        showLine();
    }

    /**
     * Shows the goodbye message.
     */
    public void showBye() {
        showLine();
        System.out.println("Bye. Hope to see you again soon!");
        showLine();
    }

    /**
     * Shows a divider line.
     */
    public void showLine() {
        System.out.println(LINE);
    }

    /**
     * Reads a full command line from the user.
     *
     * @return The command line entered by the user.
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /**
     * Shows an error message in a boxed format.
     *
     * @param message The error message to show.
     */
    public void showError(String message) {
        showLine();
        System.out.println(message);
        showLine();
    }

    /**
     * Shows a message in a boxed format.
     *
     * @param message The message to show.
     */
    public void showMessage(String message) {
        showLine();
        System.out.println(message);
        showLine();
    }

    /**
     * Shows the full task list.
     *
     * @param tasks The tasks to display.
     */
    public void showTaskList(List<Task> tasks) {
        showLine();
        if (tasks.isEmpty()) {
            System.out.println("No tasks in your list.");
        } else {
            for (int i = 0; i < tasks.size(); i++) {
                System.out.println((i + 1) + ". " + tasks.get(i));
            }
        }
        showLine();
    }

    /**
     * Shows a confirmation message after a task is added.
     *
     * @param task The task added.
     * @param size The new size of the task list.
     */
    public void showTaskAdded(Task task, int size) {
        showLine();
        System.out.println("Got it. I've added this task:");
        System.out.println(task);
        System.out.println("Now you have " + size + " tasks in the list.");
        showLine();
    }

    /**
     * Shows a confirmation message after a task is deleted.
     *
     * @param task The task removed.
     * @param size The new size of the task list.
     */
    public void showTaskDeleted(Task task, int size) {
        showLine();
        System.out.println("Noted. I've removed this task:");
        System.out.println(task);
        System.out.println("Now you have " + size + " tasks in the list.");
        showLine();
    }

    /**
     * Shows tasks occurring on a given date (used for the Level 8 stretch command).
     *
     * @param date The date being queried.
     * @param matching The list of tasks that match the date.
     * @param emptyMessage Message to show if no tasks match.
     */
    public void showTasksOnDate(LocalDate date, List<Task> matching, String emptyMessage) {
        showLine();
        System.out.println("Tasks on " + date + ":");
        if (matching.isEmpty()) {
            System.out.println(emptyMessage);
        } else {
            for (int i = 0; i < matching.size(); i++) {
                System.out.println((i + 1) + ". " + matching.get(i));
            }
        }
        showLine();
    }
}
