import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

/**
 * Runs the Shinchan chatbot that manages a list of tasks.
 */
public class Shinchan {

    private static final String lineSeparator = "----------------------------------------";

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
            "The deadline command must include '/by' followed by the due date.";
    private static final String messageEventEmpty =
            "The description of an event cannot be empty.";
    private static final String messageEventMissingTime =
            "The event command must include '/from' and '/to' followed by the respective times.";
    private static final String messageDeleteInvalid =
            "Invalid task number for deletion.";

    private static final int splitLimitTwo = 2;
    private static final int userIndexOffset = 1;

    private final List<Task> tasks = new ArrayList<>();

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

        Task task = new ToDos(description);
        tasks.add(task);
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

        Task task = new Deadlines(description, by);
        tasks.add(task);
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

        String from = toParts[0].trim();
        String to = toParts[1].trim();

        Task task = new Events(description, from, to);
        tasks.add(task);
        printAdded(task);
    }

    private void handleMark(String input) throws ShinchanException {
        int taskIndex = parseTaskIndex(input);
        if (!isValidIndex(taskIndex)) {
            throw new ShinchanException(messageInvalidTaskNumber);
        }

        Task task = tasks.get(taskIndex);
        task.markAsDone();
        printBoxedMessage(task.toString());
    }

    private void handleUnmark(String input) throws ShinchanException {
        int taskIndex = parseTaskIndex(input);
        if (!isValidIndex(taskIndex)) {
            throw new ShinchanException(messageInvalidTaskNumber);
        }

        Task task = tasks.get(taskIndex);
        task.markAsUndone();
        printBoxedMessage(task.toString());
    }

    private void handleDelete(String input) throws ShinchanException {
        int taskIndex = parseTaskIndex(input);
        if (!isValidIndex(taskIndex)) {
            throw new ShinchanException(messageDeleteInvalid);
        }

        Task removed = tasks.remove(taskIndex);
        printLine();
        System.out.println("Noted. I've removed this task:");
        System.out.println(removed);
        System.out.println("Now you have " + tasks.size() + " tasks in the list.");
        printLine();
    }

    private int parseTaskIndex(String input) throws ShinchanException {
        String[] parts = input.split(" ", splitLimitTwo);
        if (parts.length < splitLimitTwo) {
            throw new ShinchanException(messageInvalidTaskNumber);
        }

        try {
            int userNumber = Integer.parseInt(parts[1].trim());
            return userNumber - userIndexOffset;
        } catch (NumberFormatException e) {
            throw new ShinchanException(messageInvalidTaskNumber);
        }
    }

    private boolean isValidIndex(int index) {
        return index >= 0 && index < tasks.size();
    }

    private String getRemainder(String input) {
        String[] parts = input.split(" ", splitLimitTwo);
        if (parts.length < splitLimitTwo) {
            return "";
        }
        return parts[1].trim();
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
            int displayIndex = i + userIndexOffset;
            System.out.println(displayIndex + ". " + tasks.get(i));
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
