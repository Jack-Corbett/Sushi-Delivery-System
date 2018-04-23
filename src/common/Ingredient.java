package common;

public class Ingredient extends Model {

    private String measurement;
    private Supplier supplier;
    private Integer restockAmount;
    public Integer amount;

    Ingredient(String name, String measurement, Supplier supplier, Integer startingAmount, Integer restockAmount) {
        super.setName(name);
        this.measurement = measurement;
        this.supplier = supplier;

        if (startingAmount < 0) throw new IllegalArgumentException("Cannot set the initial stock level less than 0");
        else this.amount = startingAmount;

        if (restockAmount < 0) throw new IllegalArgumentException("Cannot set the restock value less than 0");
        else this.restockAmount = restockAmount;
    }

    @Override
    public String getName() {
        return name;
    }

    public String getMeasurement() {
        return measurement;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public Integer getAmount() {
        return amount;
    }

    public Integer getRestockAmount() {
        return restockAmount;
    }

    @Override
    public void setName(String name) {
        super.setName(name);
    }

    public void setMeasurement(String measurement) {
        notifyUpdate("measurement", this.measurement, measurement);
        this.measurement = measurement;
    }

    public void setSupplier(Supplier supplier) {
        notifyUpdate("supplier", this.supplier, supplier);
        this.supplier = supplier;
    }

    public void setRestockAmount(Integer restockAmount) {
        notifyUpdate("restockAmount", this.restockAmount, restockAmount);
        this.restockAmount = restockAmount;
    }
}
