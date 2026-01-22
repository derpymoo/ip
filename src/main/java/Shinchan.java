
import java.util.ArrayList;
import java.util.Scanner;

public class Shinchan {

    private static final String LINE = "----------------------------------------";
    public ArrayList<Task> list = new ArrayList<>();

    public static void main(String[] args) {
        Shinchan shinchan = new Shinchan();
        shinchan.run();
    }

    public void run() {
        greet();
        Scanner scanner = new Scanner(System.in);

        while (scanner.hasNextLine()) {
            try {
                String input = scanner.nextLine().trim();

                if (input.isEmpty()) {
                    throw new ShinchanException("Input cannot be empty. Please enter a valid command.");
                }

                String instruction = input.split(" ", 2)[0].toLowerCase();

                switch (instruction) {
                    case "todo":
                        if (input.length() == 4) {
                            throw new ShinchanException("The description of a todo cannot be empty.");
                        }
                        newToDos(input);
                        break;

                    case "list":
                        printList();
                        break;
                    case "deadline":
                        if (input.indexOf("deadline ") + 9 >= input.indexOf(" /by ")) {
                            throw new ShinchanException("The description of a deadline cannot be empty.");
                        }
                        if (!input.contains(" /by ")) {
                            throw new ShinchanException("The deadline command must include '/by' followed by the due date.");
                        }
                        newDeadlines(input);
                        break;
                    case "event":
                        if (input.indexOf("event ") + 6 >= input.indexOf("/from")) {
                            throw new ShinchanException("The description of an event cannot be empty.");
                        }
                        if (!input.contains("/from") || !input.contains("/to")) {
                            throw new ShinchanException("The event command must include '/from' and '/to' followed by the respective times.");
                        }
                        newEvents(input);
                        break;
                    case "mark":
                        int index = Integer.parseInt(input.split(" ")[1]) - 1;
                        markTask(index);
                        break;
                    case "unmark":
                        int idx = Integer.parseInt(input.split(" ")[1]) - 1;
                        unmarkTask(idx);
                        break;
                    case "bye":
                        exit();
                        scanner.close();
                        return;

                    default:
                        throw new ShinchanException("I'm sorry, but I don't know what that means.");
                }
            } catch (ShinchanException e) {
                System.out.println(LINE);
                System.out.println(e.getMessage());
                System.out.println(LINE);
            }
        }
    }

    public void newToDos(String input) {
        String[] parts = input.split(" ", 2);
        Task item = new ToDos(parts[1]);
        list.add(item);
        echo(item);
    }

    public void newDeadlines(String input) {
        String[] parts = input.split(" ", 2); // split into "deadline" and the rest
        String[] desctime = parts[1].split(" /by ", 2); // split into description and due date
        Task item = new Deadlines(desctime[0], desctime[1]);
        list.add(item);
        echo(item);
    }

    public void newEvents(String input) {
        String[] fromSplit = input.split("/from", 2);
        String description = fromSplit[0].replaceFirst("event ", "").trim();
        String timing = fromSplit[1].trim();
        String[] toSplit = timing.split("/to", 2);
        String from = toSplit[0].trim();
        String to = toSplit[1].trim();
        Task item = new Events(description, from, to);
        list.add(item);
        echo(item);
    }

    public void echo(Task item) {
        System.out.println(LINE);
        System.out.println("Got it. I've added this task:");
        System.out.println(item.toString());
        System.out.println("Now you have " + list.size() + " tasks in the list.");
        System.out.println(LINE);
    }

    public void greet() {
        System.out.println(LINE);
        System.out.println("Hello! I'm Shinchan!");
        System.out.println("What can I do for you?");
        System.out.println(LINE);
    }

    public void exit() {
        System.out.println(LINE);
        System.out.println("Bye. Hope to see you again soon!");
        System.out.println(LINE);
    }

    public void printList() {
        System.out.println(LINE);
        System.out.println("Here are the tasks in your list:");
        for (int i = 0; i < list.size(); i++) {
            System.out.println(i + 1 + "." + list.get(i).toString());
        }
        System.out.println(LINE);
    }

    public void markTask(int index) {
        if (index < 0 || index >= list.size()) {
            System.out.println("Invalid task number.");
            return;
        }
        Task item = list.get(index);
        item.markAsDone();
        System.out.println(LINE);
        System.out.println(item.toString());
        System.out.println(LINE);
    }

    public void unmarkTask(int index) {
        if (index < 0 || index >= list.size()) {
            System.out.println("Invalid task number.");
            return;
        }
        Task item = list.get(index);
        item.markAsUndone();
        System.out.println(LINE);
        System.out.println(item.toString());
        System.out.println(LINE);
    }

}
