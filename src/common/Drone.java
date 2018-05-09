package common;

import server.Server;

public class Drone extends Model implements Runnable {

    private IngredientStock is;
    private Server server;
    private Integer speed;
    private String status;
    private boolean running;

    public Drone(Server server, IngredientStock ingredientStock, Integer speed) {
        this.server = server;
        this.is = ingredientStock;
        this.speed = speed;
        status = "Idle";
        running = true;
    }

    @Override
    public void run() {
        while (running) {
            // Check if any dishes need delivering
            Order order;
            while ((order = server.orderQueue.poll()) != null) {
                status = "Delivering: " + order.getName();
                try {
                    Thread.sleep(order.getDistance()/speed);
                    order.setComplete();
                    server.completedOrders.add(order);
                } catch (InterruptedException e) {
                    System.err.println("Drone failed to deliver order: " + order.getName());
                }
                status = "Idle";
            }

            // Check if ingredients need restocking
            for (Ingredient ingredient : is.getStock().keySet()) {
                if (is.getStock().get(ingredient).intValue() < ingredient.getRestockAmount()) {
                    status = "Restocking: " + ingredient.getName();
                    try {
                        Thread.sleep(ingredient.getSupplier().getDistance()/speed);
                        is.addStock(ingredient, ingredient.getRestockAmount());
                    } catch (InterruptedException e) {
                        System.err.println("Drone failed to restock ingredient: " + ingredient.getName());
                    }
                }
                status = "Idle";
            }
        }
    }

    @Override
    public String getName() {
        return name;
    }

    public Integer getSpeed() {
        return speed;
    }

    public String getStatus() {
        return status;
    }
}
