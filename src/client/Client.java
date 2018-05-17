package client;

import common.*;

import java.util.*;

/**
 * Client class responsible for implementing the ClientInterface and using the CommsClient class to send and receive
 * messages from the server.
 * @author Jack Corbett
 */
public class Client implements ClientInterface {

    private CommsClient comms = new CommsClient();
    private ArrayList<Postcode> postcodes = new ArrayList<>();
    private ArrayList<UpdateListener> updateListeners = new ArrayList<>();

    /**
     * Constructor that removes the initial acknowledgment method from the received message queue
     */
    public Client() {
        comms.receiveMessage();
    }

    /**
     * Add a new user to the system
     * @param username username
     * @param password password
     * @param address address
     * @param postcode valid postcode
     * @return The newly registered user, if the registration succeeds.
     */
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

    /**
     * Log the user in by starting a new thread on the server
     * @param username username
     * @param password password
     * @return The logged in user, if the login succeeds.
     */
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

    /**
     * Fetch all the postcodes currently registered on the server.
     * @return A list of all postcodes.
     */
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

    /**
     * Fetch a list of all dishes currently listed in the stock system
     * @return A list of dishes.
     */
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

    /**
     * @param dish Dish to lookup
     * @return The text description of the passed dish.
     */
    @Override
    public String getDishDescription(Dish dish) {
        return dish.getDescription();
    }

    /**
     * @param dish Dish to lookup
     * @return The price of the passed dish.
     */
    @Override
    public Number getDishPrice(Dish dish) {
        return dish.getPrice();
    }

    /**
     * @param user user to lookup
     * @return The collection of items in the users basket.
     */
    @Override
    public Map<Dish, Number> getBasket(User user) {
        return user.getBasket();
    }

    /**
     * @param user user to lookup basket
     * @return The cost of all the items in the users basket summed.
     */
    @Override
    public Number getBasketCost(User user) {
        Double totalCost = 0.0;
        HashMap<Dish, Number> basket = user.getBasket();
        for (Dish dish : basket.keySet()) {
            totalCost += (dish.getPrice().doubleValue() * basket.get(dish).intValue());
        }
        return totalCost;
    }

    /**
     * Adds a dish to the users basket.
     * @param user user of basket
     * @param dish dish to change
     * @param quantity quantity to set
     */
    @Override
    public void addDishToBasket(User user, Dish dish, Number quantity) {
        comms.sendMessage("USER Add to basket:" + dish.getName() + ":" + quantity);

        if (comms.receiveMessage().equals("SUCCESS Added")) {
            user.getBasket().put(dish, quantity);
        } else {
            System.err.println("Adding dish to basket failed");
        }
    }

    /**
     * Updates an item that is already in the users basket.
     * @param user user of basket
     * @param dish dish to change
     * @param quantity quantity to set. 0 should remove.
     */
    @Override
    public void updateDishInBasket(User user, Dish dish, Number quantity) {
        comms.sendMessage("USER Update basket:" + dish.getName() + ":" + quantity);

        if (comms.receiveMessage().equals("SUCCESS Updated")) {
            user.getBasket().put(dish, quantity);
        } else {
            System.err.println("Updating dish in basket failed");
        }
    }

    /**
     * Creates an order.
     * @param user user of basket
     * @return The new order object created with the users current basket.
     */
    @Override
    public Order checkoutBasket(User user) {
        comms.sendMessage("USER Checkout:");

        if (comms.receiveMessage().equals("SUCCESS Order created")) {
            System.out.println(user.getBasket().toString());
            Order order = new Order(user, user.getBasket());
            user.clearBasket();
            return order;
        } else {
            System.err.println("Checking out basket failed");
        }
        return null;
    }

    /**
     * Removes all items from the users basket.
     * @param user user of basket
     */
    @Override
    public void clearBasket(User user) {
        comms.sendMessage("USER Clear basket:" + user.getName());

        if (comms.receiveMessage().equals("SUCCESS Basket cleared")) {
            user.clearBasket();
        } else {
            System.err.println("Clearing basket failed");
        }
    }

    /**
     * Fetch all the orders to be displayed in the UI.
     * @param user user to lookup
     * @return A list of all the orders for the given user.
     */
    @Override
    public List<Order> getOrders(User user) {
        if (user != null) {
            comms.sendMessage("USER Get orders:" + user.getName());
            String[] response = comms.receiveMessage().split(":");
            String[] dishes;
            String[] dishInfo;
            ArrayList<Order> orders = new ArrayList<>();

            if (response[0].equals("USER Orders")) {
                for (int i = 1; i < response.length; i++) {
                    // Split into each dish
                    dishes = response[i].split(",");

                    // Create the hash map
                    LinkedHashMap<Dish, Number> orderItems = new LinkedHashMap<>();

                    for (String dish : dishes) {
                        dishInfo = dish.split("/");
                        orderItems.put(new Dish(dishInfo[0], dishInfo[1], Double.parseDouble(dishInfo[2]),
                                        Integer.parseInt(dishInfo[3]), Integer.parseInt(dishInfo[4])),
                                Integer.parseInt(dishInfo[5]));
                    }
                    orders.add(new Order(user, orderItems));
                }
                return orders;
            } else {
                System.err.println("Fetching orders failed");
            }
        }
        return new ArrayList<>();
    }

    /**
     * @param order order to lookup
     * @return If the order has been completed (delivered by a drone to the user)
     */
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

    /**
     * @param order order to lookup
     * @return The status text. Example data includes: Processing, Completed
     */
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

    /**
     * @param order to lookup
     * @return The cost of a given order.
     */
    @Override
    public Number getOrderCost(Order order) {
        return order.getCost();
    }

    /**
     * Cancels an order which will prevent it from being fulfilled, although if it has already been completed it
     * will have no effect.
     * @param order to cancel
     */
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
        updateListeners.add(listener);
    }

    @Override
    public void notifyUpdate() {
        for (UpdateListener updateListener : updateListeners) {
            updateListener.updated(new UpdateEvent());
        }
    }
}
