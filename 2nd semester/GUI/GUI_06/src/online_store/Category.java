package online_store;

import java.math.BigDecimal;
import java.math.RoundingMode;

public enum Category {
    ELECTRONICS("Electronics", 0.25, 0.23, 24) {
        @Override
        public String getDescription() {
            return "Electronic equipment, computers, smartphones and accessories";
        }
        @Override
        public boolean requiresWarranty() {
            return true;
        }
        @Override
        public int getDefaultWarrantyPeriod() {
            return 24;
        }
    },
    CLOTHING("Clothing", 0.40, 0.23, 0) {
        @Override
        public String getDescription() {
            return "Women's, men's and children's clothing and accessories";
        }
        @Override
        public boolean requiresWarranty() {
            return false;
        }
        @Override
        public int getDefaultWarrantyPeriod() {
            return 0;
        }
    },
    HOME("Home & Garden", 0.30, 0.23, 12) {
        @Override
        public String getDescription() {
            return "Furniture, decorations, tools and home equipment";
        }
        @Override
        public boolean requiresWarranty() {
            return true;
        }
        @Override
        public int getDefaultWarrantyPeriod() {
            return 12;
        }
    },
    SPORTS("Sports & Recreation", 0.35, 0.23, 6) {
        @Override
        public String getDescription() {
            return "Sports equipment, fitness, tourism and outdoor";
        }
        @Override
        public boolean requiresWarranty() {
            return false;
        }
        @Override
        public int getDefaultWarrantyPeriod() {
            return 6;
        }
    },
    BOOKS("Books", 0.20, 0.05, 0) {
        @Override
        public String getDescription() {
            return "Literature, guides, scientific and educational books";
        }
        @Override
        public boolean requiresWarranty() {
            return false;
        }
        @Override
        public int getDefaultWarrantyPeriod() {
            return 0;
        }
    },
    HEALTH("Health & Beauty", 0.50, 0.08, 0) {
        @Override
        public String getDescription() {
            return "Supplements, cosmetics, hygiene and care products";
        }
        @Override
        public boolean requiresWarranty() {
            return false;
        }
        @Override
        public int getDefaultWarrantyPeriod() {
            return 0;
        }
    };

    private final String displayName;
    private final double margin;
    private final double vatRate;
    private final int defaultWarrantyMonths;

    Category(String displayName, double margin, double vatRate, int defaultWarrantyMonths) {
        this.displayName = displayName;
        this.margin = margin;
        this.vatRate = vatRate;
        this.defaultWarrantyMonths = defaultWarrantyMonths;
    }

    public String getDisplayName() { return displayName; }
    public double getMargin() { return margin; }
    public double getVatRate() { return vatRate; }
    public int getDefaultWarrantyMonths() { return defaultWarrantyMonths; }

    public abstract String getDescription();
    public abstract boolean requiresWarranty();
    public abstract int getDefaultWarrantyPeriod();

    public BigDecimal calculateSellingPrice(BigDecimal purchasePrice) {
        BigDecimal priceWithMargin = purchasePrice.multiply(BigDecimal.valueOf(1 + margin));
        BigDecimal grossPrice = priceWithMargin.multiply(BigDecimal.valueOf(1 + vatRate));
        return grossPrice.setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal calculateVAT(BigDecimal sellingPrice) {
        BigDecimal netPrice = sellingPrice.divide(BigDecimal.valueOf(1 + vatRate), 2, RoundingMode.HALF_UP);
        return sellingPrice.subtract(netPrice);
    }

    public void displayInfo() {
        System.out.printf("%-20s | Margin: %5.1f%% | VAT: %5.1f%% | Warranty: %2d months | %s%n",
                displayName, margin * 100, vatRate * 100, defaultWarrantyMonths, getDescription());
    }

    public static Category findByName(String name) {
        for (Category c : values()) {
            if (c.getDisplayName().equalsIgnoreCase(name) || c.name().equalsIgnoreCase(name)) {
                return c;
            }
        }
        return null;
    }
}