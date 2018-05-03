package common;

import java.util.ArrayList;
import java.util.HashMap;

public class User extends Model {

    private String username;
    private String password;
    private String address;
    private Postcode postcode;
    public HashMap<Dish, Number> basket = new HashMap<>();
    public ArrayList<Order> orders = new ArrayList<>();

    public User(String username, String password, String address, Postcode postcode) {
        this.username = username;
        this.password = password;
        this.address = address;
        this.postcode = postcode;
        setName(username);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
        setName("User:" + username);
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
        this.postcode = postcode;
    }

    public HashMap<Dish, Number> getBasket() {
        return basket;
    }

    public void setBasket(HashMap<Dish, Number> basket) {
        this.basket = basket;
    }

    public ArrayList<Order> getOrders() {
        return orders;
    }

    @Override
    public String getName() {
        return name;
    }
}
