package com.itstep.homecook.data.dto;

public class Recipe_Positions {
    private int id;
    private Ingredients ingredient;
    private Recipes recipe;
    private int amount;
    private String unit;
    private String notes;

    private int ingredient_id;

    public Recipe_Positions(
        int id,
        Ingredients ingredient,
        Recipes recipe,
        int amount,
        String unit,
        String notes,
        int ingredient_id
    ) {
        this.id = id;
        this.ingredient = ingredient;
        this.recipe = recipe;
        this.amount = amount;
        this.unit = unit;
        this.notes = notes;
        this.ingredient_id = ingredient_id;
    }

    public static Recipe_Positions fromResultSet(java.sql.ResultSet rs) throws java.sql.SQLException {
        Recipe_Positions rp = new Recipe_Positions(
            rs.getInt("id"),    
            Ingredients.fromResultSet(rs),
            null,
            rs.getInt("amount"),
            rs.getString("unit"),
            rs.getString("notes"),
            rs.getInt("ingredient_id")
        );
        return rp;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public Ingredients getIngredient() {
        return ingredient;
    }
    public void setIngredient(Ingredients ingredient) {
        this.ingredient = ingredient;
    }
    public Recipes getRecipe() {
        return recipe;
    }
    public void setRecipe(Recipes recipe) {
        this.recipe = recipe;
    }
    public int getAmount() {
        return amount;
    }
    public void setAmount(int amount) {
        this.amount = amount;
    }
    public String getUnit() {
        return unit;
    }
    public void setUnit(String unit) {
        this.unit = unit;
    }
    public String getNotes() {
        return notes;
    }
    public void setNotes(String notes) {
        this.notes = notes;
    }
    public int getIngredient_id() {
        return ingredient_id;
    }
    public void setIngredient_id(int ingredient_id) {
        this.ingredient_id = ingredient_id;
    }
}
