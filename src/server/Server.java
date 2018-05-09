package server;

import common.*;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Server implements ServerInterface {

    // STORE OBJECT REFERENCES
    public IngredientStock is = new IngredientStock();
    private DishStock ds = new DishStock();
    private ArrayList<Supplier> suppliers = new ArrayList<>();
    private ArrayList<Postcode> postcodes = new ArrayList<>();
    public ArrayList<User> users = new ArrayList<>();

    public ArrayList<Order> completedOrders = new ArrayList<>();
    public ConcurrentLinkedQueue<Order> orderQueue = new ConcurrentLinkedQueue<>();

    private ArrayList<Staff> staff = new ArrayList<>();
    private ArrayList<Drone> drones = new ArrayList<>();

    @Override
    public void loadConfiguration(String filename) throws FileNotFoundException {
        new Configuration(filename, this);
    }

    @Override
    public void setRestockingIngredientsEnabled(boolean enabled) {
         is.setRestockingEnabled(enabled);
    }

    @Override
    public void setRestockingDishesEnabled(boolean enabled) {
        ds.setRestockingEnabled(enabled);
    }

    // DISH

    @Override
    public void setStock(Dish dish, Number stock) {
        ds.setStockLevel(dish, stock);
    }

    @Override
    public void setStock(Ingredient ingredient, Number stock) {
        is.setStockLevel(ingredient, stock);
    }

    @Override
    public List<Dish> getDishes() {
        return ds.getDishes();
    }

    @Override
    public Dish addDish(String name, String description, Number price, Number restockThreshold, Number restockAmount) {
        Dish dish = new Dish(name, description, price.doubleValue(), restockThreshold.intValue(),
                restockAmount.intValue());
        ds.addDishToStock(dish, 0);
        return dish;
    }

    @Override
    public void removeDish(Dish dish) throws UnableToDeleteException {
        ds.removeDish(dish);
    }

    @Override
    public void addIngredientToDish(Dish dish, Ingredient ingredient, Number quantity) {
        dish.addIngredient(ingredient, quantity.intValue());
    }

    @Override
    public void removeIngredientFromDish(Dish dish, Ingredient ingredient) {
        dish.removeIngredient(ingredient);
    }

    @Override
    public void setRecipe(Dish dish, Map<Ingredient, Number> recipe) {
        dish.setRecipe(recipe);
    }

    @Override
    public void setRestockLevels(Dish dish, Number restockThreshold, Number restockAmount) {
        dish.setRestockThreshold(restockThreshold.intValue());
        dish.setRestockAmount(restockAmount.intValue());
    }

    @Override
    public Number getRestockThreshold(Dish dish) {
        return dish.getRestockThreshold();
    }

    @Override
    public Number getRestockAmount(Dish dish) {
        return dish.getRestockAmount();
    }

    @Override
    public Map<Ingredient, Number> getRecipe(Dish dish) {
        return dish.getRecipe();
    }

    @Override
    public Map<Dish, Number> getDishStockLevels() {
        return ds.getStock();
    }

    // INGREDIENTS

    @Override
    public List<Ingredient> getIngredients() {
        return is.getIngredients();
    }

    @Override
    public Ingredient addIngredient(String name, String unit, Supplier supplier, Number restockThreshold,
                                    Number restockAmount) {
        Ingredient ingredient = new Ingredient(name, unit, supplier, restockThreshold.intValue(),
                restockAmount.intValue());
        is.addIngredientToStock(ingredient, 0);
        return ingredient;
    }

    @Override
    public void removeIngredient(Ingredient ingredient) throws UnableToDeleteException {
        is.removeStock(ingredient);
    }

    @Override
    public void setRestockLevels(Ingredient ingredient, Number restockThreshold, Number restockAmount) {
        ingredient.setRestockThreshold(restockThreshold.intValue());
        ingredient.setRestockAmount(restockAmount.intValue());
    }

    @Override
    public Number getRestockThreshold(Ingredient ingredient) {
        return ingredient.getRestockThreshold();
    }

    @Override
    public Number getRestockAmount(Ingredient ingredient) {
        return ingredient.getRestockAmount();
    }

    @Override
    public Map<Ingredient, Number> getIngredientStockLevels() {
        return is.getStock();
    }

    // SUPPLIERS

    @Override
    public List<Supplier> getSuppliers() {
        return suppliers;
    }

    @Override
    public Supplier addSupplier(String name, Number distance) {
        Supplier supplier = new Supplier(name, distance.intValue());
        suppliers.add(supplier);
        return supplier;
    }

    @Override
    public void removeSupplier(Supplier supplier) throws UnableToDeleteException {
        suppliers.remove(supplier);
    }

    @Override
    public Number getSupplierDistance(Supplier supplier) {
        return supplier.getDistance();
    }

    // DRONES

    @Override
    public List<Drone> getDrones() {
        return drones;
    }

    @Override
    public Drone addDrone(Number speed) {
        Drone drone = new Drone(this, is, speed.intValue());
        drones.add(drone);
        return drone;
    }

    @Override
    public void removeDrone(Drone drone) throws UnableToDeleteException {
        drones.remove(drone);
    }

    @Override
    public Number getDroneSpeed(Drone drone) {
        return drone.getSpeed();
    }

    @Override
    public String getDroneStatus(Drone drone) {
        return drone.getStatus();
    }

    // STAFF

    @Override
    public List<Staff> getStaff() {
        return staff;
    }

    @Override
    public Staff addStaff(String name) {
        Staff staffMember = new Staff(name, is, ds);
        staff.add(staffMember);
        return staffMember;
    }

    @Override
    public void removeStaff(Staff staff) throws UnableToDeleteException {
        this.staff.remove(staff);
    }

    @Override
    public String getStaffStatus(Staff staff) {
        return staff.getStatus();
    }

    // ORDERS

    @Override
    public List<Order> getOrders() {
        return completedOrders;
    }

    @Override
    public void removeOrder(Order order) throws UnableToDeleteException {
        completedOrders.remove(order);
    }

    @Override
    public Number getOrderDistance(Order order) {
        return order.getDistance();
    }

    @Override
    public boolean isOrderComplete(Order order) {
        return order.getComplete();
    }

    @Override
    public String getOrderStatus(Order order) {
        return order.getStatus();
    }

    @Override
    public Number getOrderCost(Order order) {
        return order.getCost();
    }

    // POSTCODES

    @Override
    public List<Postcode> getPostcodes() {
        return postcodes;
    }

    @Override
    public void addPostcode(String code, Number distance) {
        postcodes.add(new Postcode(code, distance.intValue()));
    }

    @Override
    public void removePostcode(Postcode postcode) throws UnableToDeleteException {
        postcodes.remove(postcode);
    }

    // USERS

    @Override
    public List<User> getUsers() {
        return users;
    }

    @Override
    public void removeUser(User user) throws UnableToDeleteException {
        users.remove(user);
    }

    @Override
    public void addUpdateListener(UpdateListener listener) {

    }

    @Override
    public void notifyUpdate() {

    }
}
