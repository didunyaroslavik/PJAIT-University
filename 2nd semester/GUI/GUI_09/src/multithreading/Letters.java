package multithreading;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

// Manages a group of threads, each printing a different letter
public class Letters implements Iterable<Thread> {

    private final List<Thread> threads = new ArrayList<>();
    private final List<LetterPrinter> printers = new ArrayList<>();

    public Letters(String letters) {
        for (int i = 0; i < letters.length(); i++) {
            char letter = letters.charAt(i);
            LetterPrinter printer = new LetterPrinter(letter);
            Thread thread = new Thread(printer, "Thread " + letter);
            printers.add(printer);
            threads.add(thread);
        }
    }

    // Starts all threads
    public void start() {
        for (Thread t : threads) {
            t.start();
        }
    }

    // Stops all printers and interrupts all threads
    public void stop() {
        for (LetterPrinter printer : printers) {
            printer.stopRunning();
        }
        for (Thread t : threads) {
            t.interrupt();
        }
    }

    @Override
    public Iterator<Thread> iterator() {
        return threads.iterator();
    }
}