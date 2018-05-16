package common;

/**
 * Represents a postcode which can be chosen by the user.
 * @author Jack Corbett
 */
public class Postcode extends Model {

    private String code;
    private Integer distance;

    /**
     * Sets the postcode information
     * @param postcode The code (eg SO15 9FX)
     * @param distance Distance from the sushi restaurant
     */
    public Postcode(String postcode, Integer distance) {
        code = postcode;
        this.distance = distance;
        setName(code);
    }

    /**
     * @return The postcode
     */
    public String getCode() {
        return code;
    }

    /**
     * @return Distance to the sushi restaurant
     */
    public Integer getDistance() {
        return distance;
    }

    /**
     * @return The name (this is the same as the code)
     */
    @Override
    public String getName() {
        return name;
    }
}
