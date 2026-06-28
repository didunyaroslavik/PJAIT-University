package logistics_system.sorting;

public class SortingLine {

    public static <T> void sort(T[] items, FieldComparator<T> comparator) {
        // Bubble Sort
        for (int i = 0; i < items.length; i++) {
            for (int j = 0; j < items.length - 1 - i; j++) {
                if (comparator.compare(items[j], items[j + 1]) > 0) {
                    T temp = items[j];
                    items[j] = items[j + 1];
                    items[j + 1] = temp;
                }
            }
        }
    }

    public static <T> void printLine(String label, T[] items) {
        System.out.println(label);
        for (T item : items) {
            System.out.println("   " + item.toString());
        }
    }
}