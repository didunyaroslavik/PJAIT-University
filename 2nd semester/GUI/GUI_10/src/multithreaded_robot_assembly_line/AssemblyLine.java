package multithreaded_robot_assembly_line;

import java.util.ArrayList;
import java.util.List;

public class AssemblyLine {

    // Production stages
    private final Stage welding;
    private final Stage electronics;
    private final Stage software;
    private final Stage qualityControl;

    // Buffers between stages
    private final Buffer inputBuffer;       // Manager  → Welding
    private final Buffer bufferWeldElectr;  // Welding  → Electronics
    private final Buffer bufferElectrSoft;  // Electronics → Software
    private final Buffer bufferSoftQC;      // Software → Quality Control

    private final List<StageWorker> workers;
    private       int  totalRobotsCreated;
    private final long startTime;

    public AssemblyLine() {
        // Stage: name, min processing time (ms), max processing time (ms)
        welding        = new Stage("WELDING",         200, 600);
        electronics    = new Stage("ELECTRONICS",     400, 700);
        software       = new Stage("SOFTWARE",        150, 500);
        qualityControl = new Stage("QUALITY_CONTROL", 300, 700);

        // Each buffer holds at most 2 robots
        inputBuffer      = new Buffer("INPUT",        2);
        bufferWeldElectr = new Buffer("WELD->ELECTR", 2);
        bufferElectrSoft = new Buffer("ELECTR->SOFT", 2);
        bufferSoftQC     = new Buffer("SOFT->QC",     2);

        workers = new ArrayList<>();
        startTime = System.currentTimeMillis();
        initializeWorkers();
    }

    /** Wires each worker to its stage and the two buffers surrounding it. */
    private void initializeWorkers() {
        workers.add(new StageWorker("Welder-1",     welding,        inputBuffer,      bufferWeldElectr));
        workers.add(new StageWorker("Technician-1", electronics,    bufferWeldElectr, bufferElectrSoft));
        workers.add(new StageWorker("Developer-1",  software,       bufferElectrSoft, bufferSoftQC));
        workers.add(new StageWorker("Inspector-1",  qualityControl, bufferSoftQC,     null));
    }

    /** Prints the startup banner and launches all worker threads. */
    public void start() {
        System.out.println("========================================");
        System.out.println("ROBOT ASSEMBLY LINE - START");
        System.out.println("========================================");
        for (StageWorker worker : workers) {
            worker.start();
        }
    }

    /** Injects one robot onto the line via the input buffer. */
    public void addRobot(Robot robot) throws InterruptedException {
        totalRobotsCreated++;
        System.out.println("[LINE] New robot entering the line: "
                + robot.getName() + " (total: " + totalRobotsCreated + ")");
        inputBuffer.put(robot);
    }

    /**
     * Cascade shutdown: close each buffer, wait for the downstream worker to
     * drain it completely, then close the next buffer.
     */
    public void shutdown() throws InterruptedException {
        System.out.println("[LINE] Initiating assembly line shutdown...");

        inputBuffer.close();
        workers.get(0).join();
        System.out.println("[LINE] Welder finished work.");

        bufferWeldElectr.close();
        workers.get(1).join();
        System.out.println("[LINE] Technician finished work.");

        bufferElectrSoft.close();
        workers.get(2).join();
        System.out.println("[LINE] Developer finished work.");

        bufferSoftQC.close();
        workers.get(3).join();
        System.out.println("[LINE] Inspector finished work.");

        printSummary();
    }

    /** Displays total counts and elapsed wall-clock time. */
    private void printSummary() {
        long totalTime = System.currentTimeMillis() - startTime;
        System.out.println("PRODUCTION SUMMARY");
        System.out.println("Robots produced: "   + totalRobotsCreated);
        System.out.println("Stage WELDING: "      + welding.getProcessedCount());
        System.out.println("Stage ELECTRONICS: "  + electronics.getProcessedCount());
        System.out.println("Stage SOFTWARE: "     + software.getProcessedCount());
        System.out.println("Stage QUALITY CONTROL: " + qualityControl.getProcessedCount());
        System.out.println("Total working time: " + totalTime + " ms");
        System.out.println("========================================");
    }
}
