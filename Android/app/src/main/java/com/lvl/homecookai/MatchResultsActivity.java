package com.lvl.homecookai;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lvl.homecookai.database.AppDatabase;
import com.lvl.homecookai.database.Recipe;
import com.lvl.homecookai.database.RecipeDao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class MatchResultsActivity extends AppCompatActivity {

    public static final String EXTRA_INGREDIENTS = "extra_ingredients";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_results);

        TextView detectedText = findViewById(R.id.detected_ingredients);
        TextView matchCount = findViewById(R.id.match_count);
        RecyclerView recyclerView = findViewById(R.id.match_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ArrayList<String> detected = getIntent().getStringArrayListExtra(EXTRA_INGREDIENTS);
        if (detected == null) {
            detected = new ArrayList<>();
        }

        if (detected.isEmpty()) {
            detectedText.setText(getString(R.string.no_ingredients_detected));
        } else {
            detectedText.setText(joinIngredients(detected));
        }

        RecipeDao recipeDao = AppDatabase.getDatabase(this).recipeDao();
        List<RecipeMatch> matches = buildMatches(recipeDao.getAllRecipes(), detected);

        MatchResultsAdapter adapter = new MatchResultsAdapter(match -> openRecipeDetail(match.recipe));
        recyclerView.setAdapter(adapter);
        adapter.setItems(matches);

        matchCount.setText(getString(R.string.match_count_format, matches.size()));
    }

    private void openRecipeDetail(Recipe recipe) {
        Intent intent = new Intent(this, RecipeDetailActivity.class);
        intent.putExtra(RecipeDetailActivity.EXTRA_RECIPE_NAME, recipe.name);
        intent.putExtra(RecipeDetailActivity.EXTRA_RECIPE_INGREDIENTS, recipe.ingredients);
        intent.putExtra(RecipeDetailActivity.EXTRA_RECIPE_TIME, recipe.time);
        intent.putExtra(RecipeDetailActivity.EXTRA_RECIPE_INSTRUCTIONS, recipe.instructions);
        intent.putExtra(RecipeDetailActivity.EXTRA_RECIPE_IMAGE, recipe.imageResId);
        startActivity(intent);
    }

    private List<RecipeMatch> buildMatches(List<Recipe> recipes, List<String> detected) {
        Set<String> detectedSet = new HashSet<>();
        for (String item : detected) {
            String normalized = normalizeIngredient(item);
            if (!normalized.isEmpty()) {
                detectedSet.add(normalized);
            }
        }

        List<RecipeMatch> results = new ArrayList<>();
        for (Recipe recipe : recipes) {
            List<String> ingredients = splitIngredients(recipe.ingredients);
            int total = ingredients.size();
            int matched = 0;
            for (String ingredient : ingredients) {
                if (detectedSet.contains(ingredient)) {
                    matched++;
                }
            }
            int percent = total == 0 ? 0 : Math.round((matched * 100f) / total);
            results.add(new RecipeMatch(recipe, percent));
        }

        Collections.sort(results, (a, b) -> {
            int cmp = Integer.compare(b.matchPercent, a.matchPercent);
            if (cmp != 0) {
                return cmp;
            }
            return a.recipe.name.compareToIgnoreCase(b.recipe.name);
        });

        return results;
    }

    private List<String> splitIngredients(String ingredients) {
        if (ingredients == null) {
            return new ArrayList<>();
        }
        String[] lines = ingredients.split("\\r?\\n");
        List<String> result = new ArrayList<>();
        for (String line : lines) {
            String cleaned = line.replace("-", "").trim();
            if (!cleaned.isEmpty()) {
                result.add(normalizeIngredient(cleaned));
            }
        }
        return result;
    }

    private String normalizeIngredient(String value) {
        return value == null ? "" : value.toLowerCase(Locale.US).trim();
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
