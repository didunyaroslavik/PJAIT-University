package cafe_management_system;

public abstract class Discount {

    private final String description;

    public Discount(String description) {
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Discount description cannot be empty.");
        }
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public abstract double apply(double originalPrice);

    public double savings(double originalPrice) {
        return originalPrice - apply(originalPrice);
    }

    @Override
    public String toString() {
        return "Discount: " + description;
    }
}