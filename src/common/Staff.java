package common;

import server.Server;

/**
 * A staff member that is responsible for preparing more dishes when required.
 * @author Jack Corbett
 */
public class Staff extends Model implements Runnable {

    private String status;
    private Boolean working;
    // References to stock controllers and server
    private IngredientStock ingredientStock;
    private DishStock dishStock;
    private Server server;

    /**
     * Create a new staff members setting their properties.
     * @param server Reference to the server object
     * @param name Name of the staff member, which is also used to identify them
     * @param dishStock Reference to the dish stock controller
     * @param ingredientStock Reference to the ingredient stock controller
     */
    public Staff(Server server, String name,  DishStock dishStock, IngredientStock ingredientStock) {
        setName(name);
        status = "Idle";
        working = true;
        this.ingredientStock = ingredientStock;
        this.dishStock = dishStock;
        this.server = server;
    }

    /**
     * Loops checking to see if any dishes need to be prepared.
     */
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
                // If there are enough ingredients prepare the dish otherwise put the dish back in the queue
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

    /**
     * Prepare a single dish to be added to the stock system.
     * @param dish The dish to make
     */
    private void prepareDish(Dish dish) {
        try {
            status = "Preparing " + dish.getName();
            notifyUpdate();

            // Remove the ingredients used from the stock system
            ingredientStock.removeStock(dish.getRecipe());

            Thread.sleep((long) (Math.random() * 60000 + 20000));

            // Add the newly prepared dish to the stock system
            dishStock.addStock(dish);

            status = "Idle";
            notifyUpdate();
        } catch (Exception e) {
            System.err.println("Staff member: " + getName() + " was unable to prepare " + dish.getName());
        }
    }

    /**
     * @return Staff members name
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * @return Staff members current status
     */
    public String getStatus() {
        return status;
    }
}
