package the_magic_backpack;

// Item implements Comparable to define natural ordering
public abstract class Item implements Comparable<Item> {

    private final String name;
    private final int value;
    private final double weight;

    public Item(String name, int value, double weight) {
        this.name = name;
        this.value = value;
        this.weight = weight;
    }

    public String getName() {
        return name;
    }

    public int getValue() {
        return value;
    }

    public double getWeight() {
        return weight;
    }

    // Natural ordering: descending by value, then alphabetically by name
    @Override
    public int compareTo(Item other) {
        // Compare values descending using Integer.compare
        int result = Integer.compare(other.value, this.value);

        // If values are equal, perform additional comparison on names
        if (result == 0) {
            // Compare names ignoring case
            result = this.name.compareToIgnoreCase(other.name);
        }
        return result;
    }

    public boolean isWorthless() {
        return value == 0;
    }

    @Override
    public String toString() {
        return String.format("%-20s | value: %4d gold | weight: %.1f kg",
                name, value, weight);
    }
}