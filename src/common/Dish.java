package common;

import java.util.concurrent.ConcurrentHashMap;

public class Dish extends Model {

    private String description;
    private Double price;
    private ConcurrentHashMap<Ingredient, Integer> recipe;
    private Integer restockAmount;
    public Integer amount;

    Dish(String name, String description, Double price, ConcurrentHashMap<Ingredient, Integer> recipe, Integer startingAmount,
         Integer restockAmount) {
        super.setName(name);
        this.description = description;
        this.price = price;
        this.recipe = recipe;

        if (startingAmount < 0) throw new IllegalArgumentException("Cannot set the initial amount level less than 0");
        else this.amount = startingAmount;

        if (restockAmount < 0) throw new IllegalArgumentException("Cannot set the restock value less than 0");
        else this.restockAmount = restockAmount;
    }

    @Override
    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Double getPrice() {
        return price;
    }

    public ConcurrentHashMap<Ingredient, Integer> getRecipe() {
        return recipe;
    }

    public Integer getAmount() {
        return amount;
    }

    public Integer getRestockAmount() {
        return restockAmount;
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

    public void setRecipe(ConcurrentHashMap<Ingredient, Integer> recipe) {
        notifyUpdate("recipe", this.recipe, recipe);
        this.recipe = recipe;
    }

    public void setRestockAmount(Integer restockAmount) {
        notifyUpdate("restockAmount", this.restockAmount, restockAmount);
        this.restockAmount = restockAmount;
    }
}
