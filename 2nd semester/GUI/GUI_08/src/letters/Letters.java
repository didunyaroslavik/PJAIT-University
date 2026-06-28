package letters;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Holds one thread per character of the given string.
 * Each thread continuously prints its assigned letter every second.
 * Implements Iterable<Thread> so the main method can use a for-each loop
 */
public class Letters implements Iterable<Thread> {

    private final List<Thread> threads = new ArrayList<>();

    public Letters(String text) {
        for (char c : text.toCharArray()) {
            Thread t = new Thread(() -> {
                // Keep printing until the thread is interrupted via stop()
                while (!Thread.currentThread().isInterrupted()) {
                    System.out.print(c);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        // Restore the interrupted flag and exit the loop cleanly
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            });
            t.setName("Thread " + c);   // gives the thread the name shown in output
            threads.add(t);
        }
    }

    /** Starts all threads. */
    public void start() {
        for (Thread t : threads) {
            t.start();
        }
    }

    /**
     * Stops all threads safely by interrupting them.
     * Thread.stop() / suspend() / resume() / destroy() are NOT used
     * because they are inherently unsafe.
     */
    public void stop() {
        for (Thread t : threads) {
            t.interrupt();
        }
    }

    /** Allows  for (Thread t : letters)  in the main method. */
    @Override
    public Iterator<Thread> iterator() {
        return threads.iterator();
    }
}
