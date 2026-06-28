package multithreaded_robot_assembly_line;

import java.util.LinkedList;
import java.util.Queue;

public class Buffer {

    private final Queue<Robot> queue;
    private final int          capacity;
    private final String       name;
    private       boolean      closed;

    public Buffer(String name, int capacity) {
        this.name     = name;
        this.capacity = capacity;
        this.queue    = new LinkedList<>();
        this.closed   = false;
    }

    /** Adds a robot; blocks if the buffer is full. Throws if the buffer is closed. */
    public synchronized void put(Robot robot) throws InterruptedException {
        while (queue.size() >= capacity && !closed) {
            System.out.println("[BUFFER][" + name + "] Buffer full ("
                    + queue.size() + "/" + capacity + "), "
                    + robot.getName() + " is waiting...");
            wait();
        }
        if (closed) {
            throw new InterruptedException("Buffer " + name + " is closed.");
        }
        queue.add(robot);
        robot.setCurrentStatus("WAITING_IN_" + name);
        System.out.println("[BUFFER][" + name + "] " + robot.getName()
                + " added to buffer. State: " + queue.size() + "/" + capacity);
        notifyAll();
    }

    /** Retrieves and removes the head robot; blocks if empty. Returns null when closed and empty. */
    public synchronized Robot take() throws InterruptedException {
        while (queue.isEmpty() && !closed) {
            System.out.println("[BUFFER][" + name + "] Buffer empty, worker is waiting...");
            wait();
        }
        // Buffer is closed and fully drained → signal end-of-work to the caller
        if (queue.isEmpty() && closed) {
            return null;
        }
        Robot robot = queue.poll();
        System.out.println("[BUFFER][" + name + "] " + robot.getName()
                + " taken from buffer. State: " + queue.size() + "/" + capacity);
        notifyAll();
        return robot;
    }

    /** Closes the buffer and wakes all waiting threads. */
    public synchronized void close() {
        closed = true;
        System.out.println("[BUFFER][" + name + "] Buffer closed.");
        notifyAll();
    }

    public synchronized int     size()     { return queue.size(); }
    public synchronized boolean isClosed() { return closed; }
    public             String   getName()  { return name; }
}
