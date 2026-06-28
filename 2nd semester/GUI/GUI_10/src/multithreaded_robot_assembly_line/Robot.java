package multithreaded_robot_assembly_line;

public class Robot {

    private static int counter = 0;

    private final int    id;
    private final String name;
    private volatile String currentStatus;   // volatile: status is read/written by multiple threads
    private final long   createdAt;

    public Robot() {
        counter++;
        id         = counter;
        name       = "Robot-" + String.format("%03d", id);
        currentStatus = "CREATED";
        createdAt  = System.currentTimeMillis();
    }

    public int    getId()             { return id; }
    public String getName()           { return name; }
    public String getCurrentStatus()  { return currentStatus; }
    public long   getCreatedAt()      { return createdAt; }

    public void setCurrentStatus(String status) {
        this.currentStatus = status;
    }

    @Override
    public String toString() { return name; }
}
