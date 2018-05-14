package common;

import server.Server;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class IngredientStock {

    private ConcurrentHashMap<Ingredient, Number> stock;
    private boolean restockingEnabled;
    private boolean running;

    public IngredientStock(Server server) {
        stock = new ConcurrentHashMap<>();
        restockingEnabled = true;
        running = true;

        Thread ingredientStockMonitor = new Thread(() -> {
            while (running) {
                if (restockingEnabled) {
                    for (Ingredient ingredient : stock.keySet()) {
                        if (stock.get(ingredient).intValue() < ingredient.getRestockThreshold()) {
                            if (!ingredient.restocking) {
                                System.out.println("Added: " + ingredient.getName());
                                server.restockIngredientQueue.add(ingredient);
                                ingredient.restocking = true;
                            }
                        }
                    }
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    System.err.println("Failed to wait before checking ingredient stock");
                }
            }
        });
        ingredientStockMonitor.start();
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

    public void removeStock(Map<Ingredient, Number> recipe, Number numberOfDishes) {
        for (Map.Entry<Ingredient, Number> entry : recipe.entrySet()) {
            Ingredient ingredient = entry.getKey();
            Number amountUsed = entry.getValue();

            if (stock.containsKey(ingredient)) {
                stock.put(ingredient, stock.get(ingredient).intValue() -
                        (amountUsed.intValue() * numberOfDishes.intValue()));
            } else {
                System.err.println("Ingredient not found in stock system");
            }
        }
    }
}