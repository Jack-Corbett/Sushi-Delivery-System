package common;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class IngredientStock {

    private ArrayList<Ingredient> stock;

    public IngredientStock() {
        stock = new ArrayList<>();
    }

    public ArrayList<Ingredient> getStock() {
        return stock;
    }

    // Subtract stock when a recipe has been made
    public void removeStock(ConcurrentHashMap<Ingredient, Integer> recipe) {

        // May need to swap ingredient implementation to string for comparison
        for (Map.Entry<Ingredient, Integer> entry : recipe.entrySet()) {
            Ingredient ingredient = entry.getKey();
            Integer amountUsed = entry.getValue();

            if (stock.contains(ingredient)) {
                synchronized (ingredient.amount) {
                    ingredient.amount -= amountUsed;
                }
            } else {
                System.err.println("Ingredient not found in stock system");
            }
        }
    }

    // Used by drones
    public void addStock() {

    }

    public void addIngredientToStock(Ingredient ingredient) {

    }

}
