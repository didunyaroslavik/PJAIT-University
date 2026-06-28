package multithreaded_robot_assembly_line;

public class StageWorker extends Thread {

    private final Stage  stage;
    private final Buffer inputBuffer;
    private final Buffer outputBuffer;   // null for the last stage (Inspector)
    private volatile boolean running;

    public StageWorker(String workerName, Stage stage,
                       Buffer inputBuffer, Buffer outputBuffer) {
        super(workerName);
        this.stage        = stage;
        this.inputBuffer  = inputBuffer;
        this.outputBuffer = outputBuffer;
        this.running      = true;
    }

    @Override
    public void run() {
        System.out.println("[WORKER][" + getName() + "] Worker started at stage: " + stage.getName());
        try {
            while (running) {
                Robot robot = inputBuffer.take();

                // null means the buffer is closed and empty → work is done
                if (robot == null) {
                    System.out.println("[WORKER][" + getName() + "] No more robots - shutting down.");
                    break;
                }

                stage.acquire(robot);
                try {
                    stage.process(robot);
                } finally {
                    // Release the station even if process() was interrupted
                    stage.release(robot);
                }

                if (outputBuffer != null) {
                    // Pass the robot to the next stage
                    outputBuffer.put(robot);
                } else {
                    // Last stage: record total production time
                    long totalTime = System.currentTimeMillis() - robot.getCreatedAt();
                    System.out.println("[COMPLETE] " + robot.getName()
                            + " finished! Total production time: " + totalTime + "ms");
                }
            }
        } catch (InterruptedException e) {
            System.out.println("[WORKER][" + getName() + "] Worker interrupted.");
            Thread.currentThread().interrupt();
        }
        System.out.println("[WORKER][" + getName() + "] Worker finished.");
    }

    /** Emergency stop: sets the flag and interrupts any blocking call. */
    public void stopWorker() {
        running = false;
        interrupt();
    }
}
