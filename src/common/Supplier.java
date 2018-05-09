package common;

public class Supplier extends Model {

    private Integer distance;
    //private Ingredient[] ingredients; - Maybe not needed

    public Supplier(String name, Integer distance) {
        setName(name);
        this.distance = distance;
    }

    @Override
    public String getName() {
        return name;
    }

    public Integer getDistance() {
        return distance;
    }

    @Override
    public void setName(String name) {
        super.setName(name);
    }

    public void setDistance(Integer distance) {
        notifyUpdate("distance", this.distance, distance);
        this.distance = distance;
    }
}
