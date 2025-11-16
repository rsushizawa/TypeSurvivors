package Data;

/**
 * Simple parameter holder used by upgrades.
 */
public class Parameter {
    public final String name;
    public double currentValue;
    public final double increasePerLevel;
    public final String unit;
    public boolean justSkipped = false;

    public Parameter(String name, double baseValue, double increasePerLevel, String unit) {
        this.name = name;
        this.currentValue = baseValue;
        this.increasePerLevel = increasePerLevel;
        this.unit = unit == null ? "" : unit;
    }

    public void increase() {
        this.currentValue += this.increasePerLevel;
    }

    public String getDisplayValue() {
        if (this.currentValue == (long) this.currentValue) {
            return String.format("%d%s", (long) this.currentValue, this.unit);
        } else {
            return String.format("%.1f%s", this.currentValue, this.unit);
        }
    }
}
