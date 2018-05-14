package common;

import java.util.HashMap;
import java.util.Map;

public class Dish extends Model {

    private String description;
    private Double price;
    private Integer restockThreshold;
    private Integer restockAmount;
    private Map<Ingredient, Number> recipe;

    public Dish(String name, String description, Double price, Integer restockThreshold, Integer restockAmount) {
        super.setName(name);
        this.description = description;
        this.price = price;
        this.restockThreshold = restockThreshold;
        this.restockAmount = restockAmount;
        recipe = new HashMap<>();
    }

    @Override
    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Number getPrice() {
        return price;
    }

    public Number getRestockThreshold() {
        return restockThreshold;
    }

    public Number getRestockAmount() {
        return restockAmount;
    }

    public Map<Ingredient, Number> getRecipe() {
        return recipe;
    }

    @Override
    public void setName(String name) {
        super.setName(name);
    }

    public void setDescription(String description) {
        notifyUpdate("description", this.description, description);
        this.description = description;
    }

    public void setPrice(Double price) {
        notifyUpdate("price", this.price, price);
        this.price = price;
    }

    public void setRestockAmount(Integer restockAmount) {
        notifyUpdate("restockAmount", this.restockAmount, restockAmount);
        this.restockAmount = restockAmount;
    }

    public void setRestockThreshold(Integer restockThreshold) {
        notifyUpdate("restockThreshold", this.restockThreshold, restockThreshold);
        this.restockThreshold = restockThreshold;
    }

    // RECIPE

    public void setRecipe(Map<Ingredient, Number> recipe) {
        notifyUpdate("recipe", this.recipe, recipe);
        this.recipe = recipe;
    }

    public void addIngredient(Ingredient ingredient, Number quantity) {
        recipe.put(ingredient, quantity);
    }

    public void removeIngredient(Ingredient ingredient) {
        recipe.remove(ingredient);
    }
}
