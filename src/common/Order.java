package common;

import java.util.LinkedHashMap;

public class Order extends Model {

    private User user;
    private LinkedHashMap<Dish, Number> items;
    private Boolean complete;
    private Boolean cancelled;
    private String status;

    public Order(User user, LinkedHashMap<Dish, Number> items) {
        this.items = items;
        this.user = user;
        setName(getUser() + " " + items.toString());
        this.complete = false;
        this.cancelled = false;
        this.status = "Processing";
    }

    public User getUser() {
        return user;
    }

    public LinkedHashMap<Dish, Number> getItems() {
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
        if (!items.isEmpty()) {
            for (Dish dish : items.keySet()) {
                total += (dish.getPrice().doubleValue() * items.get(dish).intValue());
            }
        }
        return total;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // MAY NEED TO CHANGE THE ORDERING
    public void setComplete() {
        notifyUpdate("status", status, "Complete");
        status = "Complete";
        notifyUpdate("complete", complete, true);
        this.complete = true;

    }

    public void setCancelled() {
        notifyUpdate("status", status, "Cancelled");
        status = "Cancelled";
        notifyUpdate("cancelled", cancelled, true);
        this.cancelled = true;
    }
}
