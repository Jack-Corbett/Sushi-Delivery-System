package common;

import server.Server;

import java.util.Map;

public class Drone extends Model implements Runnable {

    private IngredientStock is;
    private DishStock ds;
    private Server server;
    private Integer speed;
    private String status;
    private boolean running;

    public Drone(Server server, IngredientStock ingredientStock, DishStock dishStock, Integer speed) {
        setName("ID: " + Math.random());
        this.server = server;
        this.is = ingredientStock;
        this.ds = dishStock;
        this.speed = speed;
        status = "Idle";
        running = true;
    }

    @Override
    public void run() {
        while (running) {
            // Check if any dishes need delivering - this is prioritised over stocking up on ingredients
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
                    System.out.println(status);
                    try {
                        Thread.sleep((order.getDistance() / speed) + 5000);

                        for (Map.Entry<Dish, Number> entry : order.getItems().entrySet()) {
                            Dish dish = entry.getKey();
                            Number amount = entry.getValue();
                            for (int i = 0; i < amount.intValue(); i++) {
                                ds.removeDish(dish);
                            }
                        }
                        order.setComplete();
                        server.completedOrders.add(order);
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
                System.out.println(status);
                try {
                    Thread.sleep((ingredient.getSupplier().getDistance() / speed) + 5000);
                    is.addStock(ingredient, ingredient.getRestockAmount());
                } catch (InterruptedException e) {
                    System.err.println("Drone failed to restock ingredient: " + ingredient.getName());
                }
                ingredient.restocking = false;
                status = "Idle";
                notifyUpdate();
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                System.err.println("Failed to wait before checking dish stock");
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
