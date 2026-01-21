import java.util.Scanner;

public class Shinchan {
    private static final String LINE = "----------------------------------------";

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
            } else {
                echo(input);
            }
        }
        scanner.close();
    }

    public void echo(String input) {
        System.out.println(LINE);
        System.out.println(input);
        System.err.println(LINE);
    }

    public void greet() {
        System.out.println(LINE);
        System.out.println("Hello! I'm Shinchan!");
        System.err.println("What can I do for you?");
        System.err.println(LINE);
    }

    public void exit() {
        System.out.println("Bye. Hope to see you again soon!");
        System.err.println(LINE);
    }
}
