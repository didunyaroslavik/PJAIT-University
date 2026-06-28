package parking_lot_simulator;

public class ParkingSimulator {

    public static void main(String[] args) throws InterruptedException {
        Parking parking = new Parking();
        final int NUMBER_OF_CARS = 20;
        Thread[] cars = new Thread[NUMBER_OF_CARS];

        System.out.println(">>> PARKING SIMULATION START <<<\n");

        for (int i = 0; i < NUMBER_OF_CARS; i++) {
            String carNumber = String.format("Car-%02d", i + 1);
            cars[i] = new Thread(new Car(carNumber, parking));
            cars[i].start();
        }

        // Wait for all cars to finish
        for (int i = 0; i < NUMBER_OF_CARS; i++) {
            cars[i].join();
        }

        System.out.println("\n>>> SIMULATION END <<<");
        parking.displayStatistics();
    }
}