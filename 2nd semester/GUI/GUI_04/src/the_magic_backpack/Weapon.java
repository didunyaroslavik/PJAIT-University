package the_magic_backpack;

public class Weapon extends Item implements Usable {

    private final int damage;
    private int durability;

    public Weapon(String name, int value, double weight,
                  int damage, int durability) {
        super(name, value, weight);
        this.damage = damage;
        this.durability = durability;
    }

    @Override
    public String use() {
        if (durability <= 0)
            return getName() + " — the weapon is destroyed!";

        durability--;
        String msg = String.format(" You attack with %s! Damage: %d",
                getName(), damage);
        if (durability == 0)
            msg += "  The weapon broke!";
        else
            msg += String.format(" (durability: %d)", durability);

        return msg;
    }

    @Override
    public boolean isConsumed() {
        return durability <= 0;
    }

    @Override
    public String toString() {
        String status = durability <= 0 ? " [DESTROYED]" :
                String.format(" [dmg:%d, dur:%d]", damage, durability);

        return super.toString() + status;
    }
}