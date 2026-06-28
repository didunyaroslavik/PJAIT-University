package logistics_system;

import logistics_system.model.*;
import logistics_system.container.*;
import logistics_system.processing.*;
import logistics_system.shipping.*;
import logistics_system.sorting.*;
import logistics_system.transfer.*;
import logistics_system.registry.*;

public class Main {

    // Inner class for Task 26
    public static class TemperatureController {
        private final double tempCelsius;

        public TemperatureController(double temp) {
            this.tempCelsius = temp;
        }

        @Override
        public String toString() {
            return "TempController{" + tempCelsius + "C}";
        }
    }

    // Inner class for Task 27
    public static class SecurityProtocol {
        private final String level;

        public SecurityProtocol(String level) {
            this.level = level;
        }

        @Override
        public String toString() {
            return "SecurityProtocol{level=" + level + "}";
        }
    }

    public static void main(String[] args) {

        System.out.println("=== CARGO — Logistics System ===");

        Electronics laptop = new Electronics("Laptop", 2.5, 3, 220);
        Electronics phone = new Electronics("Phone", 0.2, 1, 5);
        Food apples = new Food("Apples", 5.0, 2, 14);
        Food milk = new Food("Milk", 1.0, 5, 3);
        HazardousMaterial acid = new HazardousMaterial(
                "Sulfuric Acid", 10.0, 5, "Class 8");

        System.out.println("\n=== 1. BOX<T> ===");

        Box<Electronics> electronicsBox = new Box<>(laptop);
        System.out.println(electronicsBox);

        Box<Food> foodBox = new Box<>();
        foodBox.put(apples);
        System.out.println(foodBox);

        Food retrieved = foodBox.getAndClear();
        System.out.println("Retrieved: " + retrieved);
        System.out.println("Box empty? " + foodBox.isEmpty());


        System.out.println("\n=== 2. PAIR<K, V> ===");

        Pair<String, Electronics> labeledLaptop =
                new Pair<>("PKG-001", laptop);
        System.out.println(labeledLaptop);

        Pair<Electronics, String> swapped = labeledLaptop.swap();
        System.out.println("Swapped: " + swapped);


        System.out.println("\n=== 3. GENERIC METHODS ===");

        InspectionUtils.logInspection(laptop);
        InspectionUtils.logInspection(apples);
        InspectionUtils.logInspection("Random string on the belt");

        Pair<String, Food> labeledApples =
                InspectionUtils.label("FOOD-042", apples);
        System.out.println(labeledApples);

        Box<Electronics> sourceBox = new Box<>(phone);
        Box<Electronics> destBox = new Box<>();
        InspectionUtils.transferBetweenBoxes(sourceBox, destBox);
        System.out.println("Source: " + sourceBox);
        System.out.println("Dest:   " + destBox);


        System.out.println("\n=== 4. TRANSFORMER<T, R> ===");

        String laptopLabel =
                PackagingTransformer.ELECTRONICS_TO_LABEL.transform(laptop);
        System.out.println("Label: " + laptopLabel);

        Double applesInGrams =
                PackagingTransformer.FOOD_WEIGHT_TO_GRAMS.transform(apples);
        System.out.println("Apples weight: " + applesInGrams + "g");

        Transformer<HazardousMaterial, String> hazardToString =
                PackagingTransformer.toStringTransformer();
        System.out.println("Hazard: " + hazardToString.transform(acid));

        Transformer<Electronics, Boolean> isHighVoltage =
                e -> e.getVoltage() > 100;
        System.out.println("Laptop high voltage? " +
                isHighVoltage.transform(laptop));


        System.out.println("\n=== 5. WRAPPER<T> + map() ===");

        Wrapper<Electronics> secureLaptop =
                new Wrapper<>(laptop, "SEAL-A1");
        System.out.println(secureLaptop);

        Wrapper<String> secureLabel =
                secureLaptop.map(PackagingTransformer.ELECTRONICS_TO_LABEL);
        System.out.println("Transformed (still sealed): " + secureLabel);

        Wrapper<Boolean> voltageCheck = secureLaptop
                .map(Electronics::getVoltage)     // Wrapper<Integer>
                .map(v -> v > 100);               // Wrapper<Boolean>
        System.out.println("Voltage check result: " + voltageCheck);

        Boolean result = voltageCheck.unwrap();
        System.out.println("Unwrapped value: " + result);


        System.out.println("\n=== 6. BUILDER<T> ===");

        Shipment<Electronics> laptopShipment =
                new ShipmentBuilder<>(laptop)
                        .to("Warsaw, Poland")
                        .withPriority(1)
                        .fragile()
                        .insured()
                        .build();
        System.out.println(laptopShipment);

        Shipment<Food> appleShipment =
                new ShipmentBuilder<>(apples)
                        .to("Berlin, Germany")
                        .build();
        System.out.println(appleShipment);


        System.out.println("\n=== 7. FIELD COMPARATOR<T> ===");

        Product[] products = { laptop, apples, acid, phone, milk };

        SortingLine.printLine("Before sorting", products);

        SortingLine.sort(
                products, FieldComparator.ascending(Product::getWeight)
        );
        SortingLine.printLine("Sorted by WEIGHT (asc)", products);

        SortingLine.sort(
                products, FieldComparator.descending(Product::getPriority)
        );
        SortingLine.printLine("Sorted by PRIORITY (desc)", products);

        SortingLine.sort(
                products, FieldComparator.ascending(Product::getName)
        );
        SortingLine.printLine("Sorted by NAME (asc)", products);


        System.out.println("\n=== 8. PECS (Producer Extends, Consumer Super) ===");

        Box<Electronics> electronicsSource = new Box<>(laptop);
        Box<Product> generalDestination = new Box<>();

        DockTransfer.transfer(electronicsSource, generalDestination);
        System.out.println(
                "General box now contains: " + generalDestination
        );


        System.out.println("\n=== 9. TYPESAFE HETEROGENEOUS CONTAINER ===");

        WarehouseRegistry registry = new WarehouseRegistry();

        registry.put(
                TemperatureController.class,
                new TemperatureController(-18.5)
        );
        registry.put(
                SecurityProtocol.class,
                new SecurityProtocol("LEVEL-5")
        );

        TemperatureController temp = registry.get(TemperatureController.class);
        SecurityProtocol security = registry.get(SecurityProtocol.class);

        System.out.println("Temperature module: " + temp);
        System.out.println("Security module:    " + security);
        System.out.println("Registry size:      " + registry.size());

    }
}