package the_magic_backpack;

import java.util.Comparator;

// Defines how items are compared based on weight and value
public class ItemWeightComparator implements Comparator<Item> {

    // Determines the order of two Item objects
    @Override
    public int compare(Item a, Item b) {
        // Compares weights for ascending order
        int result = Double.compare(a.getWeight(), b.getWeight());

        // Checks if weights are equal
        if (result == 0) {
            // Performs additional descending order comparison on values
            result = Integer.compare(b.getValue(), a.getValue());
        }

        return result;
    }
}