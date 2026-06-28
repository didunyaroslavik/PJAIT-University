package logistics_system.sorting;

import java.util.Comparator;
import java.util.function.Function;

public class FieldComparator<T> implements Comparator<T> {
    private final Function<T, ? extends Comparable> fieldExtractor;
    private final boolean ascending;

    public FieldComparator(Function<T, ? extends Comparable> fieldExtractor, boolean ascending) {
        this.fieldExtractor = fieldExtractor;
        this.ascending = ascending;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public int compare(T o1, T o2) {
        Comparable val1 = fieldExtractor.apply(o1);
        Comparable val2 = fieldExtractor.apply(o2);
        int result = val1.compareTo(val2);
        return ascending ? result : -result;
    }

    public static <T, U extends Comparable<U>> FieldComparator<T> ascending(Function<T, U> extractor) {
        return new FieldComparator<>(extractor, true);
    }

    public static <T, U extends Comparable<U>> FieldComparator<T> descending(Function<T, U> extractor) {
        return new FieldComparator<>(extractor, false);
    }
}