package logistics_system.processing;

import logistics_system.container.Box;
import logistics_system.container.Pair;

public class InspectionUtils {

    public static <T> void logInspection(T item) {
        System.out.println("=== INSPECTION LOG ===");
        System.out.println("Type : " + item.getClass().getSimpleName());
        System.out.println("Value: " + item.toString());
        System.out.println("========================");
    }

    public static <T> Pair<String, T> label(String id, T item) {
        System.out.printf("[LABELER] Assigning label '%s' to: %s%n", id, item.toString());
        return new Pair<>(id, item);
    }

    public static <T> void transferBetweenBoxes(Box<T> source, Box<T> destination) {
        T item = source.getAndClear();
        destination.put(item);
        System.out.println("[TRANSFER] Moved: " + item.toString());
    }
}