package common;

import java.util.HashMap;

public class Order extends Model {

    private User user;
    private HashMap<Dish, Number> items;
    private Boolean complete;
    private Boolean cancelled;
    private String status;

    public Order(User user, HashMap<Dish, Number> items) {
        this.items = items;
        this.user = user;
        setName("Order: " + items.toString() + " " + getUser());
        this.complete = false;
        this.cancelled = false;
        this.status = "Processing";
    }

    public User getUser() {
        return user;
    }

    public HashMap<Dish, Number> getItems() {
        return items;
    }

    public Boolean getComplete() {
        return complete;
    }

    public Boolean getCancelled() {
        return cancelled;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public String getName() {
        return name;
    }

    public Integer getDistance() {
        return user.getPostcode().getDistance();
    }

    public Double getCost() {
        Double total = 0.0;
        for (Dish dish : items.keySet()) {
            total += (dish.getPrice().doubleValue() * items.get(dish).intValue());
        }
        return total;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setComplete() {
        status = "Complete";
        this.complete = true;
    }

    public void setCancelled() {
        status = "Cancelled";
        this.cancelled = true;
    }
}
