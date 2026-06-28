package cafe_management_system;

import java.util.Locale;

public record Product(String name, double price, String category) {

    public Product {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Product name cannot be empty.");
        }
        if (price <= 0) {
            throw new IllegalArgumentException("Price must be greater than 0. Received: " + price);
        }
        if (category == null || category.isBlank()) {
            throw new IllegalArgumentException("Category cannot be empty.");
        }
    }

    public String formatted() {
        return String.format(Locale.US, "%s (%s) %.2f PLN", name, category, price);
    }
}