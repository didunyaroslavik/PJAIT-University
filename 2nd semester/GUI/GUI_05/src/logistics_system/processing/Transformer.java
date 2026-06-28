package logistics_system.processing;

@FunctionalInterface
public interface Transformer<T, R> {
    R transform(T input);
}