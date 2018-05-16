package common;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a sushi dish which can be ordered by a client from the Sushi Delivery System.
 * @author Jack Corbett
 */
public class Dish extends Model {

    private String description;
    private Double price;
    private Integer restockThreshold;
    private Integer restockAmount;
    private Map<Ingredient, Number> recipe;
    int noRestocking;

    /**
     * Set the dish properties.
     * @param name Name
     * @param description Description of the dish to explain it to the user
     * @param price Price the user must pay for the dish
     * @param restockThreshold Restocking is triggered when the stock level is below this
     * @param restockAmount Sets the amount stock will be replenished to above the threshold
     */
    public Dish(String name, String description, Double price, Integer restockThreshold, Integer restockAmount) {
        super.setName(name);
        this.description = description;
        this.price = price;
        this.restockThreshold = restockThreshold;
        this.restockAmount = restockAmount;
        recipe = new HashMap<>();
        noRestocking = 0;
    }

    /**
     * @return Name of the dish
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * @return Dish description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return Price of the dish
     */
    public Number getPrice() {
        return price;
    }

    /**
     * @return Restock threshold that restocking is triggered at
     */
    public Number getRestockThreshold() {
        return restockThreshold;
    }

    /**
     * @return Restock amount that determines the stock level restocked to above the threshold
     */
    public Number getRestockAmount() {
        return restockAmount;
    }

    /**
     * @return A map of ingredients with their corresponding quantities required to prepare the dish
     */
    public Map<Ingredient, Number> getRecipe() {
        return recipe;
    }

    /**
     * @param name The new dish name
     */
    @Override
    public void setName(String name) {
        super.setName(name);
    }

    /**
     * @param restockAmount The restock amount
     */
    public void setRestockAmount(Integer restockAmount) {
        notifyUpdate("restockAmount", this.restockAmount, restockAmount);
        this.restockAmount = restockAmount;
    }

    /**
     * @param restockThreshold The restock threshold
     */
    public void setRestockThreshold(Integer restockThreshold) {
        notifyUpdate("restockThreshold", this.restockThreshold, restockThreshold);
        this.restockThreshold = restockThreshold;
    }

    // RECIPE SPECIFIC
    /**
     * @param recipe The dishes recipe
     */
    public void setRecipe(Map<Ingredient, Number> recipe) {
        notifyUpdate("recipe", this.recipe, recipe);
        this.recipe = recipe;
    }

    /**
     * @param ingredient The ingredient to be added to the recipe
     * @param quantity The number of that ingredient required
     */
    public void addIngredient(Ingredient ingredient, Number quantity) {
        Map<Ingredient, Number> oldRecipe = recipe;
        recipe.put(ingredient, quantity);
        notifyUpdate("recipe", oldRecipe, recipe);
    }

    /**
     * @param ingredient The ingredient no longer required in the recipe
     */
    public void removeIngredient(Ingredient ingredient) {
        Map<Ingredient, Number> oldRecipe = recipe;
        recipe.remove(ingredient);
        notifyUpdate("recipe", oldRecipe, recipe);
    }
}
