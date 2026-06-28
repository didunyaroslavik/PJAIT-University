package logistics_system.model;

public abstract class Product {
    private final String name;
    private final double weight;
    private final int priority;

    public Product(String name, double weight, int priority) {
        this.name = name;
        this.weight = weight;
        this.priority = priority;
    }

    public String getName() {
        return name;
    }

    public double getWeight() {
        return weight;
    }

    public int getPriority() {
        return priority;
    }
}