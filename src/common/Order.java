package common;

import java.util.ArrayList;

public class Order extends Model {

    private ArrayList<String> items;
    private Boolean status;
    private Postcode postcode;

    public Order(ArrayList<String> items, Postcode postcode) {
        this.items = items;
        this.postcode = postcode;
        setName("Order: " + items.toString() + " " + getPostcode());
        this.status = false;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public Postcode getPostcode() {
        return postcode;
    }

    public ArrayList<String> getItems() {
        return items;
    }

    @Override
    public String getName() {
        return null;
    }
}
