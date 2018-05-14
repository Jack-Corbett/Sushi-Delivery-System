package common;

import server.Server;

import java.io.*;
import java.util.HashMap;

public class Configuration {

    public Configuration(String filename, Server server) {
        try {
            InputStream inputStream = this.getClass().getResourceAsStream(filename);

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader br = new BufferedReader(inputStreamReader);

                String string;
                while ((string = br.readLine()) != null) {
                    if (string.equals("")) continue;
                    String[] strings = string.split(":");

                    switch (strings[0]) {
                        case "SUPPLIER":

                            server.addSupplier(strings[1], Integer.parseInt(strings[2]));

                            break;
                        case "INGREDIENT":

                            Supplier ingSupplier = null;
                            for (Supplier supplier : server.getSuppliers()) {
                                if (supplier.getName().equals(strings[3])) ingSupplier = supplier;
                            }
                            server.addIngredient(strings[1], strings[2], ingSupplier,
                                    Integer.parseInt(strings[4]), Integer.parseInt(strings[5]));

                            break;
                        case "DISH":

                            Dish dish = server.addDish(strings[1], strings[2],
                                    Double.parseDouble(strings[3]),
                                    Integer.parseInt(strings[4]),
                                    Integer.parseInt(strings[5]));

                            // Split into pairs of quantities and ingredients
                            String[] recipeElements = strings[6].split(",");

                            for (String recipeElement : recipeElements) {
                                // Split into ingredient and quantity
                                String[] element = recipeElement.split(" \\* ");

                                // Pointer to the ingredient
                                Ingredient recipeIngredient = null;

                                for (Ingredient ingredient : server.getIngredients()) {
                                    if (ingredient.getName().equals(element[1])) recipeIngredient = ingredient;
                                }

                                server.addIngredientToDish(dish, recipeIngredient,
                                        Double.parseDouble(element[0]));
                            }

                            break;
                        case "POSTCODE":

                            server.addPostcode(strings[1], Integer.parseInt(strings[2]));

                            break;
                        case "USER":

                            Postcode userPostcode = null;

                            for (Postcode postcode : server.getPostcodes()) {
                                if (postcode.getName().equals(strings[4])) userPostcode = postcode;
                            }

                            server.users.add(new User(strings[1], strings[2], strings[3], userPostcode));

                            break;
                        case "ORDER":

                            HashMap<Dish, Number> order = new HashMap<>();

                            String[] orderElements = strings[2].split(",");

                            for (String orderElement : orderElements) {
                                String[] element = orderElement.split(" \\* ");

                                Dish orderDish = null;

                                for (Dish dishO : server.getDishes()) {
                                    if (dishO.getName().equals(element[1])) orderDish = dishO;
                                }

                                order.put(orderDish, Integer.parseInt(element[0]));
                            }

                            User orderUser = null;

                            for (User user : server.users) {
                                if (user.getName().equals(strings[1])) orderUser = user;
                            }

                            server.orderQueue.add(new Order(orderUser, order));

                            break;
                        case "STOCK":

                            Dish dishForStock = null;
                            Ingredient ingredientForStock = null;

                            for (Dish dishStock : server.getDishes()) {
                                if (dishStock.getName().equals(strings[1])) dishForStock = dishStock;
                            }

                            for (Ingredient ingredientStock : server.getIngredients()) {
                                if (ingredientStock.getName().equals(strings[1])) ingredientForStock = ingredientStock;
                            }

                            if (dishForStock != null) server.setStock(dishForStock, Integer.parseInt(strings[2]));
                            if (ingredientForStock != null) server.setStock(ingredientForStock,
                                    Integer.parseInt(strings[2]));

                            break;
                        case "STAFF":

                            server.addStaff(strings[1]);

                            break;
                        case "DRONE":

                            server.addDrone(Integer.parseInt(strings[1]));

                            break;
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to read from config file");
        }
    }
}
