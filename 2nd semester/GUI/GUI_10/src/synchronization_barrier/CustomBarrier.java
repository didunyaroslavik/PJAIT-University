package synchronization_barrier;

/**
 * A reusable synchronization barrier that makes a group of threads
 * wait until all of them have reached a common checkpoint.
 */
public class CustomBarrier {

    private final int limit;      // total threads required to release the barrier
    private int count = 0;        // threads currently waiting at the barrier
    private int generation = 0;   // barrier phase identifier (increments each cycle)

    public CustomBarrier(int limit) {
        if (limit <= 0) {
            throw new IllegalArgumentException(
                "Limit must be greater than 0, but got: " + limit
            );
        }
        this.limit = limit;
    }

    /**
     * Blocks the calling thread until all 'limit' threads have called await().
     * The last thread to arrive releases everyone and advances the generation.
     */
    public synchronized void await() throws InterruptedException {
        int currentGeneration = generation;
        count++;

        System.out.println(Thread.currentThread().getName()
            + " arrived at barrier (" + count + "/" + limit + ")"
            + " [generation: " + currentGeneration + "]");

        if (count < limit) {
            // Not everyone is here yet — wait until the generation changes
            while (currentGeneration == generation && count < limit) {
                wait();
            }
        } else {
            // This thread is the last one — release all waiting threads
            System.out.println(">>> Barrier reached! Releasing all threads <<<");
            count = 0;
            generation++;
            notifyAll();
        }
    }

    /** Returns how many threads are currently waiting at the barrier. */
    public synchronized int getWaitingCount() {
        return count;
    }

    /** Returns the current generation (phase) of the barrier. */
    public synchronized int getGeneration() {
        return generation;
    }

    /** Forcefully resets the barrier and releases any waiting threads. */
    public synchronized void reset() {
        count = 0;
        generation++;
        notifyAll();
    }
}
