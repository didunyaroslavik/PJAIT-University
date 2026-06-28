package the_magic_backpack;

import java.util.Iterator;
import java.util.NoSuchElementException;

// Allows iteration over backpack items while skipping worthless ones
public class BackpackIterator implements Iterator<Item> {

    // Immutable field storing a reference to the array
    private final Item[] items;
    // Immutable field storing the number of occupied elements
    private final int itemCount;
    // Current iteration position
    private int currentIndex;

    // Initializes iterator and sets starting position
    public BackpackIterator(Item[] items, int itemCount) {
        this.items = items;
        this.itemCount = itemCount;
        this.currentIndex = 0;
        this.skipWorthless();
    }

    // Moves iteration index to the nearest valuable item
    private void skipWorthless() {
        // Loops while within bounds and element is worthless
        while (currentIndex < itemCount && items[currentIndex].isWorthless()) {
            currentIndex++; // Increments index to skip worthless items
        }
    }

    // Checks whether there is another valuable item
    @Override
    public boolean hasNext() {
        return currentIndex < itemCount;
    }

    // Returns the next valuable item
    @Override
    public Item next() {
        if (!hasNext()) { // Checks if another element is available
            // Throws exception if no elements are left
            throw new NoSuchElementException("No more valuable items.");
        }

        // Stores item at current index
        Item item = items[currentIndex];
        currentIndex++; // Increments index by one

        // Prepares iterator for next call by skipping worthless elements
        skipWorthless();

        return item; // Returns retrieved item
    }
}