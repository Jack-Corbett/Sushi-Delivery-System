package common;

public class Ingredient extends Model {

    private String unit;
    private Supplier supplier;
    private Integer restockAmount;
    private Integer restockThreshold;
    boolean restocking;

    public Ingredient(String name, String unit, Supplier supplier, Integer restockThreshold, Integer restockAmount) {
        super.setName(name);
        this.unit = unit;
        this.supplier = supplier;
        this.restockThreshold = restockThreshold;
        this.restockAmount = restockAmount;
        restocking = false;
    }

    @Override
    public String getName() {
        return name;
    }

    public String getUnit() {
        return unit;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public Integer getRestockThreshold() {
        return restockThreshold;
    }

    public Integer getRestockAmount() {
        return restockAmount;
    }

    @Override
    public void setName(String name) {
        super.setName(name);
    }

    public void setunit(String unit) {
        notifyUpdate("unit", this.unit, unit);
        this.unit = unit;
    }

    public void setSupplier(Supplier supplier) {
        notifyUpdate("supplier", this.supplier, supplier);
        this.supplier = supplier;
    }

    public void setRestockAmount(Integer restockAmount) {
        notifyUpdate("restockAmount", this.restockAmount, restockAmount);
        this.restockAmount = restockAmount;
    }

    public void setRestockThreshold(Integer restockThreshold) {
        notifyUpdate("restockThreshold", this.restockThreshold, restockThreshold);
        this.restockThreshold = restockThreshold;
    }
}
