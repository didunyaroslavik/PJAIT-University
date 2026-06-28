package cafe_management_system;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Order {

    private int id;
    private final OrderItem[] items;
    private final Customer customer;
    private final LocalDateTime createdAt;
    private Discount discount;

    private Order(int id, OrderItem[] items, Customer customer, Discount discount) {
        this.id = id;
        this.items = new OrderItem[items.length];
        for (int i = 0; i < items.length; i++) {
            this.items[i] = items[i];
        }
        this.customer = customer;
        this.discount = discount;
        this.createdAt = LocalDateTime.now();
    }

    public int getId() { return id; }

    public Customer getCustomer() { return customer; }

    public Discount getDiscount() { return discount; }

    public void setDiscount(Discount discount) { this.discount = discount; }

    public OrderItem[] getItems() {
        OrderItem[] copy = new OrderItem[items.length];
        for (int i = 0; i < items.length; i++) {
            copy[i] = items[i];
        }
        return copy;
    }

    public int getLineCount() {
        return items.length;
    }

    public int getItemCount() {
        int count = 0;
        for (int i = 0; i < items.length; i++) {
            count += items[i].quantity();
        }
        return count;
    }

    public double calculateSubtotal() {
        double sum = 0;
        for (int i = 0; i < items.length; i++) {
            sum += items[i].totalPrice();
        }
        return sum;
    }

    public double calculateTotal() {
        double subtotal = calculateSubtotal();
        if (discount != null) {
            return discount.apply(subtotal);
        }
        return subtotal;
    }

    @Override
    public String toString() {
        return String.format(Locale.US, "Order #%d (%s) -> %.2f PLN (%d units)",
                id, customer.name(), calculateTotal(), getItemCount());
    }

    // --- INNER CLASS: RECEIPT ---
    public class Receipt {
        private final String cashierName;
        private static final String CAFE_NAME = "CAFE \"UNDER JAVA\"";
        private static final int WIDTH = 42;

        public Receipt(String cashierName) {
            if (cashierName == null || cashierName.isBlank()) {
                throw new IllegalArgumentException("Cashier name cannot be empty.");
            }
            this.cashierName = cashierName;
        }

        private String center(String text) {
            int spaces = Math.max(0, (WIDTH - text.length()) / 2);
            return " ".repeat(spaces) + text;
        }

        private String formatLine(String label, double amount) {
            String right = String.format(Locale.US, "%.2f PLN", amount);
            int spaces = WIDTH - label.length() - right.length();
            if (spaces < 1) spaces = 1;
            return label + " ".repeat(spaces) + right;
        }

        private String formatNegLine(String label, double amount) {
            String right = String.format(Locale.US, "-%.2f PLN", amount);
            int spaces = WIDTH - label.length() - right.length();
            if (spaces < 1) spaces = 1;
            return label + " ".repeat(spaces) + right;
        }

        public String generate() {
            StringBuilder sb = new StringBuilder();
            String eqSep = "=".repeat(WIDTH);
            String dashSep = "-".repeat(WIDTH);

            sb.append(eqSep).append("\n");
            sb.append(center(CAFE_NAME)).append("\n");
            sb.append(eqSep).append("\n");

            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
            sb.append(String.format("Date: %s%n", createdAt.format(dtf)));
            sb.append(String.format("Cashier: %s%n", cashierName));
            sb.append(String.format("Order: #%d%n", id));
            sb.append(String.format("Customer: %s [%s]%n", customer.name(), customer.loyaltyLevel()));
            sb.append("\n");

            for (int i = 0; i < items.length; i++) {
                sb.append(items[i].formatted()).append("\n");
            }

            sb.append("\n").append(dashSep).append("\n");

            double subtotal = calculateSubtotal();
            sb.append(formatLine("Subtotal:", subtotal)).append("\n");

            if (discount != null) {
                double savings = discount.savings(subtotal);
                sb.append(formatNegLine("Discount: " + discount.getDescription(), savings)).append("\n");
            }

            sb.append(dashSep).append("\n");
            sb.append(formatLine("TOTAL DUE:", calculateTotal())).append("\n");
            sb.append(eqSep).append("\n");
            sb.append(center("Thank you!")).append("\n");
            sb.append(eqSep).append("\n");

            return sb.toString();
        }
    }

    // --- INNER CLASS: BUILDER ---
    public static class Builder {
        private static final int INITIAL_CAPACITY = 4;
        private final int id;
        private final Customer customer;
        private OrderItem[] items;
        private int size;
        private Discount discount;

        public Builder(int id, Customer customer) {
            if (id <= 0) {
                throw new IllegalArgumentException("Order ID must be greater than zero.");
            }
            if (customer == null) {
                throw new IllegalArgumentException("Customer cannot be null.");
            }
            this.id = id;
            this.customer = customer;
            this.items = new OrderItem[INITIAL_CAPACITY];
            this.size = 0;
        }

        private void grow() {
            OrderItem[] newItems = new OrderItem[items.length * 2];
            for (int i = 0; i < size; i++) {
                newItems[i] = items[i];
            }
            items = newItems;
        }

        public Builder addItem(Product product, int quantity) {
            if (size == items.length) {
                grow();
            }
            items[size] = new OrderItem(product, quantity);
            size++;
            return this;
        }

        public Builder addItem(Product product) {
            return addItem(product, 1);
        }

        public Builder withDiscount(Discount discount) {
            this.discount = discount;
            return this;
        }

        public Order build() {
            if (size == 0) {
                throw new IllegalStateException("Order must contain at least one item.");
            }
            OrderItem[] trimmed = new OrderItem[size];
            for (int i = 0; i < size; i++) {
                trimmed[i] = items[i];
            }
            return new Order(id, trimmed, customer, discount);
        }
    }
}