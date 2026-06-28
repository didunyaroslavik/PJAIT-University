package online_store;

import java.math.BigDecimal;
import java.math.RoundingMode;

public enum DiscountType {
    NONE(0),
    SALE_5(5),
    SALE_10(10),
    SALE_15(15),
    CLEARANCE_20(20),
    CLEARANCE_30(30),
    CLEARANCE_50(50),
    LIQUIDATION_70(70);

    private final int discountPercent;

    DiscountType(int discountPercent) {
        this.discountPercent = discountPercent;
    }

    public int getDiscountPercent() {
        return discountPercent;
    }

    public BigDecimal applyDiscount(BigDecimal price) {
        if (discountPercent == 0) {
            return price;
        }
        BigDecimal multiplier = BigDecimal.valueOf(1 - (discountPercent / 100.0));
        return price.multiply(multiplier).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public String toString() {
        if (discountPercent == 0) {
            return "No discount";
        }
        return "-" + discountPercent + "%";
    }
}