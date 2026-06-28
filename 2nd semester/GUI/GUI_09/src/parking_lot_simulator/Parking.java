package parking_lot_simulator;

import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

// Represents the multi-story parking lot
public class Parking {

    private final int LEVELS = 3;
    private final int SPOTS_PER_LEVEL = 5;
    private final int TOTAL_CAPACITY = LEVELS * SPOTS_PER_LEVEL;

    private final Semaphore semaphore;
    private final AtomicInteger[] spotsOccupiedPerLevel;
    private final AtomicInteger totalOccupied;
    private final Object displayLock = new Object();
    private final AtomicInteger totalCarsServed;
    private final long simulationStartTime;

    public Parking() {
        semaphore = new Semaphore(TOTAL_CAPACITY, true); // fair semaphore
        spotsOccupiedPerLevel = new AtomicInteger[LEVELS];
        for (int i = 0; i < LEVELS; i++) {
            spotsOccupiedPerLevel[i] = new AtomicInteger(0);
        }
        totalOccupied = new AtomicInteger(0);
        totalCarsServed = new AtomicInteger(0);
        simulationStartTime = System.currentTimeMillis();
    }

    // Handles a car entering; returns the level where it parked
    public int enter(String carNumber) throws InterruptedException {
        System.out.println(carNumber + " is waiting for a spot...");
        semaphore.acquire(); // block until a spot is free

        int level = findFreeLevel();

        synchronized (displayLock) {
            spotsOccupiedPerLevel[level].incrementAndGet();
            totalOccupied.incrementAndGet();
            totalCarsServed.incrementAndGet();
            System.out.println("[+] " + carNumber + " parked on level " + (level + 1));
            displayStatus();
        }

        return level;
    }

    // Handles a car exiting from the given level
    public void exit(String carNumber, int level) {
        synchronized (displayLock) {
            spotsOccupiedPerLevel[level].decrementAndGet();
            totalOccupied.decrementAndGet();
            semaphore.release();
            System.out.println("[-] " + carNumber + " left from level " + (level + 1));
            displayStatus();
        }
    }

    // Finds the first level that has a free spot
    private int findFreeLevel() {
        for (int i = 0; i < LEVELS; i++) {
            if (spotsOccupiedPerLevel[i].get() < SPOTS_PER_LEVEL) {
                return i;
            }
        }
        return 0; // fallback (should not happen if semaphore is correct)
    }

    // Prints the current occupancy status of each level
    private void displayStatus() {
        System.out.println("=== PARKING STATUS ===");
        for (int i = 0; i < LEVELS; i++) {
            int occupied = spotsOccupiedPerLevel[i].get();
            System.out.print("Level " + (i + 1) + ": [");
            for (int j = 0; j < SPOTS_PER_LEVEL; j++) {
                System.out.print(j < occupied ? "X" : "_");
            }
            System.out.println("] (" + occupied + "/" + SPOTS_PER_LEVEL + ")");
        }
        System.out.println("Occupied spots: " + totalOccupied.get() + "/" + TOTAL_CAPACITY);
        System.out.println("======================\n");
    }

    // Prints final simulation statistics
    public void displayStatistics() {
        long simulationTime = System.currentTimeMillis() - simulationStartTime;
        System.out.println("=== FINAL STATISTICS ===");
        System.out.printf("Total simulation time: %.3f seconds%n", simulationTime / 1000.0);
        System.out.println("Total cars served: " + totalCarsServed.get());
        System.out.println("Available spots: " + semaphore.availablePermits() + "/" + TOTAL_CAPACITY);
        System.out.println("========================");
    }
}