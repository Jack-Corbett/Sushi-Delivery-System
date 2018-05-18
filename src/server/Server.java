package server;

import common.*;

import java.io.FileNotFoundException;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Server class responsible for implementing the ServerInterface and using the CommsServer class to act on messages
 * sent by the clients. It also stores the object references including the stock controllers.
 * @author Jack Corbett
 */
public class Server implements ServerInterface {

    // Responsible for backing up the server state to file when a change is made
    public DataPersistence dataPersistence = new DataPersistence();

    // STORE OBJECT REFERENCES
    // Instantiate the stock controllers
    public IngredientStock is = new IngredientStock(this);
    private DishStock ds = new DishStock(this);

    // Instantiate array lists to store the suppliers, postcodes, staff, drones and users
    private ArrayList<Supplier> suppliers = new ArrayList<>();
    private ArrayList<Postcode> postcodes = new ArrayList<>();
    private ArrayList<Staff> staff = new ArrayList<>();
    private ArrayList<Drone> drones = new ArrayList<>();
    public ArrayList<User> users = new ArrayList<>();

    // The orders array list stores all orders regardless of their status which is used to display orders in the UI
    public ArrayList<Order> orders = new ArrayList<>();
    /* The order queue is used by the drones so they can be delivered in the order that the orders are received.
    Although this does also depend on if the dishes are ready, if not the order is added to the end of the queue again
    */
    public ConcurrentLinkedQueue<Order> orderQueue = new ConcurrentLinkedQueue<>();

    // A queue used by the drones to check for ingredients that need to be restocked
    public ConcurrentLinkedQueue<Ingredient> restockIngredientQueue = new ConcurrentLinkedQueue<>();
    // A queue used by the staff to check for dishes that need to be restocked
    public ConcurrentLinkedQueue<Dish> restockDishQueue = new ConcurrentLinkedQueue<>();

    // Stores a reference to all update listeners added so the UI can be updated when changes take place
    private ArrayList<UpdateListener> updateListeners = new ArrayList<>();

    /**
     * Instantiate a new CommsServer passing the server object
     */
    public Server() {
        new CommsServer(this);
    }

    /**
     * Sets up all system properties by reading settings from a text file
     * @param filename configuration file to load
     * @throws FileNotFoundException If the configuration file cannot be found in the common package
     */
    @Override
    public void loadConfiguration(String filename) throws FileNotFoundException {
        new Configuration(filename, this);
    }

    /**
     * @param enabled set to true to enable restocking of ingredients, or false to disable.
     */
    @Override
    public void setRestockingIngredientsEnabled(boolean enabled) {
         is.setRestockingEnabled(enabled);
    }

    /**
     * @param enabled set to true to enable restocking of dishes, or false to disable.
     */
    @Override
    public void setRestockingDishesEnabled(boolean enabled) {
        ds.setRestockingEnabled(enabled);
    }

    // DISH

    /**
     * @param dish dish to set the stock
     * @param stock stock amount
     */
    @Override
    public void setStock(Dish dish, Number stock) {
        ds.setStockLevel(dish, stock);
        notifyUpdate();
    }

    /**
     * @param ingredient ingredient to set the stock
     * @param stock stock amount
     */
    @Override
    public void setStock(Ingredient ingredient, Number stock) {
        is.setStockLevel(ingredient, stock);
        notifyUpdate();
    }

    /**
     * @return A list of all stocked dishes
     */
    @Override
    public List<Dish> getDishes() {
        return ds.getDishes();
    }

    /**
     * Adds a new dish to the stock system
     * @param name name of dish
     * @param description description of dish
     * @param price price of dish
     * @param restockThreshold minimum threshold to reach before restocking
     * @param restockAmount amount to restock by
     * @return The newly added dish object
     */
    @Override
    public Dish addDish(String name, String description, Number price, Number restockThreshold, Number restockAmount) {
        Dish dish = new Dish(name, description, price.doubleValue(), restockThreshold.intValue(),
                restockAmount.intValue());
        ds.addDishToStock(dish, 0);
        notifyUpdate();
        return dish;
    }

    /**
     * @param dish dish to remove
     * @throws UnableToDeleteException If the dish cannot be removed from the stock system
     */
    @Override
    public void removeDish(Dish dish) throws UnableToDeleteException {
        ds.removeDish(dish);
        notifyUpdate();
    }

    /**
     * @param dish dish to edit the recipe of
     * @param ingredient ingredient to add/update
     * @param quantity quantity to set. Should update and replace, not add to.
     */
    @Override
    public void addIngredientToDish(Dish dish, Ingredient ingredient, Number quantity) {
        dish.addIngredient(ingredient, quantity.intValue());
        notifyUpdate();
    }

    /**
     * @param dish dish to edit the recipe of
     * @param ingredient ingredient to completely remove
     */
    @Override
    public void removeIngredientFromDish(Dish dish, Ingredient ingredient) {
        dish.removeIngredient(ingredient);
        notifyUpdate();
    }

    /**
     * @param dish dish to modify the recipe of
     * @param recipe map of ingredients and quantity numbers to update
     */
    @Override
    public void setRecipe(Dish dish, Map<Ingredient, Number> recipe) {
        dish.setRecipe(recipe);
        notifyUpdate();
    }

    /**
     * @param dish dish to modify the restocking levels of
     * @param restockThreshold new amount at which to restock
     * @param restockAmount new amount to restock by when threshold is reached
     */
    @Override
    public void setRestockLevels(Dish dish, Number restockThreshold, Number restockAmount) {
        dish.setRestockThreshold(restockThreshold.intValue());
        dish.setRestockAmount(restockAmount.intValue());
        notifyUpdate();
    }

    /**
     * @param dish dish to query restock threshold of
     * @return The restock threshold of that dish
     */
    @Override
    public Number getRestockThreshold(Dish dish) {
        return dish.getRestockThreshold();
    }

    /**
     * @param dish dish to query restock amount of
     * @return The restock amount of that dish
     */
    @Override
    public Number getRestockAmount(Dish dish) {
        return dish.getRestockAmount();
    }

    /**
     * @param dish dish to query the recipe of
     * @return The recipe map of that dish
     */
    @Override
    public Map<Ingredient, Number> getRecipe(Dish dish) {
        return dish.getRecipe();
    }

    /**
     * @return The stock levels of all dishes
     */
    @Override
    public Map<Dish, Number> getDishStockLevels() {
        return ds.getStock();
    }

    // INGREDIENTS
    /**
     * @return A list of all ingredients
     */
    @Override
    public List<Ingredient> getIngredients() {
        return is.getIngredients();
    }

    /**
     * @param name name of the ingredient
     * @param unit unit the ingredient is measured in
     * @param supplier supplier
     * @param restockThreshold when amount reaches restockThreshold restock
     * @param restockAmount when threshold is reached, restock with this amount
     * @return The newly added ingredient object
     */
    @Override
    public Ingredient addIngredient(String name, String unit, Supplier supplier, Number restockThreshold,
                                    Number restockAmount) {
        Ingredient ingredient = new Ingredient(name, unit, supplier, restockThreshold.intValue(),
                restockAmount.intValue());
        is.addIngredientToStock(ingredient, 0);
        notifyUpdate();
        return ingredient;
    }

    /**
     * @param ingredient ingredient to remove
     * @throws UnableToDeleteException If the ingredient cannot be removed from the stock system
     */
    @Override
    public void removeIngredient(Ingredient ingredient) throws UnableToDeleteException {
        is.removeStock(ingredient);
        notifyUpdate();
    }

    /**
     * @param ingredient ingredient to modify the restocking levels of
     * @param restockThreshold new amount at which to restock
     * @param restockAmount new amount to restock by when threshold is reached
     */
    @Override
    public void setRestockLevels(Ingredient ingredient, Number restockThreshold, Number restockAmount) {
        ingredient.setRestockThreshold(restockThreshold.intValue());
        ingredient.setRestockAmount(restockAmount.intValue());
        notifyUpdate();
    }

    /**
     * @param ingredient ingredient to query restock threshold of
     * @return The restock threshold of that ingredient
     */
    @Override
    public Number getRestockThreshold(Ingredient ingredient) {
        return ingredient.getRestockThreshold();
    }

    /**
     * @param ingredient ingredient to query restock amount of
     * @return The restock amount of that ingredient
     */
    @Override
    public Number getRestockAmount(Ingredient ingredient) {
        return ingredient.getRestockAmount();
    }

    /**
     * @return The stock levels of all ingredients
     */
    @Override
    public Map<Ingredient, Number> getIngredientStockLevels() {
        return is.getStock();
    }

    // SUPPLIERS
    /**
     * @return A list of all suppliers
     */
    @Override
    public List<Supplier> getSuppliers() {
        return suppliers;
    }

    /**
     * @param name name of supplier
     * @param distance from restaurant
     * @return The newly added supplier
     */
    @Override
    public Supplier addSupplier(String name, Number distance) {
        Supplier supplier = new Supplier(name, distance.intValue());
        suppliers.add(supplier);
        notifyUpdate();
        return supplier;
    }

    /**
     * @param supplier supplier to remove
     * @throws UnableToDeleteException If the supplier cannot be removed
     */
    @Override
    public void removeSupplier(Supplier supplier) throws UnableToDeleteException {
        suppliers.remove(supplier);
        notifyUpdate();
    }

    /**
     * @param supplier supplier to query
     * @return The distance of the supplier from the sushi restaurant
     */
    @Override
    public Number getSupplierDistance(Supplier supplier) {
        return supplier.getDistance();
    }

    // DRONES
    /**
     * @return A list of all the drones added to the system
     */
    @Override
    public List<Drone> getDrones() {
        return drones;
    }

    /**
     * @param speed speed of drone
     * @return Newly added drone object
     */
    @Override
    public Drone addDrone(Number speed) {
        Drone drone = new Drone(this, is, ds, speed.intValue());
        new Thread(drone).start();
        drones.add(drone);
        notifyUpdate();
        return drone;
    }

    /**
     * @param drone drone to remove
     * @throws UnableToDeleteException If the drone cannot be removed
     */
    @Override
    public void removeDrone(Drone drone) throws UnableToDeleteException {
        drones.remove(drone);
        notifyUpdate();
    }

    /**
     * @param drone drone to query
     * @return The speed of the drone
     */
    @Override
    public Number getDroneSpeed(Drone drone) {
        return drone.getSpeed();
    }

    /**
     * @param drone drone to query
     * @return The status of the drone
     */
    @Override
    public String getDroneStatus(Drone drone) {
        return drone.getStatus();
    }

    // STAFF
    /**
     * @return A list of all the staff added to the system
     */
    @Override
    public List<Staff> getStaff() {
        return staff;
    }

    /**
     * @param name name of staff member
     * @return The newly added staff member
     */
    @Override
    public Staff addStaff(String name) {
        Staff staffMember = new Staff(this, name, ds, is);
        new Thread(staffMember).start();
        staff.add(staffMember);
        notifyUpdate();
        return staffMember;
    }

    /**
     * @param staff staff member to remove
     * @throws UnableToDeleteException If the staff member cannot be removed
     */
    @Override
    public void removeStaff(Staff staff) throws UnableToDeleteException {
        this.staff.remove(staff);
        notifyUpdate();
    }

    /**
     * @param staff member to query
     * @return The status of that staff member
     */
    @Override
    public String getStaffStatus(Staff staff) {
        return staff.getStatus();
    }

    // ORDERS
    /**
     * @return A list of all the orders that have been placed
     */
    @Override
    public List<Order> getOrders() {
        return orders;
    }

    /**
     * @param order order to remove from both the queue and array list
     * @throws UnableToDeleteException If the order cannot be removed
     */
    @Override
    public void removeOrder(Order order) throws UnableToDeleteException {
        orderQueue.remove(order);
        orders.remove(order);
        notifyUpdate();
    }

    /**
     * @param order order to query
     * @return Distance to the delivery location
     */
    @Override
    public Number getOrderDistance(Order order) {
        return order.getDistance();
    }

    /**
     * @param order order to query
     * @return If the order has been completed (delivered)
     */
    @Override
    public boolean isOrderComplete(Order order) {
        return order.getComplete();
    }

    /**
     * @param order order to query
     * @return The order's current status
     */
    @Override
    public String getOrderStatus(Order order) {
        return order.getStatus();
    }

    /**
     * @param order order to query
     * @return The total cost of the order
     */
    @Override
    public Number getOrderCost(Order order) {
        return order.getCost();
    }

    // POSTCODES
    /**
     * @return A list of all postcodes added to the system
     */
    @Override
    public List<Postcode> getPostcodes() {
        return postcodes;
    }

    /**
     * @param code postcode string representation
     * @param distance distance from the restaurant
     */
    @Override
    public void addPostcode(String code, Number distance) {
        postcodes.add(new Postcode(code, distance.intValue()));
        notifyUpdate();
    }

    /**
     * @param postcode postcode to remove
     * @throws UnableToDeleteException If the postcode cannot be removed
     */
    @Override
    public void removePostcode(Postcode postcode) throws UnableToDeleteException {
        postcodes.remove(postcode);
        notifyUpdate();
    }

    // USERS
    /**
     * @return A list of all registered users
     */
    @Override
    public List<User> getUsers() {
        return users;
    }

    /**
     * @param user to remove
     * @throws UnableToDeleteException If the user cannot be removed
     */
    @Override
    public void removeUser(User user) throws UnableToDeleteException {
        users.remove(user);
        notifyUpdate();
    }

    /**
     * @param listener An update listener to be informed of all model changes.
     */
    @Override
    public void addUpdateListener(UpdateListener listener) {
        updateListeners.add(listener);
    }

    /**
     * Notifies all update listeners
     */
    @Override
    public void notifyUpdate() {
        for (UpdateListener updateListener : updateListeners) {
            updateListener.updated(new UpdateEvent());
        }
    }
}
