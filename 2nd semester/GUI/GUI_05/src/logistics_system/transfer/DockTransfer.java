package logistics_system.transfer;

import logistics_system.container.Box;

public class DockTransfer {

    public static <T> void transfer(Box<? extends T> source, Box<? super T> destination) {
        if (source.isEmpty()) {
            throw new IllegalStateException("Source box is empty, nothing to transfer.");
        }
        T item = source.getAndClear();
        destination.put(item);
        System.out.println("[DOCK] Transferred: " + item.toString());
    }

    public static <T> void copy(Box<? extends T> source, Box<? super T> destination) {
        if (source.isEmpty()) {
            throw new IllegalStateException("Source box is empty, nothing to copy.");
        }
        T item = source.get();
        destination.put(item);
        System.out.println("[DOCK] Copied: " + item.toString());
    }
}