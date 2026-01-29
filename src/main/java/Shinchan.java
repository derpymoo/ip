import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

/**
 * Runs the Shinchan chatbot that manages a list of tasks.
 */
public class Shinchan {

    private static final String lineSeparator = "----------------------------------------";
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

    /**
     * Creates a Shinchan chatbot instance and loads tasks from disk.
     */
    public Shinchan() {
        tasks = new ArrayList<>();
        storage = new Storage(dataFilePath);

        try {
            tasks.addAll(storage.load());
        } catch (ShinchanException e) {
            printBoxedMessage(e.getMessage());
        }
    }

    public static void main(String[] args) {
        Shinchan shinchan = new Shinchan();
        shinchan.run();
    }

    private void run() {
        printGreeting();

        try (Scanner scanner = new Scanner(System.in)) {
            while (scanner.hasNextLine()) {
                String input = scanner.nextLine().trim();

                try {
                    boolean shouldExit = handleInput(input);
                    if (shouldExit) {
                        return;
                    }
                } catch (ShinchanException e) {
                    printBoxedMessage(e.getMessage());
                }
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
            printList();
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
            printExit();
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
        printAdded(task);
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
        printAdded(task);
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
        printAdded(task);
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

        printLine();
        if (matching.isEmpty()) {
            System.out.println(messageNoTasksOnDate);
        } else {
            System.out.println("Here are the deadlines/events on " + date + ":");
            for (int i = 0; i < matching.size(); i++) {
                System.out.println((i + userIndexOffset) + ". " + matching.get(i));
            }
        }
        printLine();
    }

    private void handleMark(String input) throws ShinchanException {
        int index = parseTaskIndex(input);
        if (!isValidIndex(index)) {
            throw new ShinchanException(messageInvalidTaskNumber);
        }

        Task task = tasks.get(index);
        task.markAsDone();
        storage.save(tasks);
        printBoxedMessage(task.toString());
    }

    private void handleUnmark(String input) throws ShinchanException {
        int index = parseTaskIndex(input);
        if (!isValidIndex(index)) {
            throw new ShinchanException(messageInvalidTaskNumber);
        }

        Task task = tasks.get(index);
        task.markAsUndone();
        storage.save(tasks);
        printBoxedMessage(task.toString());
    }

    private void handleDelete(String input) throws ShinchanException {
        int index = parseTaskIndex(input);
        if (!isValidIndex(index)) {
            throw new ShinchanException(messageDeleteInvalid);
        }

        Task removed = tasks.remove(index);
        storage.save(tasks);

        printLine();
        System.out.println("Noted. I've removed this task:");
        System.out.println(removed);
        System.out.println("Now you have " + tasks.size() + " tasks in the list.");
        printLine();
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

    private void printGreeting() {
        printLine();
        System.out.println("Hello! I'm Shinchan!");
        System.out.println("What can I do for you?");
        printLine();
    }

    private void printExit() {
        printLine();
        System.out.println("Bye. Hope to see you again soon!");
        printLine();
    }

    private void printList() {
        printLine();
        System.out.println("Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println((i + userIndexOffset) + ". " + tasks.get(i));
        }
        printLine();
    }

    private void printAdded(Task task) {
        printLine();
        System.out.println("Got it. I've added this task:");
        System.out.println(task);
        System.out.println("Now you have " + tasks.size() + " tasks in the list.");
        printLine();
    }

    private void printLine() {
        System.out.println(lineSeparator);
    }

    private void printBoxedMessage(String message) {
        printLine();
        System.out.println(message);
        printLine();
    }
}
