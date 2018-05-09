package common;

public class Staff extends Model implements Runnable {

    private String status;
    private Boolean working;
    // References to stock controllers
    private IngredientStock ingredientStock;
    private DishStock dishStock;

    public Staff(String name, IngredientStock ingredientStock, DishStock dishStock) {
        setName(name);
        status = "Idle";
        working = true;
        this.ingredientStock = ingredientStock;
        this.dishStock = dishStock;
    }

    @Override
    public void run() {
        while (working) {
            Boolean makeDish;
            // Monitor stock levels of dishes
            for (Dish dish : dishStock.getStock().keySet()) {
                // Flag to trigger dish preparation
                makeDish = true;

                // If the amount of those dishes is below the restocking threshold prepare another
                if (dishStock.getStock().get(dish).intValue() < dish.getRestockThreshold().intValue()){
                    // Check there are enough ingredients
                    for (Ingredient ingredient : dish.getRecipe().keySet()) {
                        if (ingredientStock.getStock().get(ingredient).intValue() < ingredient.getRestockThreshold()) {
                            makeDish = false;
                        }
                    }
                    // If there are enough ingredients prepare the dish
                    if (makeDish) prepareDish(dish);
                }
            }
        }
    }

    private void prepareDish(Dish dish) {
        try {
            status = "Preparing " + dish.getName();

            // Remove the ingredients used from the stock system
            ingredientStock.removeStock(dish.getRecipe());

            Thread.sleep((long) (Math.random() * 60000 + 20000));

            // Add the newly prepared dish to the stock system
            dishStock.addStock(dish, 1);

            status = "Idle";
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
