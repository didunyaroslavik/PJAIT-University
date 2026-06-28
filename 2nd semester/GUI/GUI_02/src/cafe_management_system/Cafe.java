package cafe_management_system;

import java.util.Locale;

public class Cafe {

    private final String name;
    private Product[] menu;
    private int menuSize;
    private Order[] orders;
    private int orderCount;

    public Cafe(String name, int menuCapacity, int orderCapacity) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Cafe name cannot be empty.");
        }
        if (menuCapacity <= 0 || orderCapacity <= 0) {
            throw new IllegalArgumentException("Capacities must be greater than zero.");
        }
        this.name = name;
        this.menu = new Product[menuCapacity];
        this.menuSize = 0;
        this.orders = new Order[orderCapacity];
        this.orderCount = 0;
    }

    public void addProduct(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null.");
        }
        if (menuSize == menu.length) {
            Product[] newMenu = new Product[menu.length * 2];
            for (int i = 0; i < menuSize; i++) {
                newMenu[i] = menu[i];
            }
            menu = newMenu;
        }
        menu[menuSize] = product;
        menuSize++;
    }

    public boolean removeProduct(String productName) {
        int index = -1;
        for (int i = 0; i < menuSize; i++) {
            if (menu[i].name().equalsIgnoreCase(productName)) {
                index = i;
                break;
            }
        }
        if (index == -1) return false;

        for (int i = index; i < menuSize - 1; i++) {
            menu[i] = menu[i + 1];
        }
        menuSize--;
        menu[menuSize] = null;
        return true;
    }

    public Product[] getProductsByCategory(String category) {
        int count = 0;
        for (int i = 0; i < menuSize; i++) {
            if (menu[i].category().equalsIgnoreCase(category)) {
                count++;
            }
        }

        Product[] result = new Product[count];
        int idx = 0;
        for (int i = 0; i < menuSize; i++) {
            if (menu[i].category().equalsIgnoreCase(category)) {
                result[idx] = menu[i];
                idx++;
            }
        }
        return result;
    }

    public void sortMenuByPrice() {
        for (int i = 1; i < menuSize; i++) {
            Product key = menu[i];
            int j = i - 1;
            while (j >= 0 && menu[j].price() > key.price()) {
                menu[j + 1] = menu[j];
                j--;
            }
            menu[j + 1] = key;
        }
    }

    public void displayMenu() {
        System.out.println("=== MENU: " + name.toUpperCase() + " ===");
        for (int i = 0; i < menuSize; i++) {
            System.out.println((i + 1) + ". " + menu[i].formatted());
        }
        System.out.println();
    }

    public void addOrder(Order order) {
        if (order == null) {
            throw new IllegalArgumentException("Order cannot be null.");
        }
        if (orderCount == orders.length) {
            Order[] newOrders = new Order[orders.length * 2];
            for (int i = 0; i < orderCount; i++) {
                newOrders[i] = orders[i];
            }
            orders = newOrders;
        }
        orders[orderCount] = order;
        orderCount++;
    }

    public Order[] getOrdersByCustomer(String customerName) {
        int count = 0;
        for (int i = 0; i < orderCount; i++) {
            if (orders[i].getCustomer().name().equalsIgnoreCase(customerName)) {
                count++;
            }
        }

        Order[] result = new Order[count];
        int idx = 0;
        for (int i = 0; i < orderCount; i++) {
            if (orders[i].getCustomer().name().equalsIgnoreCase(customerName)) {
                result[idx] = orders[i];
                idx++;
            }
        }
        return result;
    }

    public void sortOrdersByTotal() {
        for (int i = 0; i < orderCount - 1; i++) {
            for (int j = 0; j < orderCount - 1 - i; j++) {
                if (orders[j].calculateTotal() > orders[j + 1].calculateTotal()) {
                    Order temp = orders[j];
                    orders[j] = orders[j + 1];
                    orders[j + 1] = temp;
                }
            }
        }
    }

    public String getName() { return name; }
    public int getMenuSize() { return menuSize; }
    public int getOrderCount() { return orderCount; }

    public Order[] getOrders() {
        Order[] copy = new Order[orderCount];
        for (int i = 0; i < orderCount; i++) {
            copy[i] = orders[i];
        }
        return copy;
    }

    // --- INNER CLASS: STATISTICS ---
    public static class Statistics {
        private final Order[] orders;
        private final int count;

        public Statistics(Order[] orders, int count) {
            if (orders == null || count <= 0) {
                throw new IllegalArgumentException("No orders to analyze.");
            }
            this.orders = orders;
            this.count = count;
        }

        public double totalRevenue() {
            double sum = 0;
            for (int i = 0; i < count; i++) {
                sum += orders[i].calculateTotal();
            }
            return sum;
        }

        public double averageOrderValue() {
            return totalRevenue() / count;
        }

        public Order mostExpensiveOrder() {
            Order max = orders[0];
            for (int i = 1; i < count; i++) {
                if (orders[i].calculateTotal() > max.calculateTotal()) {
                    max = orders[i];
                }
            }
            return max;
        }

        public Order cheapestOrder() {
            Order min = orders[0];
            for (int i = 1; i < count; i++) {
                if (orders[i].calculateTotal() < min.calculateTotal()) {
                    min = orders[i];
                }
            }
            return min;
        }

        public int totalItemsSold() {
            int total = 0;
            for (int i = 0; i < count; i++) {
                total += orders[i].getItemCount();
            }
            return total;
        }

        public String summary() {
            StringBuilder sb = new StringBuilder();
            sb.append("=== STATISTICS ===\n");
            sb.append(String.format("Number of orders: %d%n", count));
            sb.append(String.format("Units sold: %d%n", totalItemsSold()));
            sb.append(String.format(Locale.US, "Total revenue: %.2f PLN%n", totalRevenue()));
            sb.append(String.format(Locale.US, "Average value: %.2f PLN%n", averageOrderValue()));
            sb.append(String.format(Locale.US, "Most expensive: #%d (%.2f PLN)%n", mostExpensiveOrder().getId(), mostExpensiveOrder().calculateTotal()));
            sb.append(String.format(Locale.US, "Cheapest: #%d (%.2f PLN)%n", cheapestOrder().getId(), cheapestOrder().calculateTotal()));
            return sb.toString();
        }
    }

    // --- INNER CLASS: DAILY REPORT ---
    public class DailyReport {
        private final String reportDate;

        public DailyReport(String reportDate) {
            if (reportDate == null || reportDate.isBlank()) {
                throw new IllegalArgumentException("Report date cannot be empty.");
            }
            this.reportDate = reportDate;
        }

        public String generate() {
            StringBuilder sb = new StringBuilder();
            String eqSep = "=".repeat(50);

            sb.append(eqSep).append("\n");
            sb.append("DAILY REPORT: ").append(name).append("\n");
            sb.append("Date: ").append(reportDate).append("\n");
            sb.append(eqSep).append("\n\n");

            sb.append("Products in menu: ").append(menuSize).append("\n");
            sb.append("Orders: ").append(orderCount).append("\n\n");

            if (orderCount > 0) {
                sb.append("Order list\n");
                for (int i = 0; i < orderCount; i++) {
                    sb.append(String.format(Locale.US, "#%d %s %.2f PLN %d units%n",
                            orders[i].getId(),
                            orders[i].getCustomer().name(),
                            orders[i].calculateTotal(),
                            orders[i].getItemCount()));
                }
                sb.append("\n");

                Statistics stats = new Statistics(orders, orderCount);
                sb.append(String.format(Locale.US, "Total revenue: %.2f PLN%n", stats.totalRevenue()));
                sb.append(String.format(Locale.US, "Average value: %.2f PLN%n", stats.averageOrderValue()));
            } else {
                sb.append("No orders available.\n");
            }

            sb.append("\n").append(eqSep).append("\n");
            return sb.toString();
        }
    }
}
