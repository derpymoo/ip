public class Shinchan {
    private static final String LINE = "----------------------------------------";

    public static void main(String[] args) {
        Shinchan shinchan = new Shinchan();
        shinchan.greet();
        shinchan.exit();
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
