package common;

/**
 * Represents an ingredient that can be combined to produce dishes. They are restocked by drones.
 * @author Jack Corbett
 */
public class Ingredient extends Model {

    private String unit;
    private Supplier supplier;
    private Integer restockAmount;
    private Integer restockThreshold;
    int noRestocking;

    /**
     * Set the ingredient properties.
     * @param name Name
     * @param unit The measurement unit of the ingredient (eg: Grams)
     * @param supplier Where the ingredient can be restocked from
     * @param restockThreshold Restocking is triggered when the stock level is below this
     * @param restockAmount Sets the amount stock will be replenished to above the threshold
     */
    public Ingredient(String name, String unit, Supplier supplier, Integer restockThreshold, Integer restockAmount) {
        super.setName(name);
        this.unit = unit;
        this.supplier = supplier;
        this.restockThreshold = restockThreshold;
        this.restockAmount = restockAmount;
        noRestocking = 0;
    }

    /**
     * @return Name of the ingredient
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * @return The supplier that the ingredient can be restocked from
     */
    Supplier getSupplier() {
        return supplier;
    }

    /**
     * @return Restock threshold that restocking is triggered at
     */
    public Integer getRestockThreshold() {
        return restockThreshold;
    }

    /**
     * @return Restock amount that determines the stock level restocked to above the threshold
     */
    public Integer getRestockAmount() {
        return restockAmount;
    }

    /**
     * @param name Name of the ingredient
     */
    @Override
    public void setName(String name) {
        super.setName(name);
    }

    /**
     * @param restockAmount The restock amount
     */
    public void setRestockAmount(Integer restockAmount) {
        notifyUpdate("restockAmount", this.restockAmount, restockAmount);
        this.restockAmount = restockAmount;
    }

    /**
     * @param restockThreshold The restock threshold
     */
    public void setRestockThreshold(Integer restockThreshold) {
        notifyUpdate("restockThreshold", this.restockThreshold, restockThreshold);
        this.restockThreshold = restockThreshold;
    }
}
