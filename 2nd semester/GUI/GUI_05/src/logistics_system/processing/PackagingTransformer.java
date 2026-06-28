package logistics_system.processing;

import logistics_system.model.Electronics;
import logistics_system.model.Food;

public class PackagingTransformer {

    public static final Transformer<Electronics, String> ELECTRONICS_TO_LABEL = e ->
            String.format("[ELECTRONICS] %s | %dV | %.1fkg", e.getName(), e.getVoltage(), e.getWeight());

    public static final Transformer<Food, Double> FOOD_WEIGHT_TO_GRAMS = f ->
            f.getWeight() * 1000.0;

    public static <T> Transformer<T, String> toStringTransformer() {
        return Object::toString;
    }
}