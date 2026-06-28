package logistics_system.registry;

import java.util.HashMap;
import java.util.Map;

public class WarehouseRegistry {
    private final Map<Class<?>, Object> modules;

    public WarehouseRegistry() {
        this.modules = new HashMap<>();
    }

    public <T> void put(Class<T> type, T instance) {
        modules.put(type, instance);
        System.out.println("[REGISTRY] Registered: " + type.getSimpleName());
    }

    public <T> T get(Class<T> type) {
        Object value = modules.get(type);
        if (value == null) {
            throw new IllegalArgumentException("Module not found for type: " + type.getSimpleName());
        }
        return type.cast(value);
    }

    public boolean contains(Class<?> type) {
        return modules.containsKey(type);
    }

    public int size() {
        return modules.size();
    }
}