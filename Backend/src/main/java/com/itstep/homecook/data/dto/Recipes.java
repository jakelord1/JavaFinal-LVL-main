package com.itstep.homecook.data.dto;

import java.util.List;
import java.util.ArrayList;

public class Recipes {
    private List<Recipe_Positions> recipe_positions;
    private int id;
    private String dish_name;
    private int cook_time;
    private String dish_shorttext;
    private String recipe_fulltext;
    private String category;
    private String image;

    public Recipes(List<Recipe_Positions> recipe_positions, int id, String dish_name, int cook_time, String dish_shorttext, String recipe_fulltext, String categories, String image) {
        this.recipe_positions = recipe_positions;
        this.id = id;
        this.dish_name = dish_name;
        this.cook_time = cook_time;
        this.dish_shorttext = dish_shorttext;
        this.recipe_fulltext = recipe_fulltext;
        this.category = categories;
        this.image = image;
    }

     public Recipes() {
     }

    public static Recipes fromResultSet(java.sql.ResultSet rs) throws java.sql.SQLException {
        
        Recipes recipe = new Recipes(
            null,
            rs.getInt("id"),
            rs.getString("dish_name"),
            rs.getInt("cook_time"),
            rs.getString("dish_shorttext"),
            rs.getString("recipe_fulltext"),
            rs.getString("category"),
            rs.getString("image")
        );
        List<Recipe_Positions> recipe_positions = new ArrayList<>();
        do {
            recipe_positions.add(Recipe_Positions.fromResultSet(rs));
        } while (rs.next());
        recipe.setRecipe_positions(recipe_positions);

        return recipe;
     }

    public List<Recipe_Positions> getRecipe_positions() {
        return recipe_positions;
    }
    public void setRecipe_positions(List<Recipe_Positions> recipe_positions) {
        this.recipe_positions = recipe_positions;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getDish_name() {
        return dish_name;
    }
    public void setDish_name(String dish_name) {
        this.dish_name = dish_name;
    }
    public String getDish_shorttext() {
        return dish_shorttext;
    }
    public void setDish_shorttext(String dish_shorttext) {
        this.dish_shorttext = dish_shorttext;
    }
    public String getRecipe_fulltext() {
        return recipe_fulltext;
    }
    public void setRecipe_fulltext(String recipe_fulltext) {
        this.recipe_fulltext = recipe_fulltext;
    }
    public String getCategories() {
        return category;
    }
    public void setCategories(String categories) {
        this.category = categories;
    }
    public String getImage() {
        return image;
    }
    public void setImage(String image) {
        this.image = image;
    }
    public int getCook_time() {
        return cook_time;
    }
    public void setCook_time(int cook_time) {
        this.cook_time = cook_time;
    }
}
