package common;

import java.util.LinkedHashMap;

/**
 * An order submitted by a user, storing the user it belongs to, the dishes they want and the corresponding quantities.
 * @author Jack Corbett
 */
public class Order extends Model {

    private User user;
    // A linked hash map is used to that when orders are recreated the dishes always appear in the same order
    private LinkedHashMap<Dish, Number> items;
    private Boolean complete;
    private Boolean cancelled;
    private String status;

    /**
     * Sets order properties including setting the initial status of processing
     * @param user The user the order belongs to
     * @param items The dishes in the order
     */
    public Order(User user, LinkedHashMap<Dish, Number> items) {
        this.items = items;
        this.user = user;
        setName(getUser() + " " + items.toString());
        this.complete = false;
        this.cancelled = false;
        this.status = "Processing";
    }

    /**
     * @return The user the order belongs to
     */
    public User getUser() {
        return user;
    }

    /**
     * @return The dishes and corresponding qualities of the order
     */
    public LinkedHashMap<Dish, Number> getItems() {
        return items;
    }

    /**
     * @return If the order has been completed (delivered to the user)
     */
    public Boolean getComplete() {
        return complete;
    }

    /**
     * @return The orders current status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @return Name of the order (This takes the form: userName {items})
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * @return Distance between the sushi restaurant and the user
     */
    public Integer getDistance() {
        return user.getPostcode().getDistance();
    }

    /**
     * @return The sum of the costs of all dishes in the order
     */
    public Double getCost() {
        Double total = 0.0;
        if (!items.isEmpty()) {
            for (Dish dish : items.keySet()) {
                total += (dish.getPrice().doubleValue() * items.get(dish).intValue());
            }
        }
        return total;
    }

    /**
     * @param status The new order status
     */
    public void setStatus(String status) {
        notifyUpdate("status", this.status, status);
        this.status = status;
    }


    /**
     * Sets the order to complete, this also updates the status
     */
    public void setComplete() {
        setStatus("Complete");
        notifyUpdate("complete", complete, true);
        this.complete = true;

    }

    /**
     * Sets the order to cancelled, this also updates the status
     */
    public void setCancelled() {
        setStatus("Cancelled");
        notifyUpdate("cancelled", cancelled, true);
        this.cancelled = true;
    }
}
