package cafe_management_system;

import java.util.Comparator;

public class Demo {

    public static void main(String[] args) {

        Cafe cafe = new Cafe("Pod Javą", 4, 4);

        Product cappuccino = new Product("Cappuccino", 12.50, "coffee");
        Product espresso   = new Product("Espresso", 7.50, "coffee");
        Product latte      = new Product("Latte", 14.00, "coffee");
        Product americano  = new Product("Americano", 9.00, "coffee");
        Product sernik     = new Product("Cheesecake", 14.50, "cake");
        Product brownie    = new Product("Brownie", 11.00, "cake");
        Product croissant  = new Product("Croissant", 8.50, "pastry");
        Product herbata    = new Product("Earl Grey Tea", 9.00, "tea");

        cafe.addProduct(cappuccino);
        cafe.addProduct(espresso);
        cafe.addProduct(latte);
        cafe.addProduct(americano);
        cafe.addProduct(sernik);
        cafe.addProduct(brownie);
        cafe.addProduct(croissant);
        cafe.addProduct(herbata);

        cafe.displayMenu();

        cafe.sortMenuByPrice();
        System.out.println("(after sorting by price)\n");
        cafe.displayMenu();

        System.out.println("=== COFFEE IN THE MENU ===\n");
        Product[] kawy = cafe.getProductsByCategory("coffee");
        for (int i = 0; i < kawy.length; i++) {
            System.out.println("  " + kawy[i].formatted());
        }
        System.out.println();

        Customer anna  = new Customer("Anna", "anna@mail.com", 150);
        Customer tomek = new Customer("Tomek", "tomek@mail.com", 30);
        Customer kasia = new Customer("Kasia", "kasia@mail.com", 250);

        System.out.println("=== CUSTOMERS ===\n");
        Customer[] customers = {anna, tomek, kasia};
        for (int i = 0; i < customers.length; i++) {
            System.out.println("  " + customers[i].formatted());
        }
        System.out.println();

        Order order1 = new Order.Builder(1, anna)
                .addItem(cappuccino, 2)
                .addItem(sernik)
                .addItem(espresso, 3)
                .withDiscount(new PercentageDiscount(10))
                .build();
        cafe.addOrder(order1);

        System.out.println("=== ORDER 1 ===\n");
        System.out.println("Number of lines: " + order1.getLineCount());
        System.out.println("Number of items: " + order1.getItemCount());
        System.out.println("Subtotal:        " + String.format("%.2f PLN", order1.calculateSubtotal()));
        System.out.println("Total:           " + String.format("%.2f PLN", order1.calculateTotal()));
        System.out.println();

        Order.Receipt receipt1 = order1.new Receipt("Anna");
        System.out.println(receipt1.generate());

        Order order2 = new Order.Builder(2, tomek)
                .addItem(latte)
                .addItem(croissant, 2)
                .withDiscount(new FixedAmountDiscount(5.0))
                .build();
        cafe.addOrder(order2);

        System.out.println("=== ORDER 2 ===\n");
        System.out.println(order2.new Receipt("Tomek").generate());

        Discount loyaltyDiscount = new Discount("Loyalty " + kasia.loyaltyLevel()) {
            @Override
            public double apply(double originalPrice) {
                double percent = Math.min(kasia.loyaltyPoints() / 10.0, 25.0);
                return Math.max(0, originalPrice * (1 - percent / 100.0));
            }
        };

        Order order3 = new Order.Builder(3, kasia)
                .addItem(herbata, 2)
                .addItem(sernik)
                .addItem(brownie, 2)
                .withDiscount(loyaltyDiscount)
                .build();
        cafe.addOrder(order3);

        System.out.println("=== ORDER 3 (loyalty discount — anonymous class) ===\n");
        System.out.println(order3.new Receipt("Kasia").generate());

        Order order4 = new Order.Builder(4, anna)
                .addItem(americano, 3)
                .addItem(croissant)
                .build();
        cafe.addOrder(order4);

        System.out.println("=== ORDER 4 (no discount) ===\n");
        System.out.println(order4.new Receipt("Anna").generate());

        cafe.sortOrdersByTotal();

        System.out.println("=== ORDER RANKING (ascending by total) ===\n");
        Order[] sorted = cafe.getOrders();
        for (int i = 0; i < sorted.length; i++) {
            System.out.printf(
                    "  Order #%d (%s) → %6.2f PLN  (%d items)%n",
                    sorted[i].getId(),
                    sorted[i].getCustomer().name(),
                    sorted[i].calculateTotal(),
                    sorted[i].getItemCount()
            );
        }
        System.out.println();

        Order[] ordersCopy = cafe.getOrders();

        Comparator<Order> byItemCountDesc = new Comparator<Order>() {
            @Override
            public int compare(Order o1, Order o2) {
                return Integer.compare(o2.getItemCount(), o1.getItemCount());
            }
        };

        for (int i = 0; i < ordersCopy.length - 1; i++) {
            int minIdx = i;
            for (int j = i + 1; j < ordersCopy.length; j++) {
                if (byItemCountDesc.compare(ordersCopy[j], ordersCopy[minIdx]) < 0) {
                    minIdx = j;
                }
            }
            if (minIdx != i) {
                Order temp = ordersCopy[i];
                ordersCopy[i] = ordersCopy[minIdx];
                ordersCopy[minIdx] = temp;
            }
        }

        System.out.println("=== RANKING BY NUMBER OF ITEMS (descending) ===\n");
        for (int i = 0; i < ordersCopy.length; i++) {
            System.out.printf(
                    "  Order #%d → %d items (%s)%n",
                    ordersCopy[i].getId(),
                    ordersCopy[i].getItemCount(),
                    ordersCopy[i].getCustomer().name()
            );
        }
        System.out.println();

        System.out.println("=== ANNA'S ORDERS ===\n");
        Order[] annaOrders = cafe.getOrdersByCustomer("Anna");
        double annaTotal = 0;
        for (int i = 0; i < annaOrders.length; i++) {
            System.out.printf(
                    "  #%d → %.2f PLN%n",
                    annaOrders[i].getId(), annaOrders[i].calculateTotal()
            );
            annaTotal += annaOrders[i].calculateTotal();
        }
        System.out.printf("  Total: %.2f PLN (%d orders)%n%n", annaTotal, annaOrders.length);

        Cafe.Statistics stats = new Cafe.Statistics(cafe.getOrders(), cafe.getOrderCount());
        System.out.println(stats.summary());

        Cafe.DailyReport report = cafe.new DailyReport("2025-01-15");
        System.out.println(report.generate());

        System.out.println("=== VALIDATION TESTS ===\n");

        try {
            new Product("", 10.0, "coffee");
        } catch (IllegalArgumentException e) {
            System.out.println("  [OK] Product: " + e.getMessage());
        }

        try {
            new Product("X", -5.0, "coffee");
        } catch (IllegalArgumentException e) {
            System.out.println("  [OK] Product: " + e.getMessage());
        }

        try {
            new OrderItem(cappuccino, 0);
        } catch (IllegalArgumentException e) {
            System.out.println("  [OK] OrderItem: " + e.getMessage());
        }

        try {
            new PercentageDiscount(150);
        } catch (IllegalArgumentException e) {
            System.out.println("  [OK] PercentageDiscount: " + e.getMessage());
        }

        try {
            new FixedAmountDiscount(-10);
        } catch (IllegalArgumentException e) {
            System.out.println("  [OK] FixedAmountDiscount: " + e.getMessage());
        }

        try {
            new Customer("Jan", "invalid-email", 0);
        } catch (IllegalArgumentException e) {
            System.out.println("  [OK] Customer: " + e.getMessage());
        }

        try {
            new Order.Builder(99, anna).build();
        } catch (IllegalStateException e) {
            System.out.println("  [OK] Builder: " + e.getMessage());
        }

        System.out.println("\n=== CHANGING DISCOUNT ON THE FLY ===\n");
        System.out.printf(
                "  Order #%d BEFORE change:  %.2f PLN%n",
                order4.getId(), order4.calculateTotal()
        );

        order4.setDiscount(new PercentageDiscount(15));
        System.out.printf(
                "  Order #%d AFTER 15%% discount: %.2f PLN%n",
                order4.getId(), order4.calculateTotal()
        );
        System.out.println();
        System.out.println(order4.new Receipt("Manager").generate());

        System.out.println("=== REMOVING PRODUCT FROM MENU ===\n");
        System.out.println("  Before: " + cafe.getMenuSize() + " products");
        boolean removed = cafe.removeProduct("Brownie");
        System.out.println("  Removed 'Brownie': " + removed);
        System.out.println("  After:  " + cafe.getMenuSize() + " products\n");
        cafe.displayMenu();
    }
}