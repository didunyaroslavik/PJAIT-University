package letters;

public class Main {

    // This main method is provided by the assignment and must not be modified.
    public static void main(String[] args) {
        Letters letters = new Letters("ABCD");
        for (Thread t : letters)
            System.out.println(t.getName() + " starting");
        letters.start();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException ignore) {
        }
        letters.stop();
        System.out.println("\nProgram completed.");
    }
}
