package com.lvl.homecookai.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface RecipeDao {
    @Query("SELECT * FROM recipes ORDER BY id")
    List<Recipe> getAllRecipes();
    
    @Query("SELECT * FROM recipes ORDER BY id LIMIT :limit OFFSET :offset")
    List<Recipe> getRecipesPaged(int limit, int offset);
    
    @Query("SELECT * FROM recipes WHERE id = :recipeId")
    Recipe getRecipeById(long recipeId);
    
    @Insert
    void insertRecipe(Recipe recipe);
    
    @Insert
    void insertRecipes(List<Recipe> recipes);
}
