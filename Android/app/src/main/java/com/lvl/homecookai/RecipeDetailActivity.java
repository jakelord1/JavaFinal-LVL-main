package com.lvl.homecookai;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
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
    private MaterialButton saveButton;
    private MaterialButton backButton;
    private RecipeDao recipeDao;
    private Recipe currentRecipe;

    private MethodsToApi api;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        recipeImage = findViewById(R.id.recipe_image);
        recipeTitleText = findViewById(R.id.recipe_title);
        ingredientsText = findViewById(R.id.ingredients_text);
        timeText = findViewById(R.id.time_text);
        instructionsText = findViewById(R.id.instructions_text);
        saveButton = findViewById(R.id.save_recipe_button);
        backButton = findViewById(R.id.back_button);
        recipeDao = AppDatabase.getDatabase(this).recipeDao();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.recipe_detail_root), (v, insets) -> {
            int top = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top;
            int bottom = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom;
            v.setPadding(v.getPaddingLeft(), top, v.getPaddingRight(), bottom);
            return insets;
        });
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

        if (saveButton != null) {
            saveButton.setOnClickListener(v -> saveRecipeToHistory());
        }
        if (backButton != null) {
            backButton.setOnClickListener(v -> finish());
        }
    }

    private void saveRecipeToHistory() {
        if (currentRecipe == null) {
            Toast.makeText(this, "Рецепт ещё не загружен", Toast.LENGTH_SHORT).show();
            return;
        }
        new Thread(() -> {
            recipeDao.insertRecipe(currentRecipe);
            runOnUiThread(() ->
                    Toast.makeText(this, getString(R.string.save_recipe), Toast.LENGTH_SHORT).show());
        }).start();
    }
}
