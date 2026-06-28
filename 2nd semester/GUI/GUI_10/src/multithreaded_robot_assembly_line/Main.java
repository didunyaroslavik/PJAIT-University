package multithreaded_robot_assembly_line;

public class Main {

    private static final int NUMBER_OF_ROBOTS        = 6;
    private static final int ROBOT_ENTRY_DELAY_MIN   = 200;  // ms
    private static final int ROBOT_ENTRY_DELAY_MAX   = 500;  // ms

    public static void main(String[] args) {

        AssemblyLine assemblyLine = new AssemblyLine();
        assemblyLine.start();

        // Manager thread: creates robots and places them on the line one by one
        Thread manager = new Thread(() -> {
            try {
                for (int i = 0; i < NUMBER_OF_ROBOTS; i++) {
                    Robot robot = new Robot();
                    assemblyLine.addRobot(robot);

                    // Random delay between introducing consecutive robots
                    int delay = ROBOT_ENTRY_DELAY_MIN
                            + (int)(Math.random() * (ROBOT_ENTRY_DELAY_MAX - ROBOT_ENTRY_DELAY_MIN));
                    Thread.sleep(delay);
                }
                System.out.println("[MANAGER] All robots have entered the assembly line.");
            } catch (InterruptedException e) {
                System.out.println("[MANAGER] Manager thread interrupted.");
                Thread.currentThread().interrupt();
            }
        }, "Manager");

        manager.start();

        // Wait for all robots to be injected, then shut down the line gracefully
        try {
            manager.join();
            assemblyLine.shutdown();
        } catch (InterruptedException e) {
            System.out.println("[MAIN] Main thread interrupted.");
            Thread.currentThread().interrupt();
        }

        System.out.println("[MAIN] Program finished.");
    }
}
