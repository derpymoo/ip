import java.util.Scanner;
import java.util.ArrayList;

public class Shinchan {
    private static final String LINE = "----------------------------------------";
    public ArrayList<String> list = new ArrayList<>();

    public static void main(String[] args) {
        Shinchan shinchan = new Shinchan();
        shinchan.run();
    }

    public void run() {
        greet();
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String input = scanner.nextLine();
            if (input.equalsIgnoreCase("bye")) {
                exit();
                break;
            } else if (input.equalsIgnoreCase("list")) {
                System.out.println(LINE);
                for (int i = 0; i < list.size(); i++) {
                    System.out.println((i + 1) + ". " + list.get(i));
                }
                System.err.println(LINE);
            } else {
                list.add(input);
                echo(input);
            }
        }
        scanner.close();
    }

    public void echo(String input) {
        System.out.println(LINE);
        System.out.println("added: " + input);
        System.err.println(LINE);
    }

    public void greet() {
        System.out.println(LINE);
        System.out.println("Hello! I'm Shinchan!");
        System.err.println("What can I do for you?");
        System.err.println(LINE);
    }

    public void exit() {
        System.out.println(LINE);
        System.out.println("Bye. Hope to see you again soon!");
        System.err.println(LINE);
    }
}
