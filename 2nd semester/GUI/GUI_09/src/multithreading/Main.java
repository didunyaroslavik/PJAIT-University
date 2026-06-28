package multithreading;

public class Main {

    public static void main(String[] args) {
        Letters letters = new Letters("ABCD");

        // List all thread names before starting
        for (Thread t : letters)
            System.out.println(t.getName() + " starting");

        letters.start();

        try {
            Thread.sleep(5000); // Let threads run for 5 seconds
        } catch (InterruptedException ignore) {
        }

        letters.stop();
        System.out.println("\nProgram completed.");
    }
}