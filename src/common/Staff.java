package common;

public class Staff extends Model implements Runnable {

    private String status;
    private Boolean working;
    // References to stock controllers
    private IngredientStock ingredientStock;
    private DishStock dishStock;

    Staff(String name, IngredientStock ingredientStock, DishStock dishStock) {
        setName(name);
        status = "Idle";
        working = true;
        this.ingredientStock = ingredientStock;
        this.dishStock = dishStock;
    }

    @Override
    public void run() {
        while (working) {
            // Flag to trigger dish preparation
            Boolean makeDish = true;

            // Monitor stock levels of dishes
            for (Dish dish : dishStock.getStock()) {
                // If the amount of those dishes is below the restocking amount prepare another
                if (dish.getAmount() < dish.getRestockAmount()) {
                    // Check there are enough ingredients
                    for (Ingredient ingredient : dish.getRecipe().keySet()) {
                        if (ingredient.getAmount() < ingredient.getRestockAmount()) {
                            makeDish = false;
                        }
                    }
                    // If there are enough ingredients prepare the dish
                    if (makeDish) prepareDish(dish);
                    makeDish = true;
                }
            }
        }
    }

    private void prepareDish(Dish dish) {
        try {
            status = "Preparing " + dish.getName();

            // Remove the ingredients used from the stock system
            ingredientStock.removeStock(dish.getRecipe());

            try {
                Thread.sleep((long) (Math.random() * 60000 + 20000));
            } catch (InterruptedException e) {
                System.err.println("Thread waiting failed");
                e.printStackTrace();
            }

            // Add the newly prepared dish to the stock system
            dishStock.addPreparedDish(dish);

            status = "Idle";

        } catch (Exception e) {
            System.err.println("Staff member: " + this.getName() + " was unable to prepare " + dish.getName());
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
