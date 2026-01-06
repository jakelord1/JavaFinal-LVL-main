package com.lvl.homecookai;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.lvl.homecookai.ApiSetup.ApiAccess;
import com.lvl.homecookai.ApiSetup.MethodsToApi;
import com.lvl.homecookai.database.Ingredient;
import com.lvl.homecookai.database.Recipe;
import com.lvl.homecookai.database.Recipe_Position;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RecipeDetailActivity extends AppCompatActivity {

    private ImageView recipeImage;
    private TextView recipeTitleText;
    private TextView ingredientsText;
    private TextView timeText;
    private TextView instructionsText;

    private MethodsToApi api;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        recipeImage = findViewById(R.id.recipe_image);
        recipeTitleText = findViewById(R.id.recipe_title);
        ingredientsText = findViewById(R.id.ingredients_text);
        timeText = findViewById(R.id.time_text);
        instructionsText = findViewById(R.id.instructions_text);
        try {


        int recipeId = getIntent().getIntExtra("recipe_id", 0);

        api = ApiAccess.getClient().create(MethodsToApi.class);

        api.getRecipe("id", recipeId).enqueue(new Callback<Recipe>() {
            @Override
            public void onResponse(Call<Recipe> call, Response<Recipe> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Recipe recipe = response.body();

                    recipeTitleText.setText(recipe.getDish_name());
                    timeText.setText(recipe.getCook_time() + " мин");
                    instructionsText.setText(recipe.getRecipe_fulltext());

                    StringBuilder ingredientsBuilder = new StringBuilder();
                    if (recipe.getRecipe_positions() != null) {
                        for (Recipe_Position pos : recipe.getRecipe_positions()) {
                            Ingredient ing = pos.getIngredient();
                            if (ing != null) {
                                ingredientsBuilder.append("- ")
                                        .append(ing.getName())
                                        .append(" ")
                                        .append(pos.getAmount())
                                        .append(" ")
                                        .append(pos.getUnit() != null ? pos.getUnit() : "")
                                        .append("\n");
                            }
                        }
                    }
                    ingredientsText.setText(ingredientsBuilder.toString());

                    Glide.with(RecipeDetailActivity.this)
                            .load(recipe.getImage())
                            .placeholder(R.drawable.ic_launcher_foreground)
                            .into(recipeImage);

                    // ActionBar
                    if (getSupportActionBar() != null) {
                        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                        getSupportActionBar().setTitle(recipe.getDish_name());
                    }
                }
            }

            @Override
            public void onFailure(Call<Recipe> call, Throwable t) {
                Toast.makeText(RecipeDetailActivity.this,
                        "Ошибка загрузки рецепта", Toast.LENGTH_SHORT).show();
                Log.e("API_ERROR", "Ошибка при запросе рецепта: " + t.getMessage(), t);
            }
        });
    }
        catch (Exception exception) {
            Log.e("Debug",exception.getMessage(), exception);
        }
    }
}
