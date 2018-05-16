package common;

import java.util.LinkedHashMap;

public class User extends Model {

    private String password;
    private String address;
    private Postcode postcode;
    private LinkedHashMap<Dish, Number> basket = new LinkedHashMap<>();

    public User(String username, String password, String address, Postcode postcode) {
        setName(username);
        this.password = password;
        this.address = address;
        this.postcode = postcode;
    }

    public String getPassword() {
        return password;
    }

    public String getAddress() {
        return address;
    }

    public Postcode getPostcode() {
        return postcode;
    }

    public void setPostcode(Postcode postcode) {
        Postcode oldPostcode = this.postcode;
        this.postcode = postcode;
        notifyUpdate("postcode", oldPostcode, this.postcode);
    }

    public LinkedHashMap<Dish, Number> getBasket() {
        return basket;
    }

    public void addToBasket(Dish dish, Number quantity) {
        basket.put(dish, quantity);
        notifyUpdate();
    }

    public void clearBasket() {
        basket.clear();
        notifyUpdate();
    }

    @Override
    public String getName() {
        return name;
    }
}
