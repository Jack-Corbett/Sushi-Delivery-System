package common;

import server.Server;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

public class DataPersistence {

    public void backup(Server server) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("backup.txt", false));
            StringBuilder sb = new StringBuilder();

            for (Supplier supplier : server.getSuppliers()) {
                sb.append("SUPPLIER:");
                sb.append(supplier.getName());
                sb.append(":");
                sb.append(supplier.getDistance());
                sb.append("\n");
            }

            sb.append("\n");

            for (Ingredient ingredient : server.getIngredients()) {
                sb.append("INGREDIENT:");
                sb.append(ingredient.getName());
                sb.append(":");
                sb.append(ingredient.getSupplier());
                sb.append(":");
                sb.append(ingredient.getRestockThreshold());
                sb.append(":");
                sb.append(ingredient.getRestockAmount());
                sb.append("\n");
            }

            sb.append("\n");

            for (Dish dish : server.getDishes()) {
                sb.append("DISH:");
                sb.append(dish.getName());
                sb.append(":");
                sb.append(dish.getDescription());
                sb.append(":");
                sb.append(dish.getPrice());
                sb.append(":");
                sb.append(dish.getRestockThreshold());
                sb.append(":");
                sb.append(dish.getRestockAmount());
                sb.append(":");
                for (Map.Entry<Ingredient, Number> entry : dish.getRecipe().entrySet()) {
                    Ingredient ingredient = entry.getKey();
                    Number amount = entry.getValue();
                    sb.append(amount);
                    sb.append(" * ");
                    sb.append(ingredient.getName());
                    sb.append(",");
                }
                sb.append("\n");
            }

            sb.append("\n");

            for (Postcode postcode : server.getPostcodes()) {
                sb.append("POSTCODE:");
                sb.append(postcode.getCode());
                sb.append(":");
                sb.append(postcode.getDistance());
                sb.append("\n");
            }

            for (User user : server.getUsers()) {
                sb.append("USER:");
                sb.append(user.getName());
                sb.append(":");
                sb.append(user.getPassword());
                sb.append(":");
                sb.append(user.getAddress());
                sb.append(":");
                sb.append(user.getPostcode());
                sb.append("\n");
            }

            sb.append("\n");

            for (Order order : server.getOrders()) {
                sb.append("ORDER:");
                sb.append(order.getUser().getName());
                sb.append(":");
                for (Map.Entry<Dish, Number> entry : order.getItems().entrySet()) {
                    Dish dish = entry.getKey();
                    Number amount = entry.getValue();
                    sb.append(amount);
                    sb.append(" * ");
                    sb.append(dish.getName());
                    sb.append(",");
                }
                sb.append("\n");
            }

            sb.append("\n");

            for (Map.Entry<Dish, Number> entry : server.getDishStockLevels().entrySet()) {
                Dish dish = entry.getKey();
                Number amount = entry.getValue();
                sb.append("STOCK:");
                sb.append(dish.getName());
                sb.append(":");
                sb.append(amount);
                sb.append("\n");
            }

            for (Map.Entry<Ingredient, Number> entry : server.getIngredientStockLevels().entrySet()) {
                Ingredient ingredient = entry.getKey();
                Number amount = entry.getValue();
                sb.append("STOCK:");
                sb.append(ingredient.getName());
                sb.append(":");
                sb.append(amount);
                sb.append("\n");
            }

            sb.append("\n");

            for (Staff staff : server.getStaff()) {
                sb.append("STAFF:");
                sb.append(staff.getName());
                sb.append("\n");
            }

            sb.append("\n");

            for (Drone drone : server.getDrones()) {
                sb.append("DRONE:");
                sb.append(drone.getSpeed());
            }

            writer.append(sb);
            writer.close();
        } catch (IOException e) {
            System.err.println("Failed to write to backup file");
        }
    }

}
