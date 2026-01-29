import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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

    private static final int splitLimitTwo = 2;
    private static final int userIndexOffset = 1;

    private static final DateTimeFormatter dateTimeInputFormatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm");

    private final List<Task> tasks;
    private final Storage storage;
    private final Ui ui;

    /**
     * Creates a Shinchan chatbot instance and loads tasks from disk.
     */
    public Shinchan() {
        tasks = new ArrayList<>();
        storage = new Storage(dataFilePath);
        ui = new Ui();

        try {
            tasks.addAll(storage.load());
        } catch (ShinchanException e) {
            ui.showError(e.getMessage());
        }
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

        String command = getCommand(input);

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
            ui.showTaskList(tasks);
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

    private String getCommand(String input) {
        String[] parts = input.split(" ", splitLimitTwo);
        return parts[0].toLowerCase(Locale.ROOT);
    }

    private void handleTodo(String input) throws ShinchanException {
        String description = getRemainder(input);
        if (description.isEmpty()) {
            throw new ShinchanException(messageTodoEmpty);
        }

        Task task = new Todos(description);
        tasks.add(task);
        storage.save(tasks);
        ui.showTaskAdded(task, tasks.size());
    }

    private void handleDeadline(String input) throws ShinchanException {
        if (!input.contains(" /by ")) {
            throw new ShinchanException(messageDeadlineMissingBy);
        }

        String remainder = getRemainder(input);
        String[] parts = remainder.split(" /by ", splitLimitTwo);

        String description = parts[0].trim();
        String by = parts[1].trim();

        if (description.isEmpty()) {
            throw new ShinchanException(messageDeadlineEmpty);
        }

        LocalDateTime dueDateTime = parseDateTime(by);
        Task task = new Deadlines(description, dueDateTime);
        tasks.add(task);
        storage.save(tasks);
        ui.showTaskAdded(task, tasks.size());
    }

    private void handleEvent(String input) throws ShinchanException {
        if (!input.contains("/from") || !input.contains("/to")) {
            throw new ShinchanException(messageEventMissingTime);
        }

        String remainder = getRemainder(input);
        String[] fromParts = remainder.split("/from", splitLimitTwo);

        String description = fromParts[0].trim();
        if (description.isEmpty()) {
            throw new ShinchanException(messageEventEmpty);
        }

        String timing = fromParts[1].trim();
        String[] toParts = timing.split("/to", splitLimitTwo);

        LocalDateTime start = parseDateTime(toParts[0].trim());
        LocalDateTime end = parseDateTime(toParts[1].trim());

        Task task = new Events(description, start, end);
        tasks.add(task);
        storage.save(tasks);
        ui.showTaskAdded(task, tasks.size());
    }

    private void handleOn(String input) throws ShinchanException {
        String dateText = getRemainder(input);
        if (dateText.isEmpty()) {
            throw new ShinchanException(messageOnMissingDate);
        }

        LocalDate date = parseDate(dateText);

        List<Task> matching = new ArrayList<>();
        for (Task task : tasks) {
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
        int index = parseTaskIndex(input);
        if (!isValidIndex(index)) {
            throw new ShinchanException(messageInvalidTaskNumber);
        }

        Task task = tasks.get(index);
        task.markAsDone();
        storage.save(tasks);
        ui.showMessage(task.toString());
    }

    private void handleUnmark(String input) throws ShinchanException {
        int index = parseTaskIndex(input);
        if (!isValidIndex(index)) {
            throw new ShinchanException(messageInvalidTaskNumber);
        }

        Task task = tasks.get(index);
        task.markAsUndone();
        storage.save(tasks);
        ui.showMessage(task.toString());
    }

    private void handleDelete(String input) throws ShinchanException {
        int index = parseTaskIndex(input);
        if (!isValidIndex(index)) {
            throw new ShinchanException(messageDeleteInvalid);
        }

        Task removed = tasks.remove(index);
        storage.save(tasks);

        ui.showTaskDeleted(removed, tasks.size());
    }

    private LocalDate parseDate(String text) throws ShinchanException {
        try {
            return LocalDate.parse(text.trim());
        } catch (DateTimeParseException e) {
            throw new ShinchanException("Date must be in yyyy-MM-dd format.");
        }
    }

    private LocalDateTime parseDateTime(String text) throws ShinchanException {
        try {
            return LocalDateTime.parse(text.trim(), dateTimeInputFormatter);
        } catch (DateTimeParseException e) {
            throw new ShinchanException(messageDateTimeBad);
        }
    }

    private int parseTaskIndex(String input) throws ShinchanException {
        String[] parts = input.split(" ", splitLimitTwo);
        if (parts.length < splitLimitTwo) {
            throw new ShinchanException(messageInvalidTaskNumber);
        }

        try {
            return Integer.parseInt(parts[1].trim()) - userIndexOffset;
        } catch (NumberFormatException e) {
            throw new ShinchanException(messageInvalidTaskNumber);
        }
    }

    private boolean isValidIndex(int index) {
        return index >= 0 && index < tasks.size();
    }

    private String getRemainder(String input) {
        String[] parts = input.split(" ", splitLimitTwo);
        return parts.length < splitLimitTwo ? "" : parts[1].trim();
    }
}
