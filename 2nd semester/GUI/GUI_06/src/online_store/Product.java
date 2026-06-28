package online_store;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Product implements Comparable<Product> {
    private String code;
    private String name;
    private Category category;
    private BigDecimal purchasePrice;
    private BigDecimal sellingPrice;
    private int stockQuantity;
    private LocalDate deliveryDate;
    private DiscountType discount;
    private String manufacturer;
    private double rating; // 0.0 - 5.0
    private int reviewCount;

    public Product(String code, String name, Category category,
                   double purchasePrice, int stockQuantity, String manufacturer) {
        this.code = code;
        this.name = name;
        this.category = category;
        this.purchasePrice = BigDecimal.valueOf(purchasePrice);
        this.sellingPrice = category.calculateSellingPrice(this.purchasePrice);
        this.stockQuantity = stockQuantity;
        this.deliveryDate = LocalDate.now();
        this.discount = DiscountType.NONE;
        this.manufacturer = manufacturer;
        this.rating = 0.0;
        this.reviewCount = 0;
    }

    // Getters
    public String getCode() { return code; }
    public String getName() { return name; }
    public Category getCategory() { return category; }
    public BigDecimal getPurchasePrice() { return purchasePrice; }
    public BigDecimal getSellingPrice() { return sellingPrice; }
    public BigDecimal getPriceAfterDiscount() {
        return discount.applyDiscount(sellingPrice);
    }
    public int getStockQuantity() { return stockQuantity; }
    public LocalDate getDeliveryDate() { return deliveryDate; }
    public DiscountType getDiscount() { return discount; }
    public String getManufacturer() { return manufacturer; }
    public double getRating() { return rating; }
    public int getReviewCount() { return reviewCount; }

    // Setters
    public void setDiscount(DiscountType discount) {
        this.discount = discount;
    }

    public boolean sell(int quantity) {
        if (quantity > stockQuantity) {
            return false;
        }
        stockQuantity -= quantity;
        return true;
    }

    public void addReview(double newRating) {
        if (newRating < 0 || newRating > 5) {
            throw new IllegalArgumentException("Rating must be between 0-5");
        }
        this.rating = ((rating * reviewCount) + newRating) / (reviewCount + 1);
        this.reviewCount++;
    }

    public AvailabilityStatus getStatus() {
        return AvailabilityStatus.determineStatus(stockQuantity);
    }

    // Calculations
    public BigDecimal calculatePotentialProfit() {
        BigDecimal profitPerUnit = getPriceAfterDiscount().subtract(purchasePrice);
        return profitPerUnit.multiply(BigDecimal.valueOf(stockQuantity))
                .setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal calculateStockValue() {
        return purchasePrice.multiply(BigDecimal.valueOf(stockQuantity))
                .setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal calculateVATAmount() {
        return category.calculateVAT(getPriceAfterDiscount());
    }

    public int getDaysInStock() {
        return (int) java.time.temporal.ChronoUnit.DAYS.between(deliveryDate, LocalDate.now());
    }

    // TODO 01
    @Override
    public int compareTo(Product other) {
        return this.name.compareToIgnoreCase(other.name);
    }

    // TODO 02
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Objects.equals(code, product.code);
    }

    // TODO 03
    @Override
    public int hashCode() {
        return Objects.hash(code);
    }

    @Override
    public String toString() {
        String discountInfo = discount == DiscountType.NONE ? "" : " " + discount;
        return String.format("[%s] %-40s | $%.2f%s | %d pcs | %s | Rating: %.1f (%d reviews)",
                code, name, getPriceAfterDiscount(), discountInfo,
                stockQuantity, getStatus().getDescription(), rating, reviewCount);
    }

    public void displayDetails() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

        System.out.println("================================================================================");
        System.out.println("PRODUCT DETAILS - " + category.getDisplayName());
        System.out.println("================================================================================");
        System.out.printf("Code:                 %s%n", code);
        System.out.printf("Name:                 %s%n", name);
        System.out.printf("Manufacturer:         %s%n", manufacturer);
        System.out.printf("Category:             %s%n", category.getDisplayName());
        System.out.println("--------------------------------------------------------------------------------");
        System.out.printf("Purchase price:       $%.2f%n", purchasePrice);
        System.out.printf("Selling price:        $%.2f%n", sellingPrice);

        if (discount != DiscountType.NONE) {
            System.out.printf("Discount:             %s%n", discount);
            System.out.printf("Price after discount: $%.2f%n", getPriceAfterDiscount());
        }

        System.out.printf("VAT (%.0f%%):            $%.2f%n",
                category.getVatRate() * 100, calculateVATAmount());
        System.out.println("--------------------------------------------------------------------------------");
        System.out.printf("Stock quantity:       %d pcs%n", stockQuantity);
        System.out.printf("Status:               %s%n", getStatus().getDescription());
        System.out.printf("Stock value:          $%.2f%n", calculateStockValue());
        System.out.printf("Potential profit:     $%.2f%n", calculatePotentialProfit());
        System.out.println("--------------------------------------------------------------------------------");
        System.out.printf("Delivery date:        %s%n", deliveryDate.format(formatter));
        System.out.printf("Days in stock:        %d%n", getDaysInStock());

        if (category.requiresWarranty()) {
            System.out.printf("Warranty:             %d months%n",
                    category.getDefaultWarrantyPeriod());
        }

        System.out.println("--------------------------------------------------------------------------------");
        System.out.printf("Rating:               %.1f / 5.0%n", rating);
        System.out.printf("Review count:         %d%n", reviewCount);
        System.out.println("================================================================================");
    }
}