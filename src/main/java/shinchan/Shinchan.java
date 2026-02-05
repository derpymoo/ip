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

    private boolean isExit = false;

    private static final String dataFilePath = "./data/shinchan.txt";

    private static final String messageEmptyInput =
            "Input cannot be empty. Please enter a valid command.";
    private static final String messageUnknownCommand =
            "I'm sorry, but I don't know what that means.";
    private static final String messageInvalidTaskNumber =
            "Invalid task number.";
    private static final String messageTodoEmpty =
            "The description of a todo cannot be empty.";
    private static final String messageDeadlineEmpty =
            "The description of a deadline cannot be empty.";
    private static final String messageDeadlineMissingBy =
            "The deadline command must include '/by' followed by the due date/time.";
    private static final String messageEventEmpty =
            "The description of an event cannot be empty.";
    private static final String messageEventMissingTime =
            "The event command must include '/from' and '/to' followed by the respective date/time.";
    private static final String messageDateTimeBad =
            "Date/time must be in yyyy-MM-dd HHmm format.";
    private static final String messageDeleteInvalid =
            "Invalid task number for deletion.";
    private static final String messageOnMissingDate =
            "The on command must include a date in yyyy-MM-dd format.";
    private static final String messageNoTasksOnDate =
            "No deadlines/events on that date.";

    private final TaskList tasks;
    private final Storage storage;
    private final Ui ui;

    /**
     * Creates a Shinchan chatbot instance and loads tasks from disk.
     */
    public Shinchan() {
        storage = new Storage(dataFilePath);
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
        // reuse existing Ui output format by capturing it
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

    public boolean isExit() {
        return isExit;
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
     * @return {@code true} if the chatbot should terminate
     * @throws ShinchanException If the command is invalid
     */
    private boolean handleInput(String input) throws ShinchanException {
        if (input.isEmpty()) {
            throw new ShinchanException(messageEmptyInput);
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
            throw new ShinchanException(messageUnknownCommand);
        }

        return false;
    }

    /**
     * Handles the creation of a todo task.
     *
     * @param input User input
     * @throws ShinchanException If the description is missing
     */
    private void handleTodo(String input) throws ShinchanException {
        String description = Parser.getRemainder(input);
        if (description.isEmpty()) {
            throw new ShinchanException(messageTodoEmpty);
        }

        Task task = new Todos(description);
        tasks.add(task);
        storage.save(tasks.asMutableList());
        ui.showTaskAdded(task, tasks.size());
    }

    /**
     * Handles the creation of a deadline task.
     *
     * @param input User input
     * @throws ShinchanException If required fields are missing or invalid
     */
    private void handleDeadline(String input) throws ShinchanException {
        if (!input.contains(" /by ")) {
            throw new ShinchanException(messageDeadlineMissingBy);
        }

        String remainder = Parser.getRemainder(input);
        String[] parts = remainder.split(" /by ", 2);

        String description = parts[0].trim();
        String by = parts[1].trim();

        if (description.isEmpty()) {
            throw new ShinchanException(messageDeadlineEmpty);
        }

        LocalDateTime dueDateTime = Parser.parseDateTime(by, messageDateTimeBad);
        Task task = new Deadlines(description, dueDateTime);
        tasks.add(task);
        storage.save(tasks.asMutableList());
        ui.showTaskAdded(task, tasks.size());
    }

    /**
     * Handles the creation of an event task.
     *
     * @param input User input
     * @throws ShinchanException If required fields are missing or invalid
     */
    private void handleEvent(String input) throws ShinchanException {
        String remainder = Parser.getRemainder(input);
        if (!remainder.contains("/from") || !remainder.contains("/to")) {
            throw new ShinchanException(messageEventMissingTime);
        }

        String[] fromParts = remainder.split("/from", 2);

        String description = fromParts[0].trim();
        if (description.isEmpty()) {
            throw new ShinchanException(messageEventEmpty);
        }

        String timing = fromParts[1].trim();
        String[] toParts = timing.split("/to", 2);

        LocalDateTime start = Parser.parseDateTime(toParts[0].trim(), messageDateTimeBad);
        LocalDateTime end = Parser.parseDateTime(toParts[1].trim(), messageDateTimeBad);

        Task task = new Events(description, start, end);
        tasks.add(task);
        storage.save(tasks.asMutableList());
        ui.showTaskAdded(task, tasks.size());
    }

    /**
     * Displays deadlines and events occurring on a specific date.
     *
     * @param input User input
     * @throws ShinchanException If the date is missing or invalid
     */
    private void handleOn(String input) throws ShinchanException {
        String dateText = Parser.getRemainder(input);
        if (dateText.isEmpty()) {
            throw new ShinchanException(messageOnMissingDate);
        }

        LocalDate date = Parser.parseDate(dateText, messageOnMissingDate);

        List<Task> matching = new ArrayList<>();
        for (Task task : tasks.asUnmodifiableList()) {
            if (task instanceof Deadlines && ((Deadlines) task).getDueDate().equals(date)) {
                matching.add(task);
            }
            if (task instanceof Events && ((Events) task).occursOn(date)) {
                matching.add(task);
            }
        }

        ui.showTasksOnDate(date, matching, messageNoTasksOnDate);
    }

    /**
     * Marks a task as completed.
     *
     * @param input User input
     * @throws ShinchanException If the task index is invalid
     */
    private void handleMark(String input) throws ShinchanException {
        int index = Parser.parseTaskIndex(input, messageInvalidTaskNumber);
        if (!isValidIndex(index)) {
            throw new ShinchanException(messageInvalidTaskNumber);
        }

        Task task = tasks.get(index);
        task.markAsDone();
        storage.save(tasks.asMutableList());
        ui.showMessage(task.toString());
    }

    /**
     * Marks a task as not completed.
     *
     * @param input User input
     * @throws ShinchanException If the task index is invalid
     */
    private void handleUnmark(String input) throws ShinchanException {
        int index = Parser.parseTaskIndex(input, messageInvalidTaskNumber);
        if (!isValidIndex(index)) {
            throw new ShinchanException(messageInvalidTaskNumber);
        }

        Task task = tasks.get(index);
        task.markAsUndone();
        storage.save(tasks.asMutableList());
        ui.showMessage(task.toString());
    }

    /**
     * Deletes a task from the task list.
     *
     * @param input User input
     * @throws ShinchanException If the task index is invalid
     */
    private void handleDelete(String input) throws ShinchanException {
        int index = Parser.parseTaskIndex(input, messageDeleteInvalid);
        if (!isValidIndex(index)) {
            throw new ShinchanException(messageDeleteInvalid);
        }

        Task removed = tasks.remove(index);
        storage.save(tasks.asMutableList());

        ui.showTaskDeleted(removed, tasks.size());
    }

    /**
     * Checks whether an index is within the valid task list range.
     *
     * @param index Task index
     * @return {@code true} if the index is valid
     */
    private boolean isValidIndex(int index) {
        return index >= 0 && index < tasks.size();
    }

    /**
     * Finds and displays tasks that match the keyword.
     *
     * @param input User input.
     * @throws ShinchanException If the keyword is missing.
     */
    private void handleFind(String input) throws ShinchanException {
        String keyword = Parser.getRemainder(input); // or your method to get the rest of the line
        if (keyword.trim().isEmpty()) {
            throw new ShinchanException("The find command must include a keyword.");
        }

        ui.showFindResults(tasks.find(keyword));
    }
}
