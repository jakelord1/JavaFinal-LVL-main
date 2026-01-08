package com.lvl.homecookai;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.lvl.homecookai.ApiSetup.ApiAccess;
import com.lvl.homecookai.ApiSetup.MethodsToApi;
import com.lvl.homecookai.database.AppDatabase;
import com.lvl.homecookai.database.Ingredient;
import com.lvl.homecookai.database.IngredientDao;
import com.lvl.homecookai.database.Recipe;
import com.lvl.homecookai.database.Recipe_Position;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IngredientConfirmActivity extends AppCompatActivity {

    public static final String EXTRA_DETECTED_INGREDIENTS = "extra_detected_ingredients";
    public static final String EXTRA_PREFILL_INGREDIENTS = "extra_prefill_ingredients";
    private static final String TAG = "IngredientConfirm";

    private IngredientAdapter ingredientAdapter;
    private SelectedIngredientAdapter selectedAdapter;
    private IngredientDao ingredientDao;
    
    private LinearLayout mainContent;
    private LinearLayout resultsContainer;
    private LinearLayout loadingView;
    private LinearLayout noIngredientsView;
    private RecyclerView ingredientsList;
    private RecyclerView selectedList;
    private RecyclerView resultsList;
    private EditText searchInput;
    private MaterialButton confirmChoicesButton;
    private TextView selectedCountText;
    private TextView resultsStatusText;
    private View resultsLoadingView;
    private View resultsEmptyView;
    private String currentQuery = "";

    private List<Ingredient> allIngredients = new ArrayList<>();
    private boolean ingredientsLoaded = false;
    private MatchResultsAdapter resultsAdapter;
    private List<String> prefillIngredients = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredient_confirm);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.ingredient_confirm_root), (v, insets) -> {
            int top = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top;
            int bottom = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom;
            v.setPadding(v.getPaddingLeft(), top, v.getPaddingRight(), bottom);
            return insets;
        });

        List<String> prefill = getIntent().getStringArrayListExtra(EXTRA_PREFILL_INGREDIENTS);
        if (prefill != null) {
            prefillIngredients.addAll(prefill);
        }

        AppDatabase database = AppDatabase.getDatabase(this);
        ingredientDao = database.ingredientDao();

        initializeViews();
        
        loadIngredientsFromAPI();
    }

    private void initializeViews() {
        mainContent = findViewById(R.id.main_content);
        resultsContainer = findViewById(R.id.results_container);

        ImageButton backButton = findViewById(R.id.confirm_back_button);
        if (backButton != null) {
            backButton.setOnClickListener(v -> handleBackPressed());
        }

        searchInput = findViewById(R.id.search_ingredients_input);
        loadingView = findViewById(R.id.loading_view);
        noIngredientsView = findViewById(R.id.no_ingredients_view);
        ingredientsList = findViewById(R.id.ingredients_list);

        if (searchInput != null) {
            searchInput.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    currentQuery = s.toString();
                    filterIngredients(currentQuery);
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });
        }

        if (ingredientsList != null) {
            ingredientsList.setLayoutManager(new LinearLayoutManager(this));
            ingredientAdapter = new IngredientAdapter(selected -> {
                updateSelectedList();
            });
            ingredientsList.setAdapter(ingredientAdapter);
        }

        selectedList = findViewById(R.id.selected_ingredients_list);
        selectedCountText = findViewById(R.id.selected_count);

        if (selectedList != null) {
            selectedList.setLayoutManager(new LinearLayoutManager(this));
            selectedAdapter = new SelectedIngredientAdapter(new SelectedIngredientAdapter.OnQuantityActionListener() {
                @Override
                public void onRemoveClicked(Ingredient ingredient) {
                    ingredientAdapter.removeIngredient(ingredient);
                    updateSelectedList();
                }

                @Override
                public void onIncrementClicked(Ingredient ingredient) {
                    ingredientAdapter.incrementIngredient(ingredient);
                    updateSelectedList();
                }

                @Override
                public void onDecrementClicked(Ingredient ingredient) {
                    ingredientAdapter.decrementIngredient(ingredient);
                    updateSelectedList();
                }
            });
            selectedList.setAdapter(selectedAdapter);
        }

        confirmChoicesButton = findViewById(R.id.confirm_choices_button);
        if (confirmChoicesButton != null) {
            confirmChoicesButton.setOnClickListener(v -> handleConfirmChoices());
        }

        resultsList = findViewById(R.id.results_list);
        resultsLoadingView = findViewById(R.id.results_loading_view);
        resultsEmptyView = findViewById(R.id.results_empty_view);
        resultsStatusText = findViewById(R.id.results_status_text);
        if (resultsList != null) {
            resultsList.setLayoutManager(new LinearLayoutManager(this));
            resultsAdapter = new MatchResultsAdapter(this::openRecipeDetail);
            resultsList.setAdapter(resultsAdapter);
        }
    }

    private void updateSelectedList() {
        if (ingredientAdapter != null && selectedAdapter != null) {
            var selected = ingredientAdapter.getSelectedIngredients();
            selectedAdapter.setItems(selected);
            
            if (selectedCountText != null) {
                selectedCountText.setText(String.valueOf(selected.size()));
            }
        }
        refreshAvailableList();
    }

    private void handleBackPressed() {
        if (resultsContainer != null && resultsContainer.getVisibility() == View.VISIBLE) {
            showMainContent();
        } else {
            finish();
        }
    }

    private void showMainContent() {
        if (mainContent != null) mainContent.setVisibility(View.VISIBLE);
        if (resultsContainer != null) resultsContainer.setVisibility(View.GONE);
    }

    private void showResults() {
        if (mainContent != null) mainContent.setVisibility(View.GONE);
        if (resultsContainer != null) resultsContainer.setVisibility(View.VISIBLE);
    }

    private void handleConfirmChoices() {
        if (ingredientAdapter == null) {
            return;
        }

        var selectedIngredients = ingredientAdapter.getSelectedIngredients();
        if (selectedIngredients.isEmpty()) {
            return;
        }

        showResults();
        showResultsLoading(true);
        setResultsStatusText("Searching for recipes...");

        List<Recipe_Position> positions = new ArrayList<>();
        StringBuilder debugIds = new StringBuilder();
        for (var entry : selectedIngredients.entrySet()) {
            Ingredient ingredient = entry.getKey();
            int quantity = entry.getValue();
            Recipe_Position position = new Recipe_Position();
            position.setIngredientId(ingredient.getId());
            position.setAmount(quantity);
            positions.add(position);
            if (debugIds.length() > 0) {
                debugIds.append(", ");
            }
            debugIds.append(ingredient.getName()).append("#").append(ingredient.getId());
        }
        Log.d(TAG, "Search ingredient IDs: " + debugIds);

        MethodsToApi api = ApiAccess.getClient().create(MethodsToApi.class);
        Call<List<Recipe>> call = api.searchRecipes("search", positions);
        call.enqueue(new Callback<List<Recipe>>() {
            @Override
            public void onResponse(Call<List<Recipe>> call, Response<List<Recipe>> response) {
                showResultsLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    List<Recipe> results = response.body();
                    if (resultsAdapter != null) {
                        resultsAdapter.setItems(results);
                    }
                    boolean isEmpty = results == null || results.isEmpty();
                    showResultsEmpty(isEmpty);
                    if (isEmpty) {
                        setResultsStatusText("0 results");
                    } else {
                        setResultsStatusText("Found " + results.size() + " recipes");
                    }
                } else {
                    showResultsEmpty(true);
                    setResultsStatusText("Технические неполадки");
                }
            }

            @Override
            public void onFailure(Call<List<Recipe>> call, Throwable t) {
                showResultsLoading(false);
                showResultsEmpty(true);
                setResultsStatusText("Технические неполадки");
            }
        });
    }

    private void loadIngredientsFromAPI() {
        showLoading(true);
        Log.d(TAG, "Starting API call for ingredients");

        MethodsToApi api = ApiAccess.getClient().create(MethodsToApi.class);
        Call<List<Ingredient>> call = api.getAllIngredients();

        call.enqueue(new Callback<List<Ingredient>>() {
            @Override
            public void onResponse(Call<List<Ingredient>> call, Response<List<Ingredient>> response) {
                Log.d(TAG, "API Response Code: " + response.code());
                Log.d(TAG, "API Response Message: " + response.message());

                if (response.isSuccessful() && response.body() != null) {
                    List<Ingredient> ingredients = response.body();
                    Log.d(TAG, "Received " + ingredients.size() + " ingredients from API");

                    if (ingredients.isEmpty()) {
                        Log.w(TAG, "API returned empty list");
                        loadIngredientsFromLocalDatabase();
                        return;
                    }

                    allIngredients = new ArrayList<>(ingredients);
                    ingredientsLoaded = true;

                    new Thread(() -> {
                        try {
                            ingredientDao.deleteAllIngredients();
                            ingredientDao.insertIngredients(ingredients);
                            Log.d(TAG, "Cached " + ingredients.size() + " ingredients to local DB");
                        } catch (Exception e) {
                            Log.e(TAG, "Error caching ingredients", e);
                        }

                        runOnUiThread(() -> {
                            Log.d(TAG, "Updating UI with " + ingredients.size() + " ingredients");
                            showLoading(false);
                            if (ingredientAdapter != null) {
                                ingredientAdapter.setAllIngredients(ingredients);
                                ingredientAdapter.setItems(filterOutSelected(ingredients));
                                showEmpty(false);
                                applyPrefillSelections(ingredients);
                            }
                        });
                    }).start();
                } else {
                    Log.e(TAG, "API response not successful");
                    if (response.errorBody() != null) {
                        try {
                            String errorBody = response.errorBody().string();
                            Log.e(TAG, "Error body: " + errorBody);
                        } catch (Exception e) {
                            Log.e(TAG, "Could not read error body", e);
                        }
                    }
                    showTechnicalIssue();
                    loadIngredientsFromLocalDatabase();
                }
            }

            @Override
            public void onFailure(Call<List<Ingredient>> call, Throwable t) {
                Log.e(TAG, "API call failed: " + t.getMessage(), t);
                t.printStackTrace();
                showTechnicalIssue();
                loadIngredientsFromLocalDatabase();
            }
        });
    }

    private void loadIngredientsFromLocalDatabase() {
        Log.d(TAG, "Loading from local database");
        showLoading(true);

        if (ingredientDao == null) {
            showLoading(false);
            showEmpty(true);
            return;
        }

        new Thread(() -> {
            try {
                List<Ingredient> ingredients = ingredientDao.getAllIngredients();
                Log.d(TAG, "Loaded " + ingredients.size() + " ingredients from local DB");

        allIngredients = new ArrayList<>(ingredients);
        ingredientsLoaded = true;

                runOnUiThread(() -> {
                    showLoading(false);
                    if (ingredientAdapter != null) {
                        ingredientAdapter.setAllIngredients(ingredients);
                        ingredientAdapter.setItems(filterOutSelected(ingredients));
                        if (ingredients.isEmpty()) {
                            showEmpty(true);
                        } else {
                            showEmpty(false);
                            applyPrefillSelections(ingredients);
                        }
                    }
                });
            } catch (Exception e) {
                Log.e(TAG, "Error loading from local database", e);
                runOnUiThread(() -> {
                    showLoading(false);
                    showEmpty(true);
                });
            }
        }).start();
    }

    private void showLoading(boolean show) {
        if (loadingView != null) {
            loadingView.setVisibility(show ? View.VISIBLE : View.GONE);
        }
        if (ingredientsList != null) {
            ingredientsList.setVisibility(show ? View.GONE : View.VISIBLE);
        }
        if (noIngredientsView != null) {
            noIngredientsView.setVisibility(View.GONE);
        }
    }

    private void showEmpty(boolean show) {
        if (noIngredientsView != null) {
            noIngredientsView.setVisibility(show ? View.VISIBLE : View.GONE);
        }
        if (ingredientsList != null) {
            ingredientsList.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private void showResultsLoading(boolean show) {
        if (resultsLoadingView != null) {
            resultsLoadingView.setVisibility(show ? View.VISIBLE : View.GONE);
        }
        if (resultsList != null) {
            resultsList.setVisibility(show ? View.GONE : View.VISIBLE);
        }
        if (resultsEmptyView != null) {
            resultsEmptyView.setVisibility(View.GONE);
        }
    }

    private void showResultsEmpty(boolean show) {
        if (resultsEmptyView != null) {
            resultsEmptyView.setVisibility(show ? View.VISIBLE : View.GONE);
        }
        if (resultsList != null) {
            resultsList.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private void setResultsStatusText(String text) {
        if (resultsStatusText != null) {
            resultsStatusText.setText(text);
        }
    }

    private void openRecipeDetail(Recipe recipe) {
        if (recipe == null) {
            return;
        }
        Intent intent = new Intent(this, RecipeDetailActivity.class);
        intent.putExtra("recipe_id", recipe.getId());
        startActivity(intent);
    }

    private void filterIngredients(String query) {
        if (query.trim().isEmpty()) {
            if (ingredientAdapter != null) {
                ingredientAdapter.setItems(filterOutSelected(allIngredients));
            }
        } else {
            String lowerQuery = query.toLowerCase().trim();
            List<Ingredient> filtered = new ArrayList<>();

            for (Ingredient ingredient : allIngredients) {
                if (ingredient.getName().toLowerCase().contains(lowerQuery) ||
                        (ingredient.getCategory() != null &&
                                ingredient.getCategory().toLowerCase().contains(lowerQuery))) {
                    filtered.add(ingredient);
                }
            }

            if (ingredientAdapter != null) {
                ingredientAdapter.setItems(filterOutSelected(filtered));
            }
        }
    }

    private void showTechnicalIssue() {
        Toast.makeText(this, "Технические неполадки", Toast.LENGTH_SHORT).show();
    }

    private void applyPrefillSelections(List<Ingredient> ingredients) {
        if (prefillIngredients == null || prefillIngredients.isEmpty() || ingredientAdapter == null) {
            return;
        }
        Set<String> names = new HashSet<>();
        for (String name : prefillIngredients) {
            if (name != null && !name.trim().isEmpty()) {
                names.add(name.trim().toLowerCase());
            }
        }
        if (names.isEmpty()) {
            return;
        }
        for (Ingredient ingredient : ingredients) {
            String name = ingredient.getName();
            if (name != null && names.contains(name.trim().toLowerCase())) {
                ingredientAdapter.setQuantity(ingredient, 1);
            }
        }
        updateSelectedList();
    }

    private void refreshAvailableList() {
        filterIngredients(currentQuery);
    }

    private List<Ingredient> filterOutSelected(List<Ingredient> source) {
        if (ingredientAdapter == null || source == null) {
            return source;
        }
        var selectedIds = ingredientAdapter.getSelectedIngredientIds();
        if (selectedIds.isEmpty()) {
            return source;
        }
        List<Ingredient> result = new ArrayList<>();
        for (Ingredient ingredient : source) {
            if (!selectedIds.contains(ingredient.getId())) {
                result.add(ingredient);
            }
        }
        return result;
    }
}
