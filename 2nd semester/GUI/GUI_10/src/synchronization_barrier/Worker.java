package synchronization_barrier;

/**
 * A worker thread that executes its task in three phases,
 * synchronizing with all other workers at a barrier after each phase.
 */
public class Worker extends Thread {

    private final CustomBarrier barrier;
    private final int taskDuration;   // milliseconds each phase takes
    private final String taskName;    // label for the task this worker performs

    public Worker(String name, CustomBarrier barrier, int taskDuration, String taskName) {
        super(name);
        this.barrier = barrier;
        this.taskDuration = taskDuration;
        this.taskName = taskName;
    }

    @Override
    public void run() {
        try {
            // Phase 1
            executeTask(1);
            barrier.await();

            // Phase 2
            executeTask(2);
            barrier.await();

            // Phase 3
            executeTask(3);
            barrier.await();

            System.out.println(getName() + " completed all phases!");

        } catch (InterruptedException e) {
            System.out.println("ERROR: " + getName() + " was interrupted!");
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Simulates work for one phase by sleeping for taskDuration milliseconds.
     */
    private void executeTask(int phase) throws InterruptedException {
        System.out.println(getName() + " starting phase " + phase + " (" + taskName + ")");
        Thread.sleep(taskDuration);
        System.out.println(getName() + " completed phase " + phase);
    }
}
