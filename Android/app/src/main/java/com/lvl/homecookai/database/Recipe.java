package com.lvl.homecookai.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "recipes")
public class Recipe {
    @PrimaryKey(autoGenerate = true)
    public long id;
    
    public String name;
    public String ingredients;
    public String time;
    public String instructions;
    public int imageResId;
    
    public Recipe() {
    }
    
    public Recipe(String name, String ingredients, String time, String instructions, int imageResId) {
        this.name = name;
        this.ingredients = ingredients;
        this.time = time;
        this.instructions = instructions;
        this.imageResId = imageResId;
    }
}

