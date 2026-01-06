package com.lvl.homecookai;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lvl.homecookai.database.AppDatabase;
import com.lvl.homecookai.database.Recipe;
import com.lvl.homecookai.database.RecipeDao;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class MatchResultsActivity extends AppCompatActivity {

    public static final String EXTRA_INGREDIENTS = "extra_ingredients";
    public static final String EXTRA_RECIPES = "extra_recipes";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_results);

        TextView detectedText = findViewById(R.id.detected_ingredients);
        TextView matchCount = findViewById(R.id.match_count);
        RecyclerView recyclerView = findViewById(R.id.match_recycler_view);
        View emptyState = findViewById(R.id.empty_state);
        View emptyBackButton = findViewById(R.id.empty_back_button);
        View profileIcon = findViewById(R.id.profile_icon);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        String json = getIntent().getStringExtra(EXTRA_RECIPES);
        List<Recipe> recipes = new ArrayList<>();

        if (json != null) {
            Type type = new TypeToken<List<Recipe>>(){}.getType();
            recipes = new Gson().fromJson(json, type);
        }

        MatchResultsAdapter adapter = new MatchResultsAdapter(this::openRecipeDetail);
        recyclerView.setAdapter(adapter);
        adapter.setItems(recipes);

        matchCount.setText(getString(R.string.match_count_format, recipes.size()));

        boolean isEmpty = recipes.isEmpty();
        recyclerView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        if (emptyState != null) {
            emptyState.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        }

        if (emptyBackButton != null) {
            emptyBackButton.setOnClickListener(v -> finish());
        }
        if (profileIcon != null) {
            profileIcon.setOnClickListener(v ->
                    startActivity(new Intent(this, MainActivity.class)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP)
                            .putExtra(MainActivity.EXTRA_START_TAB, R.id.nav_profile))
            );
        }
    }

    private void openRecipeDetail(Recipe recipe) {
        Intent intent = new Intent(this, RecipeDetailActivity.class);
        intent.putExtra("recipe_id", recipe.getId());
        startActivity(intent);
    }

    private String joinIngredients(List<String> ingredients) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < ingredients.size(); i++) {
            if (i > 0) {
                builder.append(", ");
            }
            builder.append(ingredients.get(i));
        }
        return builder.toString();
    }
}
