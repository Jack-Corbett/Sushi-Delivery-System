package common;

import java.util.ArrayList;

public class DishStock {

    private ArrayList<Dish> stock = new ArrayList<>();
    private IngredientStock ingredientStock;

    public DishStock(IngredientStock ingredientStock) {
        this.ingredientStock = ingredientStock;
    }

    public ArrayList<Dish> getStock() {
        return stock;
    }

    public void addPreparedDish(Dish dish) {
        synchronized (dish.amount) { dish.amount ++; }
    }

    // Sending orders by drone and setting staff implemented later
}
