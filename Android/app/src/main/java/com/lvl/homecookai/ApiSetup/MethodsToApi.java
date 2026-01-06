package com.lvl.homecookai.ApiSetup;

import com.lvl.homecookai.database.Ingredient;
import com.lvl.homecookai.database.Recipe;
import com.lvl.homecookai.database.Recipe_Position;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface MethodsToApi {
    @GET("recipes")
    Call<List<Recipe>> getAllRecipes(@Query("type") String type, @Query("page") int page);
    @GET("recipes")
    Call<Recipe> getRecipe(@Query("type") String type, @Query("id") int recipeId);
    @POST("recipes")
    Call<List<Recipe>> searchRecipes(@Query("action") String action, @Body List<Recipe_Position> positions);
    @GET("ingredients")
    Call<List<Ingredient>> getAllIngredients();
}
