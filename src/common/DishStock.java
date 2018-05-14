package common;

import server.Server;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class DishStock {

    private ConcurrentHashMap<Dish, Number> stock;
    private boolean restockingEnabled;
    private boolean running;

    public DishStock(Server server) {
        stock = new ConcurrentHashMap<>();
        restockingEnabled = true;
        running = true;

        Thread dishStockMonitor = new Thread(() -> {
            while (running) {
                if (restockingEnabled) {
                    for (Dish dish : stock.keySet()) {
                        if (stock.get(dish).intValue() < dish.getRestockThreshold().intValue()) {
                            if (!dish.restocking) {
                                System.out.println("Added: " + dish.getName());
                                server.restockDishQueue.add(dish);
                                dish.restocking = true;
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
        dishStockMonitor.start();
    }

    public void setRestockingEnabled(boolean enabled) {
        this.restockingEnabled = enabled;
    }

    public ConcurrentHashMap<Dish, Number> getStock() {
        return stock;
    }

    public List<Dish> getDishes() {
        return new ArrayList<>(stock.keySet());
    }

    public void addDishToStock(Dish dish, Number number) {
        stock.put(dish, number);
    }

    public void setStockLevel(Dish dish, Number number) {
        stock.replace(dish, number);
    }

    public void addStock(Dish dish, Number amount) {
        stock.put(dish, stock.getOrDefault(dish, 0).intValue() + amount.intValue());
    }

    public void removeDish(Dish dish) {
        stock.put(dish, stock.get(dish).intValue() - 1);
    }
}
