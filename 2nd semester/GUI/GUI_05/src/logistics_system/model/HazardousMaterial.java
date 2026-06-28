package logistics_system.model;

public class HazardousMaterial extends Product {
    private final String hazardClass;

    public HazardousMaterial(String name, double weight, int priority, String hazardClass) {
        super(name, weight, priority);
        this.hazardClass = hazardClass;
    }

    public String getHazardClass() {
        return hazardClass;
    }

    @Override
    public String toString() {
        return "HazardousMaterial{name='" + getName() +
                "', class " + hazardClass + "}";
    }
}