package client;

import common.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Client implements ClientInterface {

    private HashMap<String, User> users;
    private ArrayList<Postcode> postcodes;

    // This needs to be moved to a storage class in future
    private IngredientStock ingredientStock;
    private DishStock dishStock;

    public Client() {
        postcodes = new ArrayList<>();
        users = new HashMap<>();
        populatePostcodes();

        // MOVE to separate class so server can interact
        ingredientStock = new IngredientStock();
        dishStock = new DishStock(ingredientStock);
    }

    @Override
    public User register(String username, String password, String address, Postcode postcode) {
        User user = new User(username, password, address, postcode);
        users.put(username, user);
        return user;
    }

    @Override
    public User login(String username, String password) {
        User user = users.get(username);
        if (user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }

    @Override
    public List<Postcode> getPostcodes() {
        return postcodes;
    }

    @Override
    public List<Dish> getDishes() {
        return dishStock.getStock();
    }

    @Override
    public String getDishDescription(Dish dish) {
        return dish.getDescription();
    }

    @Override
    public Number getDishPrice(Dish dish) {
        return dish.getPrice();
    }

    @Override
    public Map<Dish, Number> getBasket(User user) {
        return user.getBasket();
    }

    @Override
    public Number getBasketCost(User user) {
        Double totalCost = 0.0;
        HashMap<Dish, Number> basket = user.getBasket();
        for (Dish dish : basket.keySet()) {
            totalCost += (dish.getPrice() * basket.get(dish).intValue());
        }
        return totalCost;
    }

    @Override
    public void addDishToBasket(User user, Dish dish, Number quantity) {
        user.getBasket().put(dish, quantity);
    }

    @Override
    public void updateDishInBasket(User user, Dish dish, Number quantity) {
        user.getBasket().put(dish, quantity);
    }

    @Override
    public Order checkoutBasket(User user) {
        return null;
    }

    @Override
    public void clearBasket(User user) {
        user.getBasket().clear();
    }

    @Override
    public List<Order> getOrders(User user) {
        if (user == null) {
            return new ArrayList<>();
        }
        return user.getOrders();
    }

    @Override
    public boolean isOrderComplete(Order order) {
        return order.getStatus();
    }

    @Override
    public String getOrderStatus(Order order) {
        if (order.getStatus()) {
            return "Order " + order.getName() + " is complete.";
        }
        return "Order " + order.getName() + " is not complete.";
    }

    @Override
    public Number getOrderCost(Order order) {
        return null;
    }

    @Override
    public void cancelOrder(Order order) {

    }

    @Override
    public void addUpdateListener(UpdateListener listener) {

    }

    @Override
    public void notifyUpdate() {

    }

    private void populatePostcodes() {
        postcodes.add(new Postcode("SO16 0TA", 10));
        postcodes.add(new Postcode("SO15 1NZ", 15));
        postcodes.add(new Postcode("SO16 3NP", 20));
        postcodes.add(new Postcode("SO14 5GZ", 8));
        postcodes.add(new Postcode("SO16 7KC", 12));
        postcodes.add(new Postcode("SO16 0BH", 20));
        postcodes.add(new Postcode("SO16 2CD", 5));
    }
}
