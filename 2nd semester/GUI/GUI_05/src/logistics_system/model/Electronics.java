package logistics_system.model;

public class Electronics extends Product {
    private final int voltage;

    public Electronics(String name, double weight, int priority, int voltage) {
        super(name, weight, priority);
        this.voltage = voltage;
    }

    public int getVoltage() {
        return voltage;
    }

    @Override
    public String toString() {
        return "Electronics{name='" + getName() +
                "', weight=" + getWeight() +
                ", voltage=" + voltage + "V}";
    }
}