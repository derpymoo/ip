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
 */
public class Shinchan {

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
     * Starts the chatbot.
     *
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        Shinchan shinchan = new Shinchan();
        shinchan.run();
    }

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

    private void handleDelete(String input) throws ShinchanException {
        int index = Parser.parseTaskIndex(input, messageDeleteInvalid);
        if (!isValidIndex(index)) {
            throw new ShinchanException(messageDeleteInvalid);
        }

        Task removed = tasks.remove(index);
        storage.save(tasks.asMutableList());

        ui.showTaskDeleted(removed, tasks.size());
    }

    private boolean isValidIndex(int index) {
        return index >= 0 && index < tasks.size();
    }
}
