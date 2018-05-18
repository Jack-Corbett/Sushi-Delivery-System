package common;

import server.Server;

import java.util.Map;

/**
 * A drone is used to deliver orders to clients and to fetch ingredients to restock from the suppliers.
 * @author Jack Corbett
 */
public class Drone extends Model implements Runnable {

    private String status;
    private Boolean running;
    // References to stock controllers and server
    private IngredientStock is;
    private DishStock ds;
    private Server server;
    private Integer speed;

    /**
     * Create a new drone setting it's properties.
     * @param server Reference to the server object
     * @param ingredientStock Reference to the ingredient stock controller
     * @param dishStock Reference to the dish stock controller
     * @param speed Flying speed of the drone
     */
    public Drone(Server server, IngredientStock ingredientStock, DishStock dishStock, Integer speed) {
        // Use a random number for the drones name
        setName("" + Math.random());
        this.server = server;
        this.is = ingredientStock;
        this.ds = dishStock;
        this.speed = speed;
        status = "Idle";
        running = true;
    }

    /**
     * Loops checking to see if any orders need to be delivered or ingredients need to be restocked.
     */
    @Override
    public void run() {
        while (running) {
            /* Check if any dishes need delivering - this is prioritised over stocking up on ingredients to
               reduce client wait times */
            Order order = server.orderQueue.poll();

            if (order != null) {
                Boolean deliver = true;
                // Check if we have enough stock
                for (Map.Entry<Dish, Number> entry : order.getItems().entrySet()) {
                    Dish dish = entry.getKey();
                    Number amount = entry.getValue();
                    if (ds.getStock().get(dish).intValue() < amount.intValue()) {
                        deliver = false;
                    }
                }
                if (deliver) {
                    status = "Delivering: " + order.getName();
                    notifyUpdate();
                    try {
                        // Add 5 seconds to factor for loading and offloading dishes
                        Thread.sleep((((long)order.getDistance() /  (long)speed) * 20000) + 5000);

                        for (Map.Entry<Dish, Number> entry : order.getItems().entrySet()) {
                            Dish dish = entry.getKey();
                            Number amount = entry.getValue();
                            for (int i = 0; i < amount.intValue(); i++) {
                                ds.removeDish(dish);
                            }
                        }
                        order.setComplete();
                        // Trigger a backup to be saved
                        server.dataPersistence.backup(server);
                    } catch (InterruptedException e) {
                        System.err.println("Drone failed to deliver order: " + order.getName());
                    }
                    status = "Idle";
                    notifyUpdate();
                } else {
                    server.orderQueue.add(order);
                }
            }

            // Check if ingredients need restocking
            Ingredient ingredient = server.restockIngredientQueue.poll();

            if (ingredient != null) {
                status = "Restocking: " + ingredient.getName();
                notifyUpdate();
                try {
                    Thread.sleep(((long) ingredient.getSupplier().getDistance() / (long) speed) * 20000);
                    is.addStock(ingredient, ingredient.getRestockAmount());
                } catch (InterruptedException e) {
                    System.err.println("Drone failed to restock ingredient: " + ingredient.getName());
                }
                status = "Idle";
                notifyUpdate();
            }
            /* Wait 0.1 seconds before checking again to decrease CPU load as the spec requires this to be
                continually checked */
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                System.err.println("Failed to wait before checking dish stock");
            }
        }
    }

    /**
     * @return The drones name
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * @return The drones flying speed
     */
    public Integer getSpeed() {
        return speed;
    }

    /**
     * @return The drones status
     */
    public String getStatus() {
        return status;
    }
}
