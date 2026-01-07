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
public interface IngredientDao {

    @Query("SELECT * FROM ingredient ORDER BY id")
    List<Ingredient> getAllIngredients();

    @Query("SELECT * FROM ingredient ORDER BY id")
    LiveData<List<Ingredient>> getAllIngredientsLive();

    @Query("SELECT * FROM ingredient WHERE id = :ingredientId")
    Ingredient getIngredientById(int ingredientId);

    @Query("SELECT * FROM ingredient WHERE name LIKE '%' || :name || '%'")
    List<Ingredient> searchIngredientsByName(String name);

    @Query("SELECT * FROM ingredient WHERE category = :category")
    List<Ingredient> getIngredientsByCategory(String category);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertIngredient(Ingredient ingredient);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertIngredients(List<Ingredient> ingredients);

    @Update
    void updateIngredient(Ingredient ingredient);

    @Delete
    void deleteIngredient(Ingredient ingredient);

    @Query("DELETE FROM ingredient")
    void deleteAllIngredients();
}
