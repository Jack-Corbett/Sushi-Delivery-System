package common;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class IngredientStock {

    private ConcurrentHashMap<Ingredient, Number> stock;
    private boolean restockingEnabled;

    public IngredientStock() {
        stock = new ConcurrentHashMap<>();
        restockingEnabled = true;
    }

    public void setRestockingEnabled(boolean enabled) {
        this.restockingEnabled = enabled;
    }

    public ConcurrentHashMap<Ingredient, Number> getStock() {
        return stock;
    }

    public List<Ingredient> getIngredients() {
        return new ArrayList<>(stock.keySet());
    }

    public void addIngredientToStock(Ingredient ingredient, Number number) {
        stock.put(ingredient, number);
    }

    public void setStockLevel(Ingredient ingredient, Number number) {
        stock.replace(ingredient, number);
    }

    public void addStock(Ingredient ingredient, Number amount) {
        stock.put(ingredient, stock.getOrDefault(ingredient, 0).intValue() + amount.intValue());
    }

    public void removeStock(Ingredient ingredient) {
        stock.put(ingredient, stock.get(ingredient).intValue() - 1);
    }

    public void removeStock(Map<Ingredient, Number> recipe) {
        for (Map.Entry<Ingredient, Number> entry : recipe.entrySet()) {
            Ingredient ingredient = entry.getKey();
            Number amountUsed = entry.getValue();

            if (stock.containsKey(ingredient)) {
                stock.put(ingredient, stock.get(ingredient).intValue() - amountUsed.intValue());
            } else {
                System.err.println("Ingredient not found in stock system");
            }
        }
    }
}