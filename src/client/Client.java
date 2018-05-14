package client;

import common.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Client implements ClientInterface {

    private CommsClient comms;
    private ArrayList<Postcode> postcodes;

    public Client() {
        comms = new CommsClient();
        postcodes = new ArrayList<>();
        // This removes the initial acknowledgment method from the queue
        comms.receiveMessage();
    }

    @Override
    public User register(String username, String password, String address, Postcode postcode) {
        comms.sendMessage("USER Register:" + username + ":" + password + ":" + address + ":" + postcode.getName());
        User user;

        if (comms.receiveMessage().equals("SUCCESS Register")) {
            user = new User(username, password, address, postcode);
            return user;
        } else {
            System.err.println("Registration failed");
        }
        return null;
    }

    @Override
    public User login(String username, String password) {
        comms.sendMessage("USER Login:" + username + ":" + password);
        User user;
        String[] response = comms.receiveMessage().split(":");

        if (response[0].equals("SUCCESS Login")) {
            user = new User(username, password, response[1], new Postcode(response[2], Integer.parseInt(response[3])));
            return user;
        } else {
            System.err.println("Login failed");
        }
        return null;
    }

    @Override
    public List<Postcode> getPostcodes() {
        comms.sendMessage("POSTCODE Fetch");
        String[] response = comms.receiveMessage().split(":");
        String[] details;

        switch (response[0]) {
            case "POSTCODE No change":
                return postcodes;

            case "POSTCODE All":
                postcodes.clear();
                for (int i = 1; i < response.length; i++) {
                    details = response[i].split(",");
                    postcodes.add(new Postcode(details[0], Integer.parseInt(details[1])));
                }
                return postcodes;

            default:
                System.err.println("Postcode fetch failed");
                return new ArrayList<>();
        }
    }

    @Override
    public List<Dish> getDishes() {
        comms.sendMessage("DISH Fetch");
        String[] response = comms.receiveMessage().split(":");
        String[] details;
        List<Dish> dishes = new ArrayList<>();

        if (response[0].equals("DISH All")) {
            for (int i = 1; i < response.length; i++) {
                details = response[i].split(",");
                dishes.add(new Dish(details[0], details[1], Double.parseDouble(details[2]),
                        Integer.parseInt(details[3]), Integer.parseInt(details[4])));
            }
            return dishes;
        } else {
            System.err.println("Dish fetch failed");
        }
        return null;
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
            totalCost += (dish.getPrice().doubleValue() * basket.get(dish).intValue());
        }
        return totalCost;
    }

    @Override
    public void addDishToBasket(User user, Dish dish, Number quantity) {
        comms.sendMessage("USER Add to basket:" + user.getName() + ":" + dish.getName() + ":" + quantity);

        if (comms.receiveMessage().equals("SUCCESS Added")) {
            user.getBasket().put(dish, quantity);
        } else {
            System.err.println("Adding dish to basket failed");
        }
    }

    @Override
    public void updateDishInBasket(User user, Dish dish, Number quantity) {
        comms.sendMessage("USER Update basket:" + user.getName() + ":" + dish.getName() + ":" + quantity);

        if (comms.receiveMessage().equals("SUCCESS Updated")) {
            user.getBasket().put(dish, quantity);
        } else {
            System.err.println("Updating dish in basket failed");
        }
    }

    @Override
    public Order checkoutBasket(User user) {
        comms.sendMessage("USER Checkout:" + user.getName());

        if (comms.receiveMessage().equals("SUCCESS Order created")) {
            Order order = new Order(user, user.getBasket());
            user.orders.add(order);
            user.clearBasket();
            return order;
        } else {
            System.err.println("Checking out basket failed");
        }
        return null;
    }

    @Override
    public void clearBasket(User user) {
        comms.sendMessage("USER Clear basket:" + user.getName());

        if (comms.receiveMessage().equals("SUCCESS Basket cleared")) {
            user.clearBasket();
        } else {
            System.err.println("Clearing basket failed");
        }
    }

    // USER Orders:Name.Desc.Price.RestockThreshold.RestockAmount * 5,Name.Desc.Price.RestockThreshold.RestockAmount * 5:Next order...
    @Override
    public List<Order> getOrders(User user) {
        if (user != null) {
            comms.sendMessage("USER Get orders");
            String[] response = comms.receiveMessage().split(":");
            String[] stringOrders;
            String[] orderElements;
            String[] dishDetails;
            ArrayList<Order> orders = new ArrayList<>();

            if (response[0].equals("USER Orders")) {
                for (int i = 1; i < response.length; i++) {
                    stringOrders = response[i].split(",");

                    // Create the hash map
                    HashMap<Dish, Number> orderItems = new HashMap<>();

                    for (String stringOrder : stringOrders) {
                        orderElements = stringOrder.split(" * ");
                        dishDetails = orderElements[0].split(".");
                        orderItems.put(new Dish(dishDetails[0], dishDetails[1], Double.parseDouble(dishDetails[2]),
                                        Integer.parseInt(dishDetails[3]), Integer.parseInt(dishDetails[4])),
                                Integer.parseInt(orderElements[1]));
                    }
                    orders.add(new Order(user, orderItems));
                }
                user.setOrders(orders);
                return user.getOrders();
            } else {
                System.err.println("Fetching orders failed");
            }
        }
        return new ArrayList<>();
    }

    @Override
    public boolean isOrderComplete(Order order) {
        comms.sendMessage("ORDER Is complete:" + order.getName());
        String response = comms.receiveMessage();
        switch (response) {
            case "SUCCESS Complete":
                order.setComplete();
                return true;
            case "SUCCESS Incomplete":
                return false;
            default:
                System.err.println("Checking order completion failed. Default false returned");
                break;
        }
        return false;
    }

    @Override
    public String getOrderStatus(Order order) {
        comms.sendMessage("ORDER Get status:" + order.getName());
        String[] response = comms.receiveMessage().split(":");

        if (response[0].equals("SUCCESS Status")) {
            order.setStatus(response[1]);
            return order.getStatus();
        } else {
            System.err.println("Getting order status failed.");
        }
        return null;
    }

    @Override
    public Number getOrderCost(Order order) {
        return order.getCost();
    }

    @Override
    public void cancelOrder(Order order) {
        comms.sendMessage("ORDER Cancel:" + order.getName());

        if (comms.receiveMessage().equals("SUCCESS Cancelled")) {
            order.setCancelled();
        } else {
            System.err.println("Cancelling order failed");
        }
    }

    @Override
    public void addUpdateListener(UpdateListener listener) {

    }

    @Override
    public void notifyUpdate() {

    }
}
