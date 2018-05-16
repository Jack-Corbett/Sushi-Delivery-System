package common;

/**
 * A supplier that sells ingredients that can be collected by the drones.
 * @author Jack Corbett
 */
public class Supplier extends Model {

    private Integer distance;

    /**
     * Create a new supplier, setting it's properties.
     * @param name Name of the supplier
     * @param distance Distance from the sushi restaurant
     */
    public Supplier(String name, Integer distance) {
        setName(name);
        this.distance = distance;
    }

    /**
     * @return Name of the supplier
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * @return Distance from the sushi restaurant
     */
    public Integer getDistance() {
        return distance;
    }

    /**
     * @param name Sets the name of the supplier
     */
    @Override
    public void setName(String name) {
        super.setName(name);
    }
}
