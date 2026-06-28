package cafe_management_system;

import java.util.Locale;

public class PercentageDiscount extends Discount {

    private final double percentage;

    public PercentageDiscount(double percentage) {
        super(String.format(Locale.US, "%.1f%% off", percentage));
        if (percentage <= 0 || percentage > 100) {
            throw new IllegalArgumentException("Discount percentage must be in the range (0, 100]. Received: " + percentage);
        }
        this.percentage = percentage;
    }

    public double getPercentage() {
        return percentage;
    }

    @Override
    public double apply(double originalPrice) {
        double discounted = originalPrice * (1 - percentage / 100.0);
        return Math.max(0, discounted);
    }
}