package logistics_system.model;

public class Food extends Product {
    private final int daysToExpire;

    public Food(String name, double weight, int priority, int daysToExpire) {
        super(name, weight, priority);
        this.daysToExpire = daysToExpire;
    }

    public int getDaysToExpire() {
        return daysToExpire;
    }

    @Override
    public String toString() {
        return "Food{name='" + getName() +
                "', weight=" + getWeight() +
                ", expires in " + daysToExpire + " days}";
    }
}