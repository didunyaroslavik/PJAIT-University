package multithreading;

// Prints a character repeatedly in a loop, runs on its own thread
public class LetterPrinter implements Runnable {

    private final char letter;
    private boolean running = true;

    public LetterPrinter(char letter) {
        this.letter = letter;
    }

    // Safely stops the loop from another thread
    public synchronized void stopRunning() {
        running = false;
    }

    // Safely reads the running state from any thread
    private synchronized boolean isRunning() {
        return running;
    }

    @Override
    public void run() {
        while (isRunning()) {
            System.out.print(letter);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                break; // Exit loop if thread is interrupted
            }
        }
    }
}