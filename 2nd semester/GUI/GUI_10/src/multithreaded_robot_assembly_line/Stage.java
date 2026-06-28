package multithreaded_robot_assembly_line;

public class Stage {

    private final String name;
    private final int    processingTimeMin;
    private final int    processingTimeMax;

    private       boolean occupied;
    private       Robot   currentRobot;
    private       int     processedCount;

    public Stage(String name, int processingTimeMin, int processingTimeMax) {
        this.name              = name;
        this.processingTimeMin = processingTimeMin;
        this.processingTimeMax = processingTimeMax;
        this.occupied          = false;
        this.currentRobot      = null;
        this.processedCount    = 0;
    }

    /**
     * Blocks until the station is free, then marks it as occupied by the given robot.
     */
    public synchronized void acquire(Robot robot) throws InterruptedException {
        while (occupied) {
            System.out.println("[STAGE][" + name + "] " + name
                    + " is occupied by " + currentRobot.getName()
                    + ". " + robot.getName() + " is waiting.");
            wait();
        }
        occupied      = true;
        currentRobot  = robot;
        robot.setCurrentStatus("IN_STAGE_" + name);
        System.out.println("[STAGE][" + name + "] >>> " + robot.getName()
                + " starts processing. Station occupied.");
    }

    /**
     * Simulates work by sleeping for a random duration in [min, max).
     * Not synchronized — the thread should NOT hold the monitor during a long sleep.
     */
    public void process(Robot robot) throws InterruptedException {
        int time = processingTimeMin
                + (int)(Math.random() * (processingTimeMax - processingTimeMin));
        System.out.println("[STAGE][" + name + "] Processing " + robot.getName()
                + "... (time: " + time + "ms)");
        Thread.sleep(time);
    }

    /** Marks the station as free and wakes one waiting thread. */
    public synchronized void release(Robot robot) {
        occupied     = false;
        currentRobot = null;
        processedCount++;
        robot.setCurrentStatus("DONE_" + name);
        System.out.println("[STAGE][" + name + "] <<< " + robot.getName()
                + " finished processing. Station free. Total done: " + processedCount);
        notify();
    }

    public             String getName()           { return name; }
    public synchronized int   getProcessedCount() { return processedCount; }
    public synchronized boolean isOccupied()      { return occupied; }
}
