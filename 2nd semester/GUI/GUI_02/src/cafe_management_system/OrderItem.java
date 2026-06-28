package cafe_management_system;

import java.util.Locale;

public record OrderItem(Product product, int quantity) {

    public OrderItem {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null.");
        }
        if (quantity < 1) {
            throw new IllegalArgumentException("Quantity must be at least 1. Received: " + quantity);
        }
    }

    public double totalPrice() {
        return product.price() * quantity;
    }

    public String formatted() {
        String left = String.format("%dx %s", quantity, product.name());
        String right = String.format(Locale.US, "%.2f PLN", totalPrice());

        int targetWidth = 40;
        int spaces = targetWidth - left.length() - right.length();
        if (spaces < 1) spaces = 1;

        return left + " ".repeat(spaces) + right;
    }
}