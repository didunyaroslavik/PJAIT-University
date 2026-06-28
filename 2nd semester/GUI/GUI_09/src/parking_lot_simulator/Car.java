package parking_lot_simulator;

import java.util.Random;

// Represents a single car running as a thread
public class Car implements Runnable {

    private final String number;
    private final Parking parking;
    private final Random random = new Random();

    public Car(String number, Parking parking) {
        this.number = number;
        this.parking = parking;
    }

    @Override
    public void run() {
        try {
            // Random delay before arriving at the parking lot
            Thread.sleep(random.nextInt(3000));

            int level = parking.enter(number);

            // Random time spent parked (2–5 seconds)
            int parkingTime = 2000 + random.nextInt(3000);
            Thread.sleep(parkingTime);

            parking.exit(number, level);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("ERROR: " + number + " was interrupted.");
        }
    }
}