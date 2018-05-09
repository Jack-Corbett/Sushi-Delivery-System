package common;

public class Drone extends Model implements Runnable {

    private Integer speed;
    private IngredientStock ingredientStock;
    private String status;
    private boolean running;

    public Drone(IngredientStock ingredientStock, Integer speed) {
        this.ingredientStock = ingredientStock;
        this.speed = speed;
        status = "Idle";
        running = true;
    }

    @Override
    public void run() {
        while (running) {
            // Check if a order needs to be
            // IMPLEMENT THIS - SERVER TELLS IT TO RUN

            for (Ingredient ingredient : ingredientStock.getStock().keySet()) {
                if (ingredient.getAmount() < ingredient.getRestockAmount()) {
                    status = "Restocking: " + ingredient.getName();
                    try {
                        Thread.sleep(ingredient.getSupplier().getDistance()/speed);
                    } catch (InterruptedException e) {
                        System.err.println("Failed to restock ingredient: " + ingredient.getName());
                    }
                }
                status = "Idle";
            }
        }
    }

    @Override
    public String getName() {
        return null;
    }

    public Integer getSpeed() {
        return speed;
    }

    public String getStatus() {
        return status;
    }
}
