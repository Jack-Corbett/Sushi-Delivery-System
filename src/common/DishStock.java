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
                        if (stock.get(dish).intValue() < dish.getRestockThreshold().intValue() + dish.getRestockAmount().intValue()) {
                            if (dish.noRestocking == 0 ||
                                    stock.get(dish).intValue() + dish.noRestocking < dish.getRestockThreshold().intValue() + dish.getRestockAmount().intValue()) {
                                if (!server.restockDishQueue.contains(dish)) {
                                    server.restockDishQueue.add(dish);
                                    dish.noRestocking ++;
                                }
                            }
                        }
                    }
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    System.err.println("Failed to wait before checking dish stock");
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
        if (dish.noRestocking > 0) dish.noRestocking --;
    }

    public void removeDish(Dish dish) {
        stock.put(dish, stock.get(dish).intValue() - 1);
    }
}
