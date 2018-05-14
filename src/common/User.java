package common;

import java.util.ArrayList;
import java.util.HashMap;

public class User extends Model {

    private String password;
    private String address;
    private Postcode postcode;
    public HashMap<Dish, Number> basket = new HashMap<>();
    public ArrayList<Order> orders = new ArrayList<>();

    public User(String username, String password, String address, Postcode postcode) {
        setName(username);
        this.password = password;
        this.address = address;
        this.postcode = postcode;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Postcode getPostcode() {
        return postcode;
    }

    public void setPostcode(Postcode postcode) {
        Postcode oldPostcode = this.postcode;
        this.postcode = postcode;
        notifyUpdate("postcode", oldPostcode, this.postcode);
    }

    public HashMap<Dish, Number> getBasket() {
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

    public ArrayList<Order> getOrders() {
        return orders;
    }

    public void setOrders(ArrayList<Order> orders) {
        this.orders = orders;
        notifyUpdate();
    }

    @Override
    public String getName() {
        return name;
    }
}
