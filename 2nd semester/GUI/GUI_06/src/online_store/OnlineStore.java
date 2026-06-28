package online_store;

import java.math.BigDecimal;
import java.util.*;

public class OnlineStore {

    private EnumMap<Category, List<Product>> productsByCategory;
    private Set<Product> allProducts;
    private Map<String, Product> productsByCode;
    private EnumMap<Category, BigDecimal> categoryRevenue;
    List<Order> orders;

    public OnlineStore() {
        productsByCategory = new EnumMap<>(Category.class);
        allProducts = new HashSet<>();
        productsByCode = new HashMap<>();
        orders = new ArrayList<>();
        categoryRevenue = new EnumMap<>(Category.class);

        for (Category category : Category.values()) {
            productsByCategory.put(category, new ArrayList<>());
            categoryRevenue.put(category, BigDecimal.ZERO);
        }
    }

    public boolean addProduct(Product product) {
        if (allProducts.contains(product)) {
            System.out.println("WARNING: Product with code " + product.getCode() + " already exists!");
            return false;
        }

        allProducts.add(product);
        productsByCategory.get(product.getCategory()).add(product);
        productsByCode.put(product.getCode(), product);
        System.out.println("ADDED: Product " + product.getName());
        return true;
    }

    public Product findProduct(String code) {
        return productsByCode.get(code);
    }

    public List<Product> findProductsByManufacturer(String manufacturer) {
        List<Product> result = new ArrayList<>();
        for (Product p : allProducts) {
            if (p.getManufacturer().equalsIgnoreCase(manufacturer)) {
                result.add(p);
            }
        }
        Collections.sort(result, ProductComparators.byName());
        return result;
    }

    public List<Product> findPromotions() {
        List<Product> result = new ArrayList<>();
        for (Product p : allProducts) {
            if (p.getDiscount() != DiscountType.NONE) {
                result.add(p);
            }
        }

        Collections.sort(result, ProductComparators.byPriceDescending());
        return result;
    }

    public List<Product> topProducts(int limit) {
        List<Product> productsWithReviews = new ArrayList<>();
        for (Product p : allProducts) {
            if (p.getReviewCount() > 0) {
                productsWithReviews.add(p);
            }
        }
        Collections.sort(productsWithReviews, ProductComparators.byRating());

        List<Product> result = new ArrayList<>();
        int counter = 0;
        for (Product p : productsWithReviews) {
            if (counter >= limit) break;
            result.add(p);
            counter++;
        }
        return result;
    }

    public List<Product> productsToReorder() {
        List<Product> result = new ArrayList<>();
        for (Product p : allProducts) {
            if (p.getStockQuantity() <= 5) {
                result.add(p);
            }
        }
        Collections.sort(result, ProductComparators.byStockQuantity());
        return result;
    }

    public Order createOrder(String customerName) {
        Order order = new Order(customerName);
        orders.add(order);
        return order;
    }

    public void displayCategories() {
        System.out.println("\n" + "=".repeat(100));
        System.out.println("PRODUCT CATEGORIES");
        System.out.println("=".repeat(100));

        for (Category category : Category.values()) {
            category.displayInfo();
        }
        System.out.println();
    }

    public void displayCategoryProducts(Category category, Comparator<Product> comparator) {
        List<Product> products = new ArrayList<>(productsByCategory.get(category));

        if (products.isEmpty()) {
            System.out.println("No products in category: " + category.getDisplayName());
            return;
        }

        if (comparator != null) {
            Collections.sort(products, comparator);
        }

        System.out.println("\n" + "=".repeat(100));
        System.out.println("CATEGORY: " + category.getDisplayName().toUpperCase());
        System.out.println("=".repeat(100));

        for (Product p : products) {
            System.out.println(p);
        }

        System.out.println("=".repeat(100));
        System.out.printf("Number of products: %d%n%n", products.size());
    }

    public void displayAllProducts(Comparator<Product> comparator, String sortDescription) {
        List<Product> productList = new ArrayList<>(allProducts);
        Collections.sort(productList, comparator);

        System.out.println("\n" + "=".repeat(100));
        System.out.println("ALL PRODUCTS");
        System.out.println("Sorting: " + sortDescription);
        System.out.println("=".repeat(100));

        for (Product p : productList) {
            System.out.println(p);
        }

        System.out.println("=".repeat(100));
        System.out.printf("Total products: %d%n%n", productList.size());
    }


    public void displayStatistics() {
        System.out.println("\n" + "=".repeat(100));
        System.out.println("STORE STATISTICS");
        System.out.println("=".repeat(100));

        BigDecimal totalValue = BigDecimal.ZERO;
        BigDecimal totalProfit = BigDecimal.ZERO;

        for (Category category : Category.values()) {
            List<Product> products = productsByCategory.get(category);
            int count = products.size();

            BigDecimal stockValue = BigDecimal.ZERO;
            for (Product p : products) {
                stockValue = stockValue.add(p.calculateStockValue());
            }

            BigDecimal potentialProfit = BigDecimal.ZERO;
            for (Product p : products) {
                potentialProfit = potentialProfit.add(p.calculatePotentialProfit());
            }

            totalValue = totalValue.add(stockValue);
            totalProfit = totalProfit.add(potentialProfit);

            System.out.printf("%-20s | Products: %4d | Value: $%10.2f | Profit: $%10.2f | Revenue: $%10.2f%n",
                    category.getDisplayName(), count,
                    stockValue, potentialProfit, categoryRevenue.get(category));
        }

        System.out.println("-".repeat(100));
        System.out.printf("TOTAL:                 Products: %4d | Value: $%10.2f | Potential profit: $%10.2f%n",
                allProducts.size(), totalValue, totalProfit);
        System.out.println("=".repeat(100) + "\n");
    }

    public void displayPromotions() {
        List<Product> promotions = findPromotions();

        if (promotions.isEmpty()) {
            System.out.println("No active promotions.");
            return;
        }

        System.out.println("\n" + "=".repeat(100));
        System.out.println("PROMOTIONS");
        System.out.println("=".repeat(100));

        for (Product p : promotions) {
            System.out.println(p);
        }

        System.out.println("=".repeat(100) + "\n");
    }

    public void displayTopProducts(int limit) {
        List<Product> top = topProducts(limit);

        if (top.isEmpty()) {
            System.out.println("No products with ratings.");
            return;
        }

        System.out.println("\n" + "=".repeat(100));
        System.out.printf("TOP %d PRODUCTS%n", limit);
        System.out.println("=".repeat(100));

        int position = 1;
        for (Product p : top) {
            System.out.printf("%2d. %s%n", position++, p);
        }

        System.out.println("=".repeat(100) + "\n");
    }

    public void displayProductsToReorder() {
        List<Product> toReorder = productsToReorder();

        if (toReorder.isEmpty()) {
            System.out.println("All products are sufficiently stocked.");
            return;
        }

        System.out.println("\n" + "=".repeat(100));
        System.out.println("PRODUCTS TO REORDER (stock <= 5 pcs)");
        System.out.println("=".repeat(100));

        for (Product p : toReorder) {
            System.out.println(p);
        }

        System.out.println("=".repeat(100) + "\n");
    }

    public void displayOrders() {
        if (orders.isEmpty()) {
            System.out.println("No orders.");
            return;
        }

        System.out.println("\n" + "=".repeat(100));
        System.out.println("ORDERS");
        System.out.println("=".repeat(100));

        List<Order> sortedOrders = new ArrayList<>(orders);
        Collections.sort(sortedOrders);

        for (Order o : sortedOrders) {
            System.out.println(o);
        }

        System.out.println("=".repeat(100) + "\n");
    }

    public void analyzeWithEnumSet() {
        System.out.println("\n" + "=".repeat(100));
        System.out.println("ANALYSIS USING ENUMSET");
        System.out.println("=".repeat(100));

        EnumSet<Category> highMargin = EnumSet.noneOf(Category.class);
        for (Category c : Category.values()) {
            if (c.getMargin() > 0.30) {
                highMargin.add(c);
            }
        }

        System.out.println("\nCategories with margin > 30%:");
        for (Category c : highMargin) {
            System.out.printf("  - %-20s (%.0f%%)%n", c.getDisplayName(), c.getMargin() * 100);
        }

        EnumSet<Category> withWarranty = EnumSet.noneOf(Category.class);
        for (Category c : Category.values()) {
            if (c.requiresWarranty()) {
                withWarranty.add(c);
            }
        }

        System.out.println("\nCategories requiring warranty:");
        for (Category c : withWarranty) {
            System.out.printf(
                    "  - %-20s (%d months)%n",
                    c.getDisplayName(), c.getDefaultWarrantyPeriod()
            );
        }

        EnumSet<Category> lowVAT = EnumSet.noneOf(Category.class);
        for (Category c : Category.values()) {
            if (c.getVatRate() < 0.20) {
                lowVAT.add(c);
            }
        }

        System.out.println("\nCategories with reduced VAT rate (<20%):");
        for (Category c : lowVAT) {
            System.out.printf("  - %-20s (%.0f%%)%n", c.getDisplayName(), c.getVatRate() * 100);
        }

        System.out.println("=".repeat(100) + "\n");
    }

    public static void main(String[] args) {
        OnlineStore store = new OnlineStore();

        System.out.println("=".repeat(100));
        System.out.println("ONLINE STORE - VERSION WITHOUT STREAM API AND ICONS");
        System.out.println("=".repeat(100) + "\n");

        // Display categories
        store.displayCategories();

        // Adding products
        System.out.println("Adding products to the store...\n");

        // ELECTRONICS
        Product laptop = new Product("EL001", "Dell XPS 15 Laptop",
                Category.ELECTRONICS, 3500, 5, "Dell");
        laptop.addReview(4.8);
        laptop.addReview(4.9);
        laptop.addReview(5.0);
        store.addProduct(laptop);

        Product smartphone = new Product("EL002", "Samsung Galaxy S23 Smartphone",
                Category.ELECTRONICS, 2800, 12, "Samsung");
        smartphone.addReview(4.7);
        smartphone.addReview(4.6);
        smartphone.setDiscount(DiscountType.SALE_10);
        store.addProduct(smartphone);

        Product headphones = new Product("EL003", "Sony WH-1000XM5 Headphones",
                Category.ELECTRONICS, 1200, 8, "Sony");
        headphones.addReview(5.0);
        headphones.addReview(4.9);
        headphones.addReview(4.8);
        headphones.addReview(5.0);
        store.addProduct(headphones);

        Product monitor = new Product("EL004", "LG UltraWide 34\" Monitor",
                Category.ELECTRONICS, 1800, 3, "LG");
        monitor.addReview(4.5);
        store.addProduct(monitor);

        Product tablet = new Product("EL005", "Apple iPad Pro 12.9\" Tablet",
                Category.ELECTRONICS, 4200, 2, "Apple");
        tablet.addReview(4.9);
        tablet.addReview(5.0);
        tablet.setDiscount(DiscountType.SALE_15);
        store.addProduct(tablet);

        // CLOTHING
        Product jacket = new Product("CL001", "The North Face Winter Jacket",
                Category.CLOTHING, 400, 15, "The North Face");
        jacket.addReview(4.3);
        jacket.addReview(4.5);
        store.addProduct(jacket);

        Product jeans = new Product("CL002", "Levi's 501 Jeans",
                Category.CLOTHING, 250, 25, "Levi's");
        jeans.addReview(4.7);
        store.addProduct(jeans);

        Product hoodie = new Product("CL003", "Nike Sportswear Hoodie",
                Category.CLOTHING, 180, 30, "Nike");
        hoodie.addReview(4.4);
        hoodie.setDiscount(DiscountType.SALE_5);
        store.addProduct(hoodie);

        Product shoes = new Product("CL004", "Adidas Ultraboost Running Shoes",
                Category.CLOTHING, 450, 4, "Adidas");
        shoes.addReview(4.8);
        shoes.addReview(4.9);
        store.addProduct(shoes);

        // HOME
        Product chair = new Product("HM001", "Ergonomic Office Chair",
                Category.HOME, 600, 10, "IKEA");
        chair.addReview(4.2);
        store.addProduct(chair);

        Product lamp = new Product("HM002", "LED Floor Lamp",
                Category.HOME, 150, 20, "Philips");
        lamp.addReview(4.0);
        store.addProduct(lamp);

        Product rug = new Product("HM003", "Wool Rug 200x300cm",
                Category.HOME, 800, 5, "Home&You");
        rug.addReview(4.6);
        store.addProduct(rug);

        Product sofa = new Product("HM004", "3-Seater Corner Sofa",
                Category.HOME, 2200, 3, "Black Red White");
        sofa.addReview(4.4);
        sofa.setDiscount(DiscountType.CLEARANCE_20);
        store.addProduct(sofa);

        // SPORTS
        Product bike = new Product("SP001", "Trek Mountain Bike",
                Category.SPORTS, 2500, 4, "Trek");
        bike.addReview(4.9);
        store.addProduct(bike);

        Product yogaMat = new Product("SP002", "Manduka Yoga Mat",
                Category.SPORTS, 220, 18, "Manduka");
        yogaMat.addReview(4.5);
        store.addProduct(yogaMat);

        Product dumbbells = new Product("SP003", "Adjustable Dumbbells 2x20kg",
                Category.SPORTS, 350, 12, "York");
        dumbbells.addReview(4.3);
        store.addProduct(dumbbells);

        Product treadmill = new Product("SP004", "Electric Treadmill",
                Category.SPORTS, 1800, 2, "NordicTrack");
        treadmill.addReview(4.7);
        treadmill.setDiscount(DiscountType.CLEARANCE_30);
        store.addProduct(treadmill);

        // BOOKS
        Product cleanCode = new Product("BK001", "Clean Code - Robert Martin",
                Category.BOOKS, 65, 40, "Helion");
        cleanCode.addReview(5.0);
        cleanCode.addReview(5.0);
        cleanCode.addReview(4.9);
        store.addProduct(cleanCode);

        Product sapiens = new Product("BK002", "Sapiens - Yuval Harari",
                Category.BOOKS, 45, 35, "Literary");
        sapiens.addReview(4.8);
        sapiens.addReview(4.9);
        store.addProduct(sapiens);

        Product witcher = new Product("BK003", "The Witcher - Andrzej Sapkowski",
                Category.BOOKS, 35, 50, "superNOVA");
        witcher.addReview(4.9);
        store.addProduct(witcher);

        Product atomic = new Product("BK004", "Atomic Habits - James Clear",
                Category.BOOKS, 55, 25, "Business Books");
        atomic.addReview(4.7);
        atomic.addReview(4.8);
        store.addProduct(atomic);

        // HEALTH
        Product vitamins = new Product("HL001", "Vitamin D3 4000 IU",
                Category.HEALTH, 25, 60, "Olimp");
        vitamins.addReview(4.5);
        store.addProduct(vitamins);

        Product protein = new Product("HL002", "Whey Protein WPC 1kg",
                Category.HEALTH, 80, 30, "MyProtein");
        protein.addReview(4.6);
        protein.setDiscount(DiscountType.SALE_10);
        store.addProduct(protein);

        Product cream = new Product("HL003", "Anti-Wrinkle Cream",
                Category.HEALTH, 120, 15, "Nivea");
        cream.addReview(4.2);
        store.addProduct(cream);

        System.out.println();

        // Attempt to add duplicate
        store.addProduct(
                new Product("EL001", "Another laptop",Category.ELECTRONICS, 4000, 2, "HP")
        );

        // STATISTICS
        System.out.println("\n" + "=".repeat(100));
        store.displayStatistics();

        // PROMOTIONS
        System.out.println("=".repeat(100));
        store.displayPromotions();

        // TOP PRODUCTS
        System.out.println("=".repeat(100));
        store.displayTopProducts(5);

        // PRODUCTS TO REORDER
        System.out.println("=".repeat(100));
        store.displayProductsToReorder();


        // CATEGORY PRODUCTS
        System.out.println("\n" + "=".repeat(100));
        store.displayCategoryProducts(
                Category.ELECTRONICS, ProductComparators.byPriceDescending()
        );

        System.out.println("=".repeat(100));
        store.displayCategoryProducts(
                Category.BOOKS, ProductComparators.byRating()
        );

        // DIFFERENT SORTINGS
        System.out.println("\n" + "=".repeat(100));
        store.displayAllProducts(
                ProductComparators.byRating(), "by rating (best first)"
        );

        System.out.println("=".repeat(100));
        store.displayAllProducts(
                ProductComparators.multiCriteria(),
                "multi-criteria: category -> rating -> price -> name"
        );

        System.out.println("=".repeat(100));
        store.displayAllProducts(
                ProductComparators.byProfit(),
                "by potential profit (highest first)");

        // PRODUCT DETAILS
        System.out.println("\n" + "=".repeat(100));
        System.out.println("SELECTED PRODUCT DETAILS");
        System.out.println("=".repeat(100) + "\n");

        laptop.displayDetails();
        System.out.println();
        smartphone.displayDetails();

        // CREATING ORDERS
        System.out.println("\n" + "=".repeat(100));
        System.out.println("CREATING ORDERS");
        System.out.println("=".repeat(100) + "\n");

        try {
            Order order1 = store.createOrder("John Doe");
            order1.addItem(laptop, 1);
            order1.addItem(headphones, 2);
            order1.addItem(cleanCode, 3);
            order1.setStatus(OrderStatus.CONFIRMED);
            System.out.println("Created: " + order1);

            Order order2 = store.createOrder("Jane Smith");
            order2.addItem(smartphone, 1);
            order2.addItem(hoodie, 2);
            order2.addItem(sapiens, 1);
            order2.setStatus(OrderStatus.PROCESSING);
            System.out.println("Created: " + order2);

            Order order3 = store.createOrder("Peter Johnson");
            order3.addItem(bike, 1);
            order3.addItem(yogaMat, 1);
            order3.setStatus(OrderStatus.SHIPPED);
            System.out.println("Created: " + order3);

        } catch (IllegalStateException e) {
            System.out.println("ERROR: " + e.getMessage());
        }

        // DISPLAY ORDERS
        System.out.println("\n" + "=".repeat(100));
        store.displayOrders();

        // ORDER DETAILS
        System.out.println("=".repeat(100));
        System.out.println("SELECTED ORDER DETAILS");
        System.out.println("=".repeat(100) + "\n");

        if (!store.orders.isEmpty()) {
            store.orders.get(0).displayDetails();
        }

        // ENUMSET ANALYSIS
        System.out.println("\n" + "=".repeat(100));
        store.analyzeWithEnumSet();

        // SEARCH
        System.out.println("=".repeat(100));
        System.out.println("PRODUCT SEARCH");
        System.out.println("=".repeat(100) + "\n");

        System.out.println("Sony products:");
        List<Product> sonyProducts = store.findProductsByManufacturer("Sony");
        for (Product p : sonyProducts) {
            System.out.println("  - " + p);
        }

        System.out.println("\nProduct with code EL002:");
        Product found = store.findProduct("EL002");
        if (found != null) {
            System.out.println("  - " + found);
        }

        // ENUMMAP DEMONSTRATION
        System.out.println("\n" + "=".repeat(100));
        System.out.println("ENUMMAP DEMONSTRATION");
        System.out.println("=".repeat(100) + "\n");

        System.out.println("Number of products in each category:");
        for (Map.Entry<Category, List<Product>> entry :
                store.productsByCategory.entrySet()) {
            System.out.printf("  %-20s: %2d products%n",
                    entry.getKey().getDisplayName(),
                    entry.getValue().size());
        }

        // METHOD COMPARISON
        System.out.println("\n" + "=".repeat(100));
        System.out.println("DEMONSTRATION OF equals(), hashCode() and Comparable");
        System.out.println("=".repeat(100) + "\n");

        Product p1 = store.findProduct("EL001");
        Product p2 = store.findProduct("EL002");
        Product p3 = store.findProduct("EL001");

        System.out.println("p1 (EL001): " + p1.getName());
        System.out.println("p2 (EL002): " + p2.getName());
        System.out.println("p3 (EL001): " + p1.getName());
        System.out.println();

        System.out.println("p1 == p3:          " + (p1 == p3));
        System.out.println("p1.equals(p3):     " + p1.equals(p3));
        System.out.println("p1.hashCode():     " + p1.hashCode());
        System.out.println("p3.hashCode():     " + p3.hashCode());
        System.out.println();

        System.out.println("p1.compareTo(p2):  " + p1.compareTo(p2) +
                " (alphabetic sorting: " + p1.getName() + " vs " + p2.getName() + ")");

        // ENUM METHODS DEMONSTRATION
        System.out.println("\n" + "=".repeat(100));
        System.out.println("ENUM METHODS DEMONSTRATION");
        System.out.println("=".repeat(100) + "\n");

        System.out.println("All categories (values()):");
        for (Category c : Category.values()) {
            System.out.println("  - " + c.name() + " -> " + c.getDisplayName());
        }

        System.out.println("\nOrdinal (position in enum):");
        for (Category c : Category.values()) {
            System.out.printf("  %s: ordinal = %d%n", c.getDisplayName(), c.ordinal());
        }

        System.out.println("\nvalueOf() - String to Enum conversion:");
        try {
            Category cat = Category.valueOf("ELECTRONICS");
            System.out.println("  Category.valueOf(\"ELECTRONICS\") = " + cat.getDisplayName());

            Category cat2 = Category.findByName("Books");
            System.out.println("  Category.findByName(\"Books\") = " + cat2.getDisplayName());
        } catch (IllegalArgumentException e) {
            System.out.println("  Error: " + e.getMessage());
        }

        // STATUS DEMONSTRATION
        System.out.println("\n" + "=".repeat(100));
        System.out.println("ENUM AvailabilityStatus DEMONSTRATION");
        System.out.println("=".repeat(100) + "\n");

        int[] testQuantities = {0, 2, 5, 10, 100};
        for (int qty : testQuantities) {
            AvailabilityStatus status = AvailabilityStatus.determineStatus(qty);
            System.out.printf("Quantity %3d pcs -> %s%n", qty, status.getDescription());
        }

        // DISCOUNT DEMONSTRATION
        System.out.println("\n" + "=".repeat(100));
        System.out.println("ENUM DiscountType DEMONSTRATION");
        System.out.println("=".repeat(100) + "\n");

        BigDecimal basePrice = BigDecimal.valueOf(1000);
        System.out.println("Base price: $" + basePrice + "\n");

        for (DiscountType discount : DiscountType.values()) {
            BigDecimal discountedPrice = discount.applyDiscount(basePrice);
            System.out.printf("%-20s -> $%.2f%n", discount, discountedPrice);
        }
    }
}