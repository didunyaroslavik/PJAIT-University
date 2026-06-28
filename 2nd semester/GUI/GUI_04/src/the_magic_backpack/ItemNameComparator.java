package the_magic_backpack;

import java.util.Comparator;

// Defines how items are compared based on names
public class ItemNameComparator implements Comparator<Item> {

    // Determines the order of two Item objects
    @Override
    public int compare(Item a, Item b) {
        // Retrieves names and compares them ignoring case
        return a.getName().compareToIgnoreCase(b.getName());
    }
}