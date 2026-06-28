package the_magic_backpack;

public class Main {

    public static void main(String[] args) {

        System.out.println("\tTHE ADVENTURER'S MAGIC BACKPACK");

        MagicBackpack backpack = new MagicBackpack(
                "Geralt of Rivia", 30.0
        );

        Potion healthPotion = new Potion(
                "Health Potion", 50, 0.3, "Restores health", 75
        );
        Potion manaPotion = new Potion(
                "Mana Potion", 45, 0.3, "Restores mana", 50
        );
        Potion poisonPotion = new Potion(
                "Poison Vial", 80, 0.2, "Deals damage", 100
        );

        Weapon silverSword = new Weapon(
                "Silver Sword", 500, 3.5, 45, 3
        );
        Weapon crossbow = new Weapon(
                "Crossbow", 200, 4.0, 30, 5
        );

        Treasure ruby = new Treasure(
                "Ruby", 300, 0.1, "Rare"
        );
        Treasure ancientCoin = new Treasure(
                "Ancient Coin", 150, 0.05, "Epic"
        );
        Treasure diamond = new Treasure(
                "Diamond", 1000, 0.05, "Legendary"
        );

        Junk brokenBottle = new Junk(
                "Broken Bottle", 0.2, "Shards of glass"
        );
        Junk oldBoot = new Junk(
                "Old Boot", 0.8, "Smells terrible"
        );
        Junk wetRag = new Junk(
                "Wet Rag", 0.5, "Completely worthless"
        );

        System.out.println("\n Packing the backpack...\n");

        backpack.add(healthPotion);
        backpack.add(brokenBottle);
        backpack.add(silverSword);
        backpack.add(oldBoot);
        backpack.add(ruby);
        backpack.add(manaPotion);
        backpack.add(wetRag);
        backpack.add(crossbow);
        backpack.add(ancientCoin);
        backpack.add(poisonPotion);
        backpack.add(diamond);

        backpack.displayContents();

        backpack.displayAllIncludingJunk();

        // Natural sorting
        System.out.println("\n Natural sort (most expensive first)...");
        backpack.sort();
        backpack.displayContents();

        // Alphabetical sorting via Comparator
        System.out.println("\n Alphabetical sort (Comparator)...");
        backpack.sort(new ItemNameComparator());
        backpack.displayContents();

        // Weight sorting via Comparator
        System.out.println("\n Weight sort (Comparator)...");
        backpack.sort(new ItemWeightComparator());
        backpack.displayContents();

        System.out.println("\n Using items...\n");

        System.out.println(healthPotion.use());
        System.out.println(healthPotion.use());
        System.out.println();

        System.out.println(silverSword.use());
        System.out.println(silverSword.use());
        System.out.println(silverSword.use());
        System.out.println(silverSword.use());
        System.out.println();

        System.out.println(poisonPotion.use());

        System.out.println("\n Searching for 'Ruby'...");
        Item found = backpack.findByName("Ruby");
        if (found != null) {
            System.out.println("   Found: " + found);
        } else {
            System.out.println("   Not found!");
        }

        System.out.println("\n Items that can still be used:");
        for (Item item : backpack.findUsableItems()) {
            System.out.println("   ->" + item.getName());
        }

        System.out.println("\n Hero browses the backpack (enhanced for):");
        System.out.println("   (junk is automatically hidden)\n");
        for (Item item : backpack) {
            System.out.println(
                    "   I see: " + item.getName()
                            + " (value: " + item.getValue() + " gold)"
            );
        }

        System.out.println("\n End of adventure!");
    }
}