package common;

import server.Server;

public class Staff extends Model implements Runnable {

    private String status;
    private Boolean working;
    // References to stock controllers
    private IngredientStock ingredientStock;
    private DishStock dishStock;
    private Server server;

    public Staff(Server server, String name, IngredientStock ingredientStock, DishStock dishStock) {
        setName(name);
        status = "Idle";
        working = true;
        this.ingredientStock = ingredientStock;
        this.dishStock = dishStock;
        this.server = server;
    }

    @Override
    public void run() {
        while (working) {
            Boolean makeDish = true;

            Dish dish = server.restockDishQueue.poll();

            if (dish != null) {
                // Check there are enough ingredients
                for (Ingredient ingredient : dish.getRecipe().keySet()) {
                    if (ingredient != null) {
                        if (ingredientStock.getStock().get(ingredient).intValue() <
                                dish.getRecipe().get(ingredient).intValue()) {
                            makeDish = false;
                        }
                    } else {
                        makeDish = false;
                    }
                }
                // If there are enough ingredients prepare the dish
                if (makeDish) {
                    prepareDish(dish);
                } else {
                    server.restockDishQueue.add(dish);
                }
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                System.err.println("Failed to wait before checking dish stock");
            }
        }
    }

    private void prepareDish(Dish dish) {
        try {
            status = "Preparing " + dish.getName();
            notifyUpdate();

            // Remove the ingredients used from the stock system
            ingredientStock.removeStock(dish.getRecipe());

            Thread.sleep((long) (Math.random() * 60000 + 20000));

            // Add the newly prepared dish to the stock system
            dishStock.addStock(dish, 1);

            status = "Idle";
            notifyUpdate();
        } catch (Exception e) {
            System.err.println("Staff member: " + getName() + " was unable to prepare " + dish.getName());
        }
    }

    @Override
    public String getName() {
        return name;
    }

    public String getStatus() {
        return status;
    }
}
