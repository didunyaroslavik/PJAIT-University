package logistics_system.shipping;

import logistics_system.model.Product;

public class ShipmentBuilder<T extends Product> {
    private final T cargo;
    private String destination = "UNKNOWN";
    private int priorityOverride = -1;
    private boolean fragile = false;
    private boolean insured = false;

    public ShipmentBuilder(T cargo) {
        this.cargo = cargo;
    }

    public ShipmentBuilder<T> to(String destination) {
        this.destination = destination;
        return this;
    }

    public ShipmentBuilder<T> withPriority(int priority) {
        this.priorityOverride = priority;
        return this;
    }

    public ShipmentBuilder<T> fragile() {
        this.fragile = true;
        return this;
    }

    public ShipmentBuilder<T> insured() {
        this.insured = true;
        return this;
    }

    public Shipment<T> build() {
        int finalPriority = (priorityOverride >= 0) ? priorityOverride : cargo.getPriority();
        return new Shipment<>(cargo, destination, finalPriority, fragile, insured);
    }
}