package the_magic_backpack;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;

// Implements Iterable for the generic type Item
public class MagicBackpack implements Iterable<Item> {

    private final String ownerName;
    private Item[] items;
    private int itemCount;
    private final double maxWeight;

    public MagicBackpack(String ownerName, double maxWeight) {
        this.ownerName = ownerName;
        this.items = new Item[10];
        this.itemCount = 0;
        this.maxWeight = maxWeight;
    }

    public boolean add(Item item) {
        if (getCurrentWeight() + item.getWeight() > maxWeight) {
            System.out.printf(
                    " Backpack is too heavy! Could not " +
                            "add: %s (%.1f kg)%n", item.getName(), item.getWeight()
            );
            return false;
        }

        if (itemCount == items.length) {
            items = Arrays.copyOf(items, items.length * 2);
        }

        items[itemCount++] = item;
        System.out.printf(
                "Added to backpack: %s%n", item.getName()
        );
        return true;
    }

    public boolean remove(Item item) {
        for (int i = 0; i < itemCount; i++) {
            if (items[i].equals(item)) {
                for (int j = i; j < itemCount - 1; j++) {
                    items[j] = items[j + 1];
                }
                items[--itemCount] = null;
                System.out.printf(
                        "Removed from backpack: %s%n", item.getName()
                );
                return true;
            }
        }
        return false;
    }

    public double getCurrentWeight() {
        double total = 0;
        for (int i = 0; i < itemCount; i++) {
            total += items[i].getWeight();
        }
        return total;
    }

    public int getTotalValue() {
        int total = 0;
        for (int i = 0; i < itemCount; i++) {
            total += items[i].getValue();
        }
        return total;
    }

    public int getTotalItemCount() {
        return itemCount;
    }

    public int getValuableItemCount() {
        int count = 0;
        for (int i = 0; i < itemCount; i++) {
            if (!items[i].isWorthless()) {
                count++;
            }
        }
        return count;
    }

    public int getJunkCount() {
        return getTotalItemCount() - getValuableItemCount();
    }

    public void sort() {
        Arrays.sort(items, 0, itemCount);
    }

    public void sort(Comparator<Item> comparator) {
        Arrays.sort(items, 0, itemCount, comparator);
    }

    public Item findByName(String name) {
        for (int i = 0; i < itemCount; i++) {
            if (items[i].getName().equalsIgnoreCase(name)) {
                return items[i];
            }
        }
        return null;
    }

    public Item[] findUsableItems() {
        int count = 0;
        for (int i = 0; i < itemCount; i++) {
            if (items[i] instanceof Usable) {
                Usable u = (Usable) items[i];
                if (!u.isConsumed()) {
                    count++;
                }
            }
        }

        Item[] usable = new Item[count];
        int index = 0;
        for (int i = 0; i < itemCount; i++) {
            if (items[i] instanceof Usable) {
                Usable u = (Usable) items[i];
                if (!u.isConsumed()) {
                    usable[index++] = items[i];
                }
            }
        }
        return usable;
    }

    // Provides an iterator traversal of backpack items
    @Override
    public Iterator<Item> iterator() {
        // Creates and returns a new instance of BackpackIterator
        return new BackpackIterator(items, itemCount);
    }

    public Item[] allItems() {
        return Arrays.copyOf(items, itemCount);
    }

    public void displayContents() {
        System.out.println();
        System.out.println("=".repeat(65));
        System.out.printf(" Backpack of: %s%n", ownerName);
        System.out.printf(
                "   Weight: %.1f / %.1f kg | Items: %d " +
                        "(valuable: %d, junk: %d)%n",
                getCurrentWeight(), maxWeight,
                getTotalItemCount(), getValuableItemCount(), getJunkCount()
        );
        System.out.printf("   Total value: %d gold%n", getTotalValue());
        System.out.println("-".repeat(65));

        if (itemCount == 0) {
            System.out.println("   (backpack is empty)");
        } else {
            System.out.println("     Hero sees (magic filter):");
            int idx = 1;
            for (Item item : this) {
                System.out.printf("   %2d. %s%n", idx++, item);
            }

            if (getJunkCount() > 0) {
                System.out.printf(
                        "%n    The backpack hides %d junk items...%n",
                        getJunkCount()
                );
            }
        }
        System.out.println("=".repeat(65));
    }

    public void displayAllIncludingJunk() {
        System.out.println("\n Full backpack contents (debug):");
        System.out.println("-".repeat(65));
        int idx = 1;
        for (Item item : allItems()) {
            String marker = item.isWorthless() ? " " : "";
            System.out.printf("   %2d. %s%s%n", idx++, item, marker);
        }
        System.out.println("-".repeat(65));
    }
}