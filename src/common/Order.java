package common;

import java.util.HashMap;

public class Order extends Model {

    private User user;
    private HashMap<Dish, Integer> items;
    private Boolean complete;
    private String status;

    public Order(User user, HashMap<Dish, Integer> items) {
        this.items = items;
        this.user = user;
        setName("Order: " + items.toString() + " " + getUser());
        this.complete = false;
        this.status = "Processing";
    }

    public User getUser() {
        return user;
    }

    public HashMap<Dish, Integer> getItems() {
        return items;
    }

    public Boolean getComplete() {
        return complete;
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
            total += (dish.getPrice().doubleValue() * items.get(dish));
        }
        return total;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setComplete() {
        this.complete = true;
    }
}
