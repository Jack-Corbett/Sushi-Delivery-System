package common;

import java.util.LinkedHashMap;

/**
 * A user of the sushi delivery system who can place orders.
 * @author Jack Corbett
 */
public class User extends Model {

    private String password;
    private String address;
    private Postcode postcode;
    private LinkedHashMap<Dish, Number> basket = new LinkedHashMap<>();

    /**
     * Create a new user
     * @param username Username used to login
     * @param password Password used to login
     * @param address Address text to tell the drone the street address
     * @param postcode Postcode to tell us the distance they are away from the sushi restaurant
     */
    public User(String username, String password, String address, Postcode postcode) {
        setName(username);
        this.password = password;
        this.address = address;
        this.postcode = postcode;
    }

    /**
     * @return The users name
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * @return The users password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @return The users address
     */
    public String getAddress() {
        return address;
    }

    /**
     * @return The users postcode
     */
    public Postcode getPostcode() {
        return postcode;
    }

    /**
     * @param postcode New postcode of the user
     */
    public void setPostcode(Postcode postcode) {
        Postcode oldPostcode = this.postcode;
        this.postcode = postcode;
        notifyUpdate("postcode", oldPostcode, this.postcode);
    }

    // BASKET SPECIFIC
    /**
     * @return The dishes along with their quantities currently in the users basket
     */
    public LinkedHashMap<Dish, Number> getBasket() {
        return basket;
    }

    /**
     * @param dish Dish to be added to the basket
     * @param quantity The number of that dish to add
     */
    public void addToBasket(Dish dish, Number quantity) {
        basket.put(dish, quantity);
        notifyUpdate();
    }

    /**
     * Removes all items from the users basket
     */
    public void clearBasket() {
        basket.clear();
        notifyUpdate();
    }
}
