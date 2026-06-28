package the_magic_backpack;

public class Potion extends Item implements Usable {

    private final String effect;
    private final int power;
    private boolean consumed;

    public Potion(String name, int value, double weight,
                  String effect, int power) {
        super(name, value, weight);
        this.effect = effect;
        this.power = power;
        this.consumed = false;
    }

    @Override
    public String use() {
        if (consumed) {
            return getName() + " — the bottle is already empty!";
        }
        consumed = true;
        return String.format(" You drink %s! %s (+%d pts)",
                getName(), effect, power);
    }

    @Override
    public boolean isConsumed() {
        return consumed;
    }

    @Override
    public String toString() {
        String status = consumed ? " [EMPTY]" : " [" + effect + "]";
        return super.toString() + status;
    }
}