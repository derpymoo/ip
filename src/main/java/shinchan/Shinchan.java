package shinchan;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import shinchan.exception.ShinchanException;
import shinchan.parser.Parser;
import shinchan.storage.Storage;
import shinchan.task.Deadlines;
import shinchan.task.Events;
import shinchan.task.Task;
import shinchan.task.Todos;
import shinchan.ui.Ui;

/**
 * Runs the Shinchan chatbot that manages a list of tasks.
 * Handles user input, command execution, and task persistence.
 */
public class Shinchan {

    private static final String DATA_FILE_PATH = "./data/shinchan.txt";

    private static final String MESSAGE_DATE_TIME_BAD =
            "Date/time must be in yyyy-MM-dd HHmm format.";
    private static final String MESSAGE_DEADLINE_EMPTY =
            "The description of a deadline cannot be empty.";
    private static final String MESSAGE_DEADLINE_MISSING_BY =
            "The deadline command must include '/by' followed by the due date/time.";
    private static final String MESSAGE_DELETE_INVALID =
            "Invalid task number for deletion.";
    private static final String MESSAGE_EMPTY_INPUT =
            "Input cannot be empty. Please enter a valid command.";
    private static final String MESSAGE_EVENT_EMPTY =
            "The description of an event cannot be empty.";
    private static final String MESSAGE_EVENT_MISSING_TIME =
            "The event command must include '/from' and '/to' followed by the respective date/time.";
    private static final String MESSAGE_INVALID_TASK_NUMBER =
            "Invalid task number.";
    private static final String MESSAGE_NO_TASKS_ON_DATE =
            "No deadlines/events on that date.";
    private static final String MESSAGE_NO_UPCOMING_DEADLINES =
            "No upcoming deadlines in the next %d days.";
    private static final String MESSAGE_ON_MISSING_DATE =
            "The on command must include a date in yyyy-MM-dd format.";
    private static final String MESSAGE_REMIND_BAD_DAYS =
            "Remind days must be a positive integer (e.g., remind 3).";
    private static final String MESSAGE_TODO_EMPTY =
            "The description of a todo cannot be empty.";
    private static final String MESSAGE_UNKNOWN_COMMAND =
            "I'm sorry, but I don't know what that means.";

    private static final int DEFAULT_REMIND_DAYS = 3;

    private boolean isExit = false;

    private final TaskList tasks;
    private final Storage storage;
    private final Ui ui;

    /**
     * Creates a Shinchan chatbot instance and loads tasks from disk.
     */
    public Shinchan() {
        storage = new Storage(DATA_FILE_PATH);
        ui = new Ui();

        TaskList loadedTasks;
        try {
            loadedTasks = new TaskList(storage.load());
        } catch (ShinchanException e) {
            ui.showError(e.getMessage());
            loadedTasks = new TaskList();
        }

        tasks = loadedTasks;
    }

    /**
     * Starts the chatbot application.
     *
     * @param args Command-line arguments (not used).
     */
    public static void main(String[] args) {
        Shinchan shinchan = new Shinchan();
        shinchan.run();
    }

    public String getWelcomeMessage() {
        return capturePrintedOutput(() -> ui.showWelcome());
    }

    public String getResponse(String input) {
        if (input == null) {
            input = "";
        }

        String trimmed = input.trim();

        return capturePrintedOutput(() -> {
            try {
                boolean shouldExit = handleInput(trimmed);
                isExit = shouldExit;
            } catch (ShinchanException e) {
                ui.showError(e.getMessage());
            }
        });
    }

    private String capturePrintedOutput(Runnable action) {
        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
        java.io.PrintStream oldOut = System.out;
        try {
            System.setOut(new java.io.PrintStream(baos));
            action.run();
        } finally {
            System.out.flush();
            System.setOut(oldOut);
        }
        return baos.toString().trim();
    }

    /**
     * Runs the main input-processing loop of the chatbot.
     */
    private void run() {
        ui.showWelcome();

        while (true) {
            String input = ui.readCommand().trim();

            try {
                boolean shouldExit = handleInput(input);
                if (shouldExit) {
                    return;
                }
            } catch (ShinchanException e) {
                ui.showError(e.getMessage());
            }
        }
    }

    /**
     * Processes a single user input command.
     *
     * @param input Raw user input
     * @return true if the chatbot should terminate
     * @throws ShinchanException If the command is invalid
     */
    private boolean handleInput(String input) throws ShinchanException {
        if (input.isEmpty()) {
            throw new ShinchanException(MESSAGE_EMPTY_INPUT);
        }

        String command = Parser.getCommandWord(input);

        switch (command) {
        case "todo":
            handleTodo(input);
            break;
        case "deadline":
            handleDeadline(input);
            break;
        case "event":
            handleEvent(input);
            break;
        case "find":
            handleFind(input);
            break;
        case "on":
            handleOn(input);
            break;
        case "remind":
            handleRemind(input);
            break;
        case "list":
            ui.showTaskList(tasks.asUnmodifiableList());
            break;
        case "mark":
            handleMark(input);
            break;
        case "unmark":
            handleUnmark(input);
            break;
        case "delete":
            handleDelete(input);
            break;
        case "bye":
            ui.showBye();
            return true;
        default:
            throw new ShinchanException(MESSAGE_UNKNOWN_COMMAND);
        }

        return false;
    }

    /**
     * Displays upcoming deadline tasks within a specified number of days.
     * Command formats:
     * - remind
     * - remind 7
     *
     * @param input User input.
     * @throws ShinchanException If the number of days is invalid.
     */
    private void handleRemind(String input) throws ShinchanException {
        String remainder = Parser.getRemainder(input).trim();

        int days = DEFAULT_REMIND_DAYS;

        if (!remainder.isEmpty()) {
            try {
                days = Integer.parseInt(remainder);
            } catch (NumberFormatException e) {
                throw new ShinchanException(MESSAGE_REMIND_BAD_DAYS);
            }
        }

        if (days <= 0) {
            throw new ShinchanException(MESSAGE_REMIND_BAD_DAYS);
        }

        List<Task> upcoming = tasks.getUpcomingDeadlines(days);

        if (upcoming.isEmpty()) {
            ui.showMessage(String.format(MESSAGE_NO_UPCOMING_DEADLINES, days));
            return;
        }

        ui.showMessage("Here are upcoming deadlines in the next " + days + " days:");
        ui.showTaskList(upcoming);
    }

    // === Remaining original handlers below (unchanged logic) ===

    private void handleTodo(String input) throws ShinchanException {
        String description = Parser.getRemainder(input);
        if (description.isEmpty()) {
            throw new ShinchanException(MESSAGE_TODO_EMPTY);
        }

        Task task = new Todos(description);
        tasks.add(task);
        storage.save(tasks.asMutableList());
        ui.showTaskAdded(task, tasks.size());
    }

    private void handleDeadline(String input) throws ShinchanException {
        if (!input.contains(" /by ")) {
            throw new ShinchanException(MESSAGE_DEADLINE_MISSING_BY);
        }

        String remainder = Parser.getRemainder(input);
        String[] parts = remainder.split(" /by ", 2);

        String description = parts[0].trim();
        String by = parts[1].trim();

        if (description.isEmpty()) {
            throw new ShinchanException(MESSAGE_DEADLINE_EMPTY);
        }

        LocalDateTime dueDateTime =
                Parser.parseDateTime(by, MESSAGE_DATE_TIME_BAD);

        Task task = new Deadlines(description, dueDateTime);
        tasks.add(task);
        storage.save(tasks.asMutableList());
        ui.showTaskAdded(task, tasks.size());
    }

    private void handleEvent(String input) throws ShinchanException {
        String remainder = Parser.getRemainder(input);
        if (!remainder.contains("/from") || !remainder.contains("/to")) {
            throw new ShinchanException(MESSAGE_EVENT_MISSING_TIME);
        }

        String[] fromParts = remainder.split("/from", 2);

        String description = fromParts[0].trim();
        if (description.isEmpty()) {
            throw new ShinchanException(MESSAGE_EVENT_EMPTY);
        }

        String timing = fromParts[1].trim();
        String[] toParts = timing.split("/to", 2);

        LocalDateTime start =
                Parser.parseDateTime(toParts[0].trim(), MESSAGE_DATE_TIME_BAD);
        LocalDateTime end =
                Parser.parseDateTime(toParts[1].trim(), MESSAGE_DATE_TIME_BAD);

        Task task = new Events(description, start, end);
        tasks.add(task);
        storage.save(tasks.asMutableList());
        ui.showTaskAdded(task, tasks.size());
    }

    private void handleOn(String input) throws ShinchanException {
        String dateText = Parser.getRemainder(input);
        if (dateText.isEmpty()) {
            throw new ShinchanException(MESSAGE_ON_MISSING_DATE);
        }

        LocalDate date =
                Parser.parseDate(dateText, MESSAGE_ON_MISSING_DATE);

        List<Task> matching = new ArrayList<>();

        for (Task task : tasks.asUnmodifiableList()) {
            if (task instanceof Deadlines
                    && ((Deadlines) task).getDueDate().equals(date)) {
                matching.add(task);
            }
            if (task instanceof Events
                    && ((Events) task).occursOn(date)) {
                matching.add(task);
            }
        }

        ui.showTasksOnDate(date, matching, MESSAGE_NO_TASKS_ON_DATE);
    }

    private void handleMark(String input) throws ShinchanException {
        int index =
                Parser.parseTaskIndex(input, MESSAGE_INVALID_TASK_NUMBER);

        if (!isValidIndex(index)) {
            throw new ShinchanException(MESSAGE_INVALID_TASK_NUMBER);
        }

        Task task = tasks.get(index);
        task.markAsDone();
        storage.save(tasks.asMutableList());
        ui.showMessage(task.toString());
    }

    private void handleUnmark(String input) throws ShinchanException {
        int index =
                Parser.parseTaskIndex(input, MESSAGE_INVALID_TASK_NUMBER);

        if (!isValidIndex(index)) {
            throw new ShinchanException(MESSAGE_INVALID_TASK_NUMBER);
        }

        Task task = tasks.get(index);
        task.markAsUndone();
        storage.save(tasks.asMutableList());
        ui.showMessage(task.toString());
    }

    private void handleDelete(String input) throws ShinchanException {
        int index =
                Parser.parseTaskIndex(input, MESSAGE_DELETE_INVALID);

        if (!isValidIndex(index)) {
            throw new ShinchanException(MESSAGE_DELETE_INVALID);
        }

        Task removed = tasks.remove(index);
        storage.save(tasks.asMutableList());
        ui.showTaskDeleted(removed, tasks.size());
    }

    private boolean isValidIndex(int index) {
        return index >= 0 && index < tasks.size();
    }

    private void handleFind(String input) throws ShinchanException {
        String keyword = Parser.getRemainder(input);

        if (keyword.trim().isEmpty()) {
            throw new ShinchanException(
                    "The find command must include a keyword.");
        }

        ui.showFindResults(tasks.find(keyword));
    }
}
