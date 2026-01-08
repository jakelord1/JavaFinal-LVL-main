package com.lvl.homecookai;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.lvl.homecookai.ApiSetup.ApiAccess;
import com.lvl.homecookai.ApiSetup.MethodsToApi;
import com.lvl.homecookai.database.AppDatabase;
import com.lvl.homecookai.database.Ingredient;
import com.lvl.homecookai.database.Recipe;
import com.lvl.homecookai.database.RecipeDao;
import com.lvl.homecookai.database.Recipe_Position;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecipeDetailActivity extends AppCompatActivity {

    private ImageView recipeImage;
    private TextView recipeTitleText;
    private TextView ingredientsText;
    private TextView timeText;
    private TextView instructionsText;
    private ImageButton backButton;
    private ImageButton favoriteButton;
    private RecipeDao recipeDao;
    private Recipe currentRecipe;
    private boolean isSaved;

    private MethodsToApi api;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        getWindow().setStatusBarColor(Color.WHITE);
        WindowInsetsControllerCompat controller =
                WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        if (controller != null) {
            controller.setAppearanceLightStatusBars(true);
        }

        recipeImage = findViewById(R.id.recipe_image);
        recipeTitleText = findViewById(R.id.recipe_title);
        ingredientsText = findViewById(R.id.ingredients_text);
        timeText = findViewById(R.id.time_text);
        instructionsText = findViewById(R.id.instructions_text);
        backButton = findViewById(R.id.back_button);
        favoriteButton = findViewById(R.id.favorite_button);
        recipeDao = AppDatabase.getDatabase(this).recipeDao();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.recipe_detail_root), (v, insets) -> {
            int bottom = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom;
            v.setPadding(v.getPaddingLeft(), 0, v.getPaddingRight(), bottom);
            return insets;
        });
        View header = findViewById(R.id.recipe_header);
        if (header != null) {
            int baseTop = header.getPaddingTop();
            ViewCompat.setOnApplyWindowInsetsListener(header, (v, insets) -> {
                int top = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top;
                v.setPadding(v.getPaddingLeft(), baseTop + top, v.getPaddingRight(), v.getPaddingBottom());
                return insets;
            });
        }
        try {


        int recipeId = getIntent().getIntExtra("recipe_id", 0);

        api = ApiAccess.getClient().create(MethodsToApi.class);

        api.getRecipeById("id", recipeId).enqueue(new Callback<Recipe>() {
            @Override
            public void onResponse(Call<Recipe> call, Response<Recipe> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Recipe recipe = response.body();
                    currentRecipe = recipe;

                    recipeTitleText.setText(recipe.getDish_name());
                    timeText.setText(recipe.getCook_time() + " " + getString(R.string.minutes_short));
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
                    loadSavedState(recipe.getId());

                }
            }

            @Override
            public void onFailure(Call<Recipe> call, Throwable t) {
                Toast.makeText(RecipeDetailActivity.this,
                        getString(R.string.error_loading_recipe), Toast.LENGTH_SHORT).show();
                Log.e("API_ERROR", "Failed to load recipe: " + t.getMessage(), t);
            }
        });
    }
        catch (Exception exception) {
            Log.e("Debug",exception.getMessage(), exception);
        }

        if (backButton != null) {
            backButton.setOnClickListener(v -> finish());
        }
        if (favoriteButton != null) {
            favoriteButton.setOnClickListener(v -> toggleSaved());
        }
    }
    private void toggleSaved() {
        if (currentRecipe == null) {
            Toast.makeText(this, getString(R.string.recipe_not_loaded), Toast.LENGTH_SHORT).show();
            return;
        }
        new Thread(() -> {
            if (isSaved) {
                recipeDao.deleteRecipe(currentRecipe);
            } else {
                recipeDao.insertRecipe(currentRecipe);
            }
            isSaved = !isSaved;
            runOnUiThread(() -> {
                updateFavoriteUi();
                int message = isSaved ? R.string.recipe_saved : R.string.recipe_removed;
                Toast.makeText(this, getString(message), Toast.LENGTH_SHORT).show();
            });
        }).start();
    }

    private void loadSavedState(long recipeId) {
        new Thread(() -> {
            isSaved = recipeDao.getRecipeById(recipeId) != null;
            runOnUiThread(this::updateFavoriteUi);
        }).start();
    }

    private void updateFavoriteUi() {
        if (favoriteButton == null) {
            return;
        }
        int icon = isSaved
                ? R.drawable.ic_star_filled
                : R.drawable.ic_star_outline;
        favoriteButton.setImageResource(icon);
    }
}







