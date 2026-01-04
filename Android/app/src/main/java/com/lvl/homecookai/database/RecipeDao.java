package com.lvl.homecookai.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface RecipeDao {

    @Query("SELECT * FROM recipes ORDER BY id")
    List<Recipe> getAllRecipes();

    @Query("SELECT * FROM recipes ORDER BY id")
    LiveData<List<Recipe>> getAllRecipesLive();

    @Query("SELECT * FROM recipes ORDER BY id LIMIT :limit OFFSET :offset")
    List<Recipe> getRecipesPaged(int limit, int offset);

    @Query("SELECT * FROM recipes WHERE id = :recipeId")
    Recipe getRecipeById(long recipeId);

    @Query("SELECT * FROM recipes WHERE dish_name LIKE '%' || :name || '%'")
    List<Recipe> searchRecipesByName(String name);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertRecipe(Recipe recipe);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertRecipes(List<Recipe> recipes);

    @Update
    void updateRecipe(Recipe recipe);

    @Delete
    void deleteRecipe(Recipe recipe);

    @Query("DELETE FROM recipes")
    void deleteAllRecipes();
}
