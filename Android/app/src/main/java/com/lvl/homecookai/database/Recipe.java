package com.lvl.homecookai.database;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.List;

@Entity(tableName = "recipes")
public class Recipe {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String dish_name;
    private int cook_time;
    private String dish_shorttext;
    private String recipe_fulltext;
    private String category;
    private String image;

    @Ignore
    private List<Recipe_Position> recipe_positions;

    public Recipe() {
    }

    public Recipe(String dish_name, int cook_time, String dish_shorttext,
                  String recipe_fulltext, String category, String image) {
        this.dish_name = dish_name;
        this.cook_time = cook_time;
        this.dish_shorttext = dish_shorttext;
        this.recipe_fulltext = recipe_fulltext;
        this.category = category;
        this.image = image;
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

    public int getCook_time() {
        return cook_time;
    }

    public void setCook_time(int cook_time) {
        this.cook_time = cook_time;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<Recipe_Position> getRecipe_positions() {
        return recipe_positions;
    }

    public void setRecipe_positions(List<Recipe_Position> recipe_positions) {
        this.recipe_positions = recipe_positions;
    }
}